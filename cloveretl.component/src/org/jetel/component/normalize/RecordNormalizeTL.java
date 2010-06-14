/*
 * jETeL/CloverETL - Java based ETL application framework.
 * Copyright (c) Javlin, a.s. (info@cloveretl.com)
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jetel.component.normalize;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.jetel.component.AbstractTransformTL;
import org.jetel.data.DataRecord;
import org.jetel.data.primitive.CloverInteger;
import org.jetel.exception.ComponentNotReadyException;
import org.jetel.exception.JetelException;
import org.jetel.exception.TransformException;
import org.jetel.graph.TransformationGraph;
import org.jetel.interpreter.data.TLBooleanValue;
import org.jetel.interpreter.data.TLNumericValue;
import org.jetel.interpreter.data.TLValue;
import org.jetel.interpreter.data.TLValueType;
import org.jetel.metadata.DataRecordMetadata;

/**
 * Implements normalization based on TransformLang source specified by user. User defines following functions (asterisk
 * denotes the mandatory ones):
 * <ul>
 * <li>* function count()</li>
 * <li>* function transform(idx)</li>
 * <li>function init()</li>
 * <li>function finished()</li>
 * </ul>
 * 
 * @author Jan Hadrava (jan.hadrava@javlinconsulting.cz), Javlin Consulting (www.javlinconsulting.cz)
 * @since 11/21/06
 * @see org.jetel.component.Normalizer
 */
public class RecordNormalizeTL extends AbstractTransformTL implements RecordNormalize {

	private static final String LENGTH_FUNCTION_NAME = "count";
	private static final String TRANSFORM_FUNCTION_NAME = "transform";
	private static final String CLEAN_FUNCTION_NAME = "clean";

	private int lenghtFunctionIdentifier;
	private int transformFunctionIdentifier;
	private int cleanFunctionIdentifier;

	private TLValue[] counterTL;
	private DataRecord[] sourceRec;
	private DataRecord[] targetRec;

	/** Constructor for the DataRecordTransform object */
	public RecordNormalizeTL(Log logger, String srcCode, TransformationGraph graph) {
		super(srcCode, logger);

		counterTL = new TLValue[] { new TLNumericValue<CloverInteger>(TLValueType.INTEGER, new CloverInteger(0)) };
		sourceRec = new DataRecord[1];
		targetRec = new DataRecord[1];
	}

	public boolean init(Properties parameters, DataRecordMetadata sourceMetadata, DataRecordMetadata targetMetadata)
			throws ComponentNotReadyException {
		wrapper.setMetadata(new DataRecordMetadata[] { sourceMetadata }, new DataRecordMetadata[] { targetMetadata });
		wrapper.setParameters(parameters);
        wrapper.setGraph(getGraph());
		wrapper.init();

		TLValue result = null;

		try {
			result = wrapper.execute(INIT_FUNCTION_NAME, null);
		} catch (JetelException e) {
			// do nothing: function init is not necessary
		}

		lenghtFunctionIdentifier = wrapper.prepareFunctionExecution(LENGTH_FUNCTION_NAME);
		transformFunctionIdentifier = wrapper.prepareFunctionExecution(TRANSFORM_FUNCTION_NAME);

		try {
			cleanFunctionIdentifier = wrapper.prepareFunctionExecution(CLEAN_FUNCTION_NAME);
		} catch (Exception ex) {
			// do nothing
			cleanFunctionIdentifier = -1;
		}

		return result == null ? true : result == TLBooleanValue.TRUE;
	}

	public int count(DataRecord source) {
		TLValue value = wrapper.executePreparedFunction(lenghtFunctionIdentifier, source, null);

		if (value.type.isNumeric()) {
			return ((TLNumericValue<?>) value).getInt();
		}

		throw new RuntimeException("Normalizer - count() functions does not return integer value !");
	}

	public int transform(DataRecord source, DataRecord target, int idx) throws TransformException {
		// set the error message to null so that the getMessage() method works correctly if no error occurs
		errorMessage = null;

		counterTL[0].getNumeric().setValue(idx);
		sourceRec[0] = source;
		targetRec[0] = target;

		TLValue result = wrapper.executePreparedFunction(transformFunctionIdentifier, sourceRec, targetRec, counterTL);

		if (result == null || result == TLBooleanValue.TRUE) {
			return 0;
		}

		if (result.getType().isNumeric()) {
			return result.getNumeric().getInt();
		}

		errorMessage = "Unexpected return result: " + result.toString() + " (" + result.getType().getName() + ")";

		return -1;
	}

	public void clean() {
		if (cleanFunctionIdentifier != -1) {
			wrapper.executePreparedFunction(cleanFunctionIdentifier);
		}
	}

}
