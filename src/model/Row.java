/*********************************************************************\
 * Row.java - xmlCam G-Code Generator                               *
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

import java.util.ArrayList;

/**
 * This class represents a line in a G-Code row.
 * That means that n fields part of the line. The last part is a comment represented by a String object.
 * @author Christian Kirsch
 *
 */

public class Row {

	private ArrayList<Field> commands;
	private String comment;

	/**
	 * Constructs a empty line.
	 */
	public Row() {
		this.commands = new ArrayList<Field>();
		this.comment = new String();
	}
	
	/**
	 * Constructs a line object adds a field.
	 * @param field The field to be added
	 */
	public Row(Field field) {
		this();
		this.commands.add(field);
	}
	
	/**
	 * Constructs a line 
	 * @param command
	 */
	/*	public Line(ArrayList<Field> command) {
		this.commands = command;
	}*/
	
	/**
	 * Adds a field at the end of the line.
	 * @param field The new field
	 */
	protected void addField(Field field) {
		commands.add(field);
	}
	
	/**
	 * Returns the field at the specified position.
	 * @param index The index of the field
	 * @return The field at the position
	 */
	public Field getField(int index) {
		return commands.get(index);
	}
	
	/**
	 * Replaces the field at the specified position with a new field.
	 * @param index The index of the field to replace
	 * @param field The new field
	 */
	protected void setField(int index, Field field) {
		commands.set(index, field);
	}
	
	/**
	 * Removes the field at the specified position.
	 * @param index The index of the field to be removed
	 */
	protected void removeField(int index) {
		commands.remove(index);
	}
	
	/**
	 * Returns the amount of fields in this line.
	 * @return The amount
	 */
	public int size( ) {
		return commands.size();
	}
	
	/**
	 * Returns the comment of the line.
	 * @return The comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * Sets the comment for the line.
	 * @param comment The comment to be set
	 */
	protected void setComment(String comment) {
		this.comment = comment;
	}
	
	/**
	 * Searches in line for a field with command character c.
	 * The parameter c is the needle, the this-object is the haystack. If nothing found, the return value will be -1;
	 * @param c The needle
	 * @return The index of the field
	 */
	public int getFieldIndex(char c) {
		for(int i = 0; i < commands.size(); i++) {
			if(commands.get(i).getLetter() == c) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Returns the complete line as a String.
	 */
	public String toString() {
		String out = new String();
		for(int i = 0; i < commands.size(); i++) {
			out += commands.get(i) + " ";
		}
		if(!comment.isEmpty()) {
			out += "; " + comment;
		}
		return out;
	}
	
}
