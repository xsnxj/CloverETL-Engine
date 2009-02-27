package org.jetel.data;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.RuleBasedCollator;

import org.jetel.data.tape.DataRecordTape;
import org.jetel.data.tape.TapeCarousel;
import org.jetel.metadata.DataRecordMetadata;
import org.jetel.util.MiscUtils;
import org.jetel.util.SynchronizeUtils;

/**
 *  Class for external sorting of data records.<br>
 *  Incoming data are stored in in-memory buffer(s), buffers
 *  are allocated in the fly - instance of SortDataRecordInternal 
 *  is used for this. When capacity is finished, data are flushed to 
 *  the disk (each chunk is sorted).
 *  
 *  When storing phase is finished, sort method must be called - it 
 *  assures to sort and flush all in-memory data, so we are ready
 *  for merging phase.
 *  
 *  Now reading can start - during this phase, data are read
 *  from the disk from tapes and merged together.
 *  
 *  If size of incoming data is not bigger than defined limit,
 *  in-memory sorting occurs.
 *  
 *  Standard way of working with ExtSortDataRecordInternal (same as ExtSortDataRecordInternal):<br>
 *  <ol>
 *  <li>put() n-times
 *  <li>sort()
 *  <li>get() n-times
 *  </ol>
 *@author     dpavlis, jlehotsky
 *@see	      org.jetel.data.RecordKey
 */

public class ExternalSortDataRecord implements ISortDataRecord {

	private boolean doMerge = false;
	private InternalSortDataRecord sorter;
	private TapeCarousel tapeCarousel;
	private boolean carouselInitialized;
	private int numberOfTapes;
	private String[] tmpDirs;
	private String[] sortKeysNames;
	private boolean[] sortOrderings;
	private RecordOrderedKey sortKey;
	DataRecordMetadata inMetadata;
	private ByteBuffer recordBuffer;
	private boolean[] sourceRecordsFlags;
	private DataRecord[] sourceRecords;
	private String localeStr;
	private RuleBasedCollator collator;
	int prevIndex;
	
	public ExternalSortDataRecord() {
		super();
        carouselInitialized = false;
	}

	/**
	 * Constructor for the ExtSortDataRecordInternal
	 * 
	 * @param metadata	Metadata describing records stored in internal buffer
	 * @param keyItems	Names of fields which compose the key used for sorting data
	 * @param sortAscending	True if required sort order is Ascending, otherwise False
	 * @param internalBufferCapacity Internal maximum capacity of a buffer
	 * @param numberOfTapes	Number of tapes to be used
	 * @param tmpDirs	List of names of temporary directories to be used for external sorting buffer on disk
	 * @param localeStr	String name of locale to use for collation. If null, no collator is used
	 */
	public ExternalSortDataRecord(DataRecordMetadata metadata, String[] keyItems, boolean[] sortOrderings, int internalBufferCapacity,
			int numberOfTapes, String[] tmpDirs) {
		this(metadata, keyItems, sortOrderings, internalBufferCapacity, numberOfTapes, tmpDirs, null);
	}
	
	public ExternalSortDataRecord(DataRecordMetadata metadata, String[] keyItems, boolean[] sortOrderings, int internalBufferCapacity,
			int numberOfTapes, String[] tmpDirs, String localeStr) {
	
		this.sortKeysNames = keyItems;		
		this.sortOrderings = sortOrderings;
		this.numberOfTapes = numberOfTapes;
		this.tmpDirs = tmpDirs;
		this.prevIndex = -1;
		this.localeStr = localeStr;
		
		inMetadata = metadata;
		
		if (internalBufferCapacity>0){	
            sorter = new InternalSortDataRecord(metadata, keyItems, sortOrderings, false, internalBufferCapacity);
        } else {
            sorter = new InternalSortDataRecord(metadata, keyItems, sortOrderings, false);
        }
		if (this.localeStr != null) {
			sorter.setUseCollator(true);
			sorter.setCollatorLocale(this.localeStr);
			this.collator = (RuleBasedCollator) RuleBasedCollator.getInstance(MiscUtils.createLocale(this.localeStr));
		}
		
		recordBuffer = ByteBuffer
        	.allocateDirect(Defaults.Record.MAX_RECORD_SIZE);
		
		if (recordBuffer == null) {
			throw new RuntimeException("Can NOT allocate internal record buffer ! Required size:"
                    + Defaults.Record.MAX_RECORD_SIZE);
		}
        
	}

	/* (non-Javadoc)
	 * @see org.jetel.data.ISortDataRecordInternal#put(org.jetel.data.DataRecord)
	 */
	public boolean put(DataRecord record) throws IOException, InterruptedException {
		if (!sorter.put(record)) {
			// we need to sort & flush buffer on to tape and merge it
			// later
			doMerge = true;
			sorter.sort();
			flushToTapeSynchronously();			
			sorter.reset();
			if (!sorter.put(record)) {
				throw new RuntimeException(
						"Can't store record into sorter !");
			}			
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.jetel.data.ISortDataRecordInternal#sort()
	 */
	public void sort() throws IOException, InterruptedException {
		if (doMerge) {
			// sort whatever remains in sorter
			sorter.sort();
			flushToTapeSynchronously();		
			phaseMerge();
		} else {
			sorter.sort();
			sorter.rewind();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.jetel.data.ISortDataRecordInternal#get()
	 */
	public DataRecord get() throws IOException, InterruptedException {		
		
		int index;
		
		if (doMerge) {

			if (prevIndex > -1) {
				if (!tapeCarousel.getTape(prevIndex).get(sourceRecords[prevIndex])) {
	                sourceRecordsFlags[prevIndex] = false;
	            }
			}
			
	        if (hasAnyData(sourceRecordsFlags)) {
	        	
	            index = getLowestIndex(sourceRecords, sourceRecordsFlags);

	            prevIndex = index;
	            
	            SynchronizeUtils.cloverYield();
	            return sourceRecords[index];
	        } else { 
				tapeCarousel.free();
				carouselInitialized = false;
				return null;
			}
		} else {			
			return sorter.get();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.jetel.data.ISortDataRecordInternal#get(java.nio.ByteBuffer)
	 */
	public boolean get(ByteBuffer recordDataBuffer) throws IOException, InterruptedException {		
		DataRecord record=get();
		if (record!=null){
		    record.serialize(recordDataBuffer);
		    recordDataBuffer.flip();
		    return true;
		}else{
		    return false;
		}		
	}

	public void reset() {
		sorter.reset();
		if (carouselInitialized && tapeCarousel != null) {
			try {
				tapeCarousel.rewind();
			} catch (Exception e) {
				carouselInitialized = false;
				tapeCarousel = null;
			}
		}
		recordBuffer.clear();
		this.prevIndex = -1;

	}
	
	/* (non-Javadoc)
	 * @see org.jetel.data.ISortDataRecordInternal#free()
	 */
	public void free() throws InterruptedException {
		if (carouselInitialized && (tapeCarousel!=null)) {
			tapeCarousel.free(); // this shouldn't happen if component exited in clean way
		}
		sorter.free();
	}	
	
	
	private void flushToTapeSynchronously() throws IOException, InterruptedException {
        DataRecordTape tape;
        if (!carouselInitialized) {
            tapeCarousel = new TapeCarousel(numberOfTapes, tmpDirs);
            tapeCarousel.open();
            tape = tapeCarousel.getFirstTape();
            carouselInitialized = true;
        } else {
            tape = tapeCarousel.getNextTape();
            if (tape == null)
                tape = tapeCarousel.getFirstTape();
        }

        tape.addDataChunk();

        sorter.rewind();
        
        // --- read sorted records
        while (sorter.get(recordBuffer)) {
            tape.put(recordBuffer);
            recordBuffer.clear();
        }
        tape.flush(false);
    }
	
	/**
     * Performs merge of partially sorted data records stored on tapes 
     * (in external files).
     * 
     * @throws IOException
     * @throws InterruptedException
     */
    private void phaseMerge() throws IOException, InterruptedException {
        int index;
        DataRecordTape targetTape;
        TapeCarousel targetCarousel = new TapeCarousel(tapeCarousel.numTapes(), tmpDirs);
        sourceRecords = new DataRecord[tapeCarousel.numTapes()];
        sourceRecordsFlags = new boolean[tapeCarousel.numTapes()];

        // initialize sort key which will be used when merging data
        sortKey = new RecordOrderedKey(sortKeysNames, sortOrderings, inMetadata, collator);
        sortKey.init();

        // initial creation & initialization of source records
        for (int i = 0; i < sourceRecords.length; i++) {
            sourceRecords[i] = new DataRecord(inMetadata);
            sourceRecords[i].init();
        }

        // rewind carousel with source data - so we can start reading it
        tapeCarousel.rewind();
        // open carousel into which we will merge data
        targetCarousel.open();
        // get first free tape from target carousel
        targetTape = targetCarousel.getFirstTape();

        /* 
         * MAIN MERGING loop
         */
        do {
            // if we need to perform only final merging (one lewel of chunks on source tapes)
            // skip to final merge
            if (tapeCarousel.getFirstTape().getNumChunks()==1) break;
            /*
             * semi-merging of one level of data chunks
             */
            do {
                loadUpRecords(tapeCarousel, sourceRecords, sourceRecordsFlags);
                if (hasAnyData(sourceRecordsFlags)) {
                    targetTape.addDataChunk();
                } else {
                    break;
                }
                while (hasAnyData(sourceRecordsFlags)) {
                    index = getLowestIndex(sourceRecords,
                                sourceRecordsFlags);
                    // write record to target tape
                    recordBuffer.clear();
                    sourceRecords[index].serialize(recordBuffer);
                    recordBuffer.flip();
                    targetTape.put(recordBuffer);
                    // read in next record from tape from which we read last
                    // record
                    if (!tapeCarousel.getTape(index).get(sourceRecords[index])) {
                        sourceRecordsFlags[index] = false;
                    }
                    SynchronizeUtils.cloverYield();
                }
                targetTape.flush(false);
                targetTape = targetCarousel.getNextTape();
                if (targetTape == null)
                    targetTape = targetCarousel.getFirstTape();
            } while (hasMoreChunks(tapeCarousel));
            // switch source tapes and target tapes, then continue with merging
            targetCarousel.rewind();
            tapeCarousel.clear();
            TapeCarousel tmp = tapeCarousel;
            tapeCarousel = targetCarousel;
            targetCarousel = tmp;
            targetTape = targetCarousel.getFirstTape();

        } while (tapeCarousel.getFirstTape().getNumChunks() > 1);

        // we don't need target carousel - merged records will be sent to output port
        targetCarousel.free();

        // DEBUG START
//        if (logger.isDebugEnabled()) {
//		    logger.debug("*** Merged data: ***");
//		    logger.debug("****** FINAL TAPE CAROUSEL REVIEW ***********");
//		
//		    DataRecordTape tape = tapeCarousel.getFirstTape();
//		    while (tape != null) {
//		    	logger.debug(tape);
//		        tape = tapeCarousel.getNextTape();
//		    }
//        }
        // DEBUG END
        
        /* 
         * send data to output - final merge
         */
        tapeCarousel.rewind();
        loadUpRecords(tapeCarousel, sourceRecords, sourceRecordsFlags);
        
    }
    
    /**
     * Returns index of the lowest record from the specified record array
     * 
     * @param sourceRecords array of source records
     * @param flags array indicating which source records contain valid data
     * @return index of the lowest record within source records array of -1 if no such record
     * exists - i.e. there is no valid record
     */
    private final int getLowestIndex(DataRecord[] sourceRecords, boolean[] flags) {
        int lowest = -1;
        for (int i = 0; i < flags.length; i++) {
            if (flags[i]) {
                lowest = i;
                break;
            }
        }
        for (int i = lowest + 1; i < sourceRecords.length; i++) {
            if (flags[i]
                    && sortKey.compare(sourceRecords[lowest], sourceRecords[i]) == 1) {
                lowest = i;
            }
        }
        return lowest;
    }
    
    /**
     * Populates source records array with records from individual tapes (included in
     * tape carousel). Sets flags in flags array for those records which contain valid data.
     * 
     * @param tapeCarousel
     * @param sourceRecords
     * @param sourceRecordsFlags
     * @throws IOException
     * @throws InterruptedException 
     */
    private final void loadUpRecords(TapeCarousel tapeCarousel,
            DataRecord[] sourceRecords, boolean[] sourceRecordsFlags)
            throws IOException, InterruptedException {
        for (int i = 0; i < tapeCarousel.numTapes(); i++) {
            DataRecordTape tape = tapeCarousel.getTape(i);
            if (tape.get(sourceRecords[i])) {
                sourceRecordsFlags[i] = true;
            } else {
                sourceRecordsFlags[i] = false;
            }
        }
    }

    /**
     * Checks whether tapes within tape carousel contains more data chunks
     * to be processed.
     * @param tapeCarousel
     * @return true if more chunks are available
     * @throws InterruptedException 
     * @throws IOException 
     */
    private final static boolean hasMoreChunks(TapeCarousel tapeCarousel) throws InterruptedException, IOException {
        boolean hasMore = false;
        for (int i = 0; i < tapeCarousel.numTapes(); i++) {
            if (tapeCarousel.getTape(i).nextDataChunk()) {
                hasMore = true;
            }
        }
        return hasMore;
    }
    
    /**
     * Checks that at least one valid record exists
     * @param flags
     * @return true if flahs indicate that at least one valid record exists
     */
    private final static boolean hasAnyData(boolean[] flags) {
        for (int i = 0; i < flags.length; i++) {
            if (flags[i] == true)
                return true;
        }
        return false;
    }

}
