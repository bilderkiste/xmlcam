/*********************************************************************\
 * Program.java - xmlCam G-Code Generator                            *
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Scanner;

import view.ProgramModelListener;

/**
 * This class represents the structure of the ProgramModel.
 * 
 * A program consists a various number of lines (0..n).
 * A line consists a various number of fields and could have a comment.
 * A field has always a letter and it could have a number (i.e. G0, X, Y-20.2039, F1000)
 * The comments start always with a semikolon.
 * <pre>
 * Programm		field_0	field_1	field_2	...	field_m	comment
 * line_0		G0 	X13.31	Y10	...		F30	;Move to position X 13.31 and Y 10 with a feed rate 30 mm/s. 
 * line_1		G0	Z0.5 
 * ...
 * line_n								;End of program
 * </pre>
 * 
 * @author Christian Kirsch
 *
 */
public class Program {

	private ArrayList<Line> lines;
	private ArrayList<ProgramModelListener> listener;

	/**
	 * Constructs a empty program.
	 */
	public Program() {
		this.lines = new ArrayList<Line>();
		this.listener = new ArrayList<ProgramModelListener>();
	}

	/**
	 * Adds a new line at the end of the program.
	 * @param line The new Line object
	 */
	public void addLine(Line line) {
		lines.add(line);
		fireModelChanged();
	}
	
	/**
	 * Adds a new line to the program after the specific position.
	 * @param line The new Line object
	 * @param rowIndex The index where to insert
	 */
	public void addLine(Line line, int rowIndex) {
		lines.add(rowIndex, line);
		fireModelChanged();
	}
	
	/**
	 * Get the program line from rowIndex.
	 * @param rowIndex number of line
	 * @return The Line object
	 */
	public Line getLine(int rowIndex) {
		return lines.get(rowIndex);
	}
	
	/**
	 * Replaces a Line object at rowIndex.
	 * @param rowIndex Line number.
	 * @param line The new Line object.
	 */
	public void setLine(int rowIndex, Line line) {
		lines.set(rowIndex, line);
		fireModelChanged();
	}
	
	/**
	 * Removes the Line Object at rowIndex.
	 * @param rowIndex The index where to delete
	 */
	public void removeLine(int rowIndex) {
		lines.remove(rowIndex);
		fireModelChanged();
	}
	
	/**
	 * Removes an amount of lines. First line to delete is rowIndex.
	 * @param rowIndex The row index
	 * @param amount The amount
	 */
	public void removeLines(int rowIndex, int amount) {
		for(int i = 0; i < amount; i++) {
			lines.remove(rowIndex);
		}
		fireModelChanged();
	}
	
	/**
	 * Removes all selected lines in the collection.
	 * @param rowCollection Integer-array with all row numbers
	 */
	public void removeLines(int[] rowCollection) {
		for(int i = rowCollection.length - 1; i >= 0; i--) {
			lines.remove(rowCollection[i]);
		}
		fireModelChanged();
	}
	
	/**
	 * Sets a new Comment on Line Object at rowIndex
	 * @param rowIndex Line number.
	 * @param comment The new comment.
	 */
	public void setComment(int rowIndex, String comment) {
		lines.get(rowIndex).setComment(comment);
		fireModelChanged();
	}
	
	/**
	 * Replaces the field at position rowIndex, columnIndex.
	 * @param rowIndex line number
	 * @param columnIndex field number
	 * @param field the new Field
	 */
	public void setField(int rowIndex, int columnIndex, Field field) {
		lines.get(rowIndex).setField(columnIndex, field);
		fireModelChanged();
	}
	
	/**
	 * Adds a new field at Line rowIndex.
	 * @param rowIndex Line number.
	 * @param field The new Field.
	 */
	public void addField(int rowIndex, Field field) {
		lines.get(rowIndex).addField(field);
		fireModelChanged();
	}
	
	/**
	 * Adds a new field at the last line of the program.
	 * @param field The new Field.
	 */
	public void addField(Field field) {
		lines.get(lines.size() - 1).addField(field);
		fireModelChanged();
	}
	
	/**
	 * Removes a field at rowIndex, ColumnIndex.
	 * @param rowIndex
	 * @param columnIndex
	 */
	public void removeField(int rowIndex, int columnIndex) {
		lines.get(rowIndex).removeField(columnIndex);
		fireModelChanged();
	}
	
	/**
	 * Sets a command of a field
	 * @param rowIndex Line number.
	 * @param columnIndex Field number.
	 * @param letter The new command letter.
	 */
	public void setLetter(int rowIndex, int columnIndex, char letter) {
		lines.get(rowIndex).getField(columnIndex).setLetter(letter);
		fireModelChanged();
	}
	
	/**
	 * Sets a number of a field
	 * @param rowIndex Line number.
	 * @param columnIndex Field number.
	 * @param number The new command number.
	 */
	public void setNumber(int rowIndex, int columnIndex, BigDecimal number) {
		lines.get(rowIndex).getField(columnIndex).setNumber(number);
		fireModelChanged();
	}
	
	/**
	 * Removes the number of the field. Changes i.e. Z32.1 to Z.
	 * @param rowIndex Line number.
	 * @param columnIndex Field number.
	 */
	public void removeNumber(int rowIndex, int columnIndex) {
		lines.get(rowIndex).getField(columnIndex).removeNumber();
		fireModelChanged();
	}
	
	/**
	 * Returns the maximum size of fields in a line in the program.
	 * @return maximum size;
	 */
	public int getFieldSize() {
		int maxSize = 0;
		for(int i = 0; i < lines.size(); i++) {
			if(lines.get(i).size() > maxSize) {
				maxSize = lines.get(i).size();
			}
		}
		return maxSize;
	}
	
	/**
	 * Return the size of lines in the program.
	 * @return line size;
	 */
	public int getLineSize() {
		return lines.size();
	}
	
	/**
	 * Clears the whole program.
	 */
	public void clear() {
		this.lines = new ArrayList<Line>();
		fireModelChanged();
	}
	
	/**
	 * Register a ProgramModelListener to this model.
	 * @param listener Listener to be registered.
	 */
	public void addProgrammModelListener(ProgramModelListener listener) {
		this.listener.add(listener);
	}
	
	/**
	 * Saves the model as a G-Code text file.
	 * @param filename Filename of the file.
	 * @throws IOException
	 */
	public void writeToFile(String filename) throws IOException {
		File file = new File(filename);
		this.writeToFile(file);
	}
	
	/**
	 * Saves the model as a G-Code text file.
	 * @param file File-Object.
	 * @throws IOException
	 */
	public void writeToFile(File file) throws IOException {
		file.createNewFile();
		FileWriter filewriter = new FileWriter(file);
		filewriter.write(this.toString());
		filewriter.flush();
		filewriter.close();
	}
	
	/**
	 * Reads from a G-Code text file and load it into the model.
	 * @param filename Filename of the file.
	 * @throws IOException
	 */
	public void readFromFile(String filename) throws IOException {
		File file = new File(filename);
		this.readFromFile(file);
	}
	
	/**
	 * Reads from a G-Code text file and load it into the model.
	 * @param file File-Object..
	 * @throws IOException
	 */
	public void readFromFile(File file) throws IOException {
		Field field;
		Line line;
		String lineBuffer;
		int index, lineIndex = 0;
		
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		
		while((lineBuffer = bufferedReader.readLine()) != null) {
		
			line = new Line();
			
			// Read comments
			index = lineBuffer.indexOf(';');
			if(index > -1) {      // Found a comment
				line.setComment(lineBuffer.substring(index + 1).trim());
				lineBuffer = lineBuffer.substring(0, index);   // Cut comment
			}
			
			Scanner textLine = new Scanner(lineBuffer);    // Split into fields
			
			while(textLine.hasNext()) {
				String fieldBuffer = new String(textLine.next());
				
				try {						
					field = GCodeHelpers.parseField(fieldBuffer);
					line.addField(field);
				} catch(NumberFormatException e) {
					System.out.println("Line " + lineIndex + ": " + e + "; Could not read parameter. Field skipped.");
				} catch(IllegalArgumentException e) {
					System.out.println("Line " + lineIndex + ": " + e + "; Could not read parameter. Field skipped.");
				}
			}	
		
			this.addLine(line);
			textLine.close();
			lineIndex++;
		}
		bufferedReader.close();
		fireModelChanged();
	}
	
	/**
	 * This method will invoke if a change in the ProgramModel appears and will inform all registered listeners.
	 */
	private void fireModelChanged() {
		for(int i = 0; i < listener.size(); i++) {
			listener.get(i).modelChanged(this);
		}
	}
	
	/**
	 * Output of the whole program.
	 */
	public String toString() {
		String out = new String();
		for(int i = 0; i < lines.size(); i++) {
			out += lines.get(i) + "\n";
		}
		return out;
	}
	
}
