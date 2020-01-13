/*********************************************************************\
 * GCodeHelpers.java - xmlCam G-Code Generator                       *
 * Copyright (C) 2020, Christian Kirsch                              *
 *                                                                   *
 * This program is free software; you can redistribute it and/or     *
 * modify it under the terms of the GNU General Public License as    *
 * published by the Free Software Foundation; either version 3 of    *
 * the License, or (at your option) any later version.               *
 *                                                                   *
 * This program is distributed in the hope that it will be useful,   *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of    *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the     *
 * GNU General Public License for more details.                      *
 *                                                                   *
 * You should have received a copy of the GNU General Public License *
 * along with this program; if not, write to the Free Software       *
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.         *
\*********************************************************************/

package model;

import java.math.BigDecimal;

/**
 * This class offers methods for common G-Code operations.
 * @author Christian Kirsch
 *
 */

public class GCodeHelpers {
	
	/**
	 * Parses a field from a String (i.e. Z, Z0, Z0.0 ...) and returns a Field object.
	 * @param field The String with the field.
	 * @return The Field object
	 * @throws NumberFormatException If second part of field is not a number.
	 * @throws IllegalArgumentException If first part of field is not a character.
	 */
	public static Field parseField(String field) throws NumberFormatException, IllegalArgumentException {
		Field fieldObject;
		
		if(field.isEmpty()) {
			return null;
		}
		
		// Has the field only a character or some float value behind?
		if(field.length() > 1) {
			fieldObject = new Field(field.charAt(0), new BigDecimal(field.substring(1)));
		} else {
			fieldObject = new Field(field.charAt(0));
		}
		
		if(!Character.isLetter(fieldObject.getLetter())) {
			throw new IllegalArgumentException("First argument must be a letter.");
		}
		
		return fieldObject;
	}
	
	/**
	 * Checks if a string is a valid field.
	 * @param field The field to be checked.
	 * @return True if valid.
	 */	
	public static boolean isFieldValid(String field) {
		try {
			parseField(field);
			return true;
		} catch(NumberFormatException e) {
			return false;
		} catch(IllegalArgumentException e) {
			return false;
		}
	}

}
