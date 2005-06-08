/*
 *    jETeL/Clover - Java based ETL application framework.
 *    Copyright (C) 2002-05  David Pavlis <david_pavlis@hotmail.com> and others.
 *    
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *    
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU    
 *    Lesser General Public License for more details.
 *    
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * Created on 31.5.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.jetel.data.sequence;

import org.jetel.exception.JetelException;

/**
 * @author david
 * @since  31.5.2005
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface Sequence {
    
    /**
     * Method with returns sequence�s name (ID). It is deemed that all sequences are
     * kept in directory and accessed by client's transformations by their names.
     * @return sequence name 
     */
    public String getName();
    
    /**
     * @return current value of the sequence
     */
    public int currentValueInt();
    
    /**
     * Calculates next value of the sequence (based on current value and defined step) and
     * returns it. In general, it is not thread safe.
     * @return next value
     */
    public int nextValueInt();
    
    public long currentValueLong();
    
    public long nextValueLong();
    
    public String currentValueString();
    
    public String nextValueString();
    
    /**
     * Set the current sequence value to the defined start value;
     */
    public void reset();

    /**
     * Informs whether the sequence is persistent - i.e. whether it keeps its value
     * between different class instances. 
     * @return true/false (is/is not persistent)
     */
    public boolean isPersistent();
    
    /**
     * Initializes sequence object. It is called after the sequence class is instantiated.
     * All necessary internal initialization should be performed in this method.
     */
    public void init() throws JetelException;
    
    /**
     * Closes the sequence (current instance). All internal resources should be freed in
     * this method.
     */
    public void close();
}
