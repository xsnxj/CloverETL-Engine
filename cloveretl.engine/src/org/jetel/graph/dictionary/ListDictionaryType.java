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
package org.jetel.graph.dictionary;

import java.util.List;

import org.jetel.ctl.data.TLType;
import org.jetel.exception.ComponentNotReadyException;

/**
 * List dictionary type. 
 * Formatting and parsing Properties is not supported.
 * 
 * In order for CTL to work, content type must be set
 * for the dictionary entries. The content type
 * should be one of the names of primitive CTL datatypes. 
 * 
 * @author krivanekm (info@cloveretl.com)
 *         (c) Javlin, a.s. (www.cloveretl.com)
 *
 * @created 27.1.2012
 */
public class ListDictionaryType extends DictionaryType {

	public static final String TYPE_ID = "list";
	
	private final TLType listType = TLType.createList(null);

	/**
	 * Constructor.
	 */
	public ListDictionaryType() {
		super(TYPE_ID, List.class);
	}
	
	/* (non-Javadoc)
	 * @see org.jetel.graph.dictionary.DictionaryType#init(java.lang.Object, org.jetel.graph.dictionary.Dictionary)
	 */
	@Override
	public Object init(Object value, Dictionary dictionary) throws ComponentNotReadyException {
		return value;
	}
	
	/* (non-Javadoc)
	 * @see org.jetel.graph.dictionary.IDictionaryType#isValidValue(java.lang.Object)
	 */
	@Override
	public boolean isValidValue(Object value) {
		return (value == null) || (value instanceof List);
	}

	@Override
	public TLType getTLType() {
		return listType;
	}
	
}