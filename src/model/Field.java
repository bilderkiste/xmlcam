/*********************************************************************\
 * Generator.java - xmlCam G-Code Generator                          *
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
import java.math.MathContext;

/**
 * This class represents a G-Code field like G0.
 * A field could be only a character or character with a number. These fields are usually organized in lines. An example is for minimum homing G161 X Y Z F1800.
 * G161 is the command, X Y Z the affected axis and F1800 the feedrate for the home moves. 
 * @author Christian Kirsch
 */

public class Field {

	private char letter;
	private boolean hasNumber;
	private BigDecimal number;
	
	public Field() {
		this.setLetter('X');
		this.number = new BigDecimal(0);
		this.hasNumber = false;	
	}
	
	public Field(char letter) {
		this.setLetter(letter);
		this.hasNumber = false;	
	}
	
	public Field(char letter, BigDecimal number) {
		this.setLetter(letter);
		this.hasNumber = true;
		// Check if number has decimal places. If yes then round them to 8 decimal places.
		if(number.scale() > 0) {
			double value = number.round(new MathContext(8)).doubleValue();
			this.number = new BigDecimal(value);
		} else {
			this.number= number;
		}
	}
	
	/**
	 * Returns the letter of the field (i.e. G, F).
	 * @return
	 */
	public char getLetter() {
		return letter;
	}

	/**
	 * Sets the letter of the field.
	 * @param letter the letter
	 */
	protected void setLetter(char letter) {
		this.letter = Character.toUpperCase(letter);
	}

	/**
	 * Returns the number from a field.
	 * @return the number
	 */
	public BigDecimal getNumber() {
		if(!hasNumber) {
			return new BigDecimal(0);
		}
		return number;
	}

	/**
	 * Sets the number of the field.
	 * @param number the number
	 */
	protected void setNumber(BigDecimal number) {
		this.number = number;
		this.hasNumber = true;
	}
	
	/**
	 * Removes the number of a field.
	 */
	protected void removeNumber() {
		this.hasNumber = false;
	}

	/**
	 * Checks if the field has a number.
	 * @return true, if the field has a number, otherwise false
	 */
	public boolean hasNumber() {
		return this.hasNumber;
	}
	
	/**
	 * Output of a field (i.e. Z4.839).
	 * If the float value has no decimal place the output is i.e. G0 instead of G0.0.
	 */
	@Override
	public String toString() {
		if(hasNumber) {
			if(number.doubleValue() % 1 != 0) { // check if decimal place is greater than 0
				return new String() + letter + number.doubleValue();
			} else {
				return new String() + letter + number.intValue();
			}
		} else {
			return new String() + letter;
		}
	}
	
}
