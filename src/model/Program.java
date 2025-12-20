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
import java.util.logging.Level;

import generator.Element;
import main.Main;
import view.ProgramModelListener;

/**
 * This class represents the structure of the ProgramModel.
 * 
 * A program consists a various number of rows (0..n).
 * A row consists a various number of fields and could have a comment.
 * A field has always a letter and it could have a number (i.e. G0, X, Y-20.2039, F1000)
 * The comments start always with a semikolon.
 * <pre>
 * Programm		field_0	field_1	field_2	...	field_m	comment
 * row_0		G0 	X13.31	Y10	...		F30	;Move to position X 13.31 and Y 10 with a feed rate 30 mm/s. 
 * row_1		G0	Z0.5 
 * ...
 * row_n								;End of program
 * </pre>
 * 
 * @author Christian Kirsch
 *
 */
public class Program {

	private ArrayList<Row> rows;
	private ArrayList<ProgramModelListener> listener;
	private ArrayList<Element> elementList;

	/**
	 * Constructs a empty program.
	 */
	public Program() {
		this.rows = new ArrayList<Row>();
		this.listener = new ArrayList<ProgramModelListener>();
		this.elementList = new ArrayList<Element>();
	}

	/**
	 * Adds a new row at the end of the program.
	 * @param row The new Row object
	 */
	public void addRow(Row row) {
		rows.add(row);
		fireModelChanged();
	}
	
	/**
	 * Adds a new row to the program after the specific position.
	 * @param row The new Row object
	 * @param rowIndex The index where to insert
	 */
	public void addRow(Row row, int rowIndex) {
		rows.add(rowIndex, row);
		fireModelChanged();
	}
	
	/**
	 * Get the program row from rowIndex.
	 * @param rowIndex Number of row
	 * @return The Row object
	 */
	public Row getRow(int rowIndex) {
		return rows.get(rowIndex);
	}
	
	/**
	 * Replaces a row object at rowIndex.
	 * @param rowIndex Row number.
	 * @param row The new Row object.
	 */
	public void setRow(int rowIndex, Row row) {
		rows.set(rowIndex, row);
		fireModelChanged();
	}
	
	/**
	 * Removes the Row Object at rowIndex.
	 * @param rowIndex The index where to delete
	 */
	public void removeRow(int rowIndex) {
		rows.remove(rowIndex);
		fireModelChanged();
	}
	
	/**
	 * Removes an amount of rows. First row to delete is rowIndex.
	 * @param rowIndex The row index
	 * @param amount The amount
	 */
	public void removeRows(int rowIndex, int amount) {
		for(int i = 0; i < amount; i++) {
			rows.remove(rowIndex);
		}
		fireModelChanged();
	}
	
	/**
	 * Removes all selected rows in the collection.
	 * @param rowCollection Integer-array with all row numbers
	 */
	public void removeRows(int[] rowCollection) {
		for(int i = rowCollection.length - 1; i >= 0; i--) {
			rows.remove(rowCollection[i]);
		}
		fireModelChanged();
	}
	
	/**
	 * Sets a new Comment on Row Object at rowIndex
	 * @param rowIndex Row number.
	 * @param comment The new comment.
	 */
	public void setComment(int rowIndex, String comment) {
		rows.get(rowIndex).setComment(comment);
		fireModelChanged();
	}
	
	/**
	 * Sets a new Comment at the last Row Object
	 * @param comment The new comment.
	 */
	public void setComment(String comment) {
		rows.get(rows.size() - 1).setComment(comment);
		fireModelChanged();
	}
	
	/**
	 * Replaces the field at position rowIndex, columnIndex.
	 * @param rowIndex Row number
	 * @param columnIndex field number
	 * @param field the new Field
	 */
	public void setField(int rowIndex, int columnIndex, Field field) {
		rows.get(rowIndex).setField(columnIndex, field);
		fireModelChanged();
	}
	
	/**
	 * Adds a new field at Row rowIndex.
	 * @param rowIndex Row number.
	 * @param field The new Field.
	 */
	public void addField(int rowIndex, Field field) {
		rows.get(rowIndex).addField(field);
		fireModelChanged();
	}
	
	/**
	 * Adds a new field at the last row of the program.
	 * @param field The new Field.
	 */
	public void addField(Field field) {
		rows.get(rows.size() - 1).addField(field);
		fireModelChanged();
	}
	
	/**
	 * Removes a field at rowIndex, ColumnIndex.
	 * @param rowIndex
	 * @param columnIndex
	 */
	public void removeField(int rowIndex, int columnIndex) {
		rows.get(rowIndex).removeField(columnIndex);
		fireModelChanged();
	}
	
	/**
	 * Sets a command of a field
	 * @param rowIndex Row number.
	 * @param columnIndex Field number.
	 * @param letter The new command letter.
	 */
	public void setLetter(int rowIndex, int columnIndex, char letter) {
		rows.get(rowIndex).getField(columnIndex).setLetter(letter);
		fireModelChanged();
	}
	
	/**
	 * Sets a number of a field
	 * @param rowIndex Row number.
	 * @param columnIndex Field number.
	 * @param number The new command number.
	 */
	public void setNumber(int rowIndex, int columnIndex, BigDecimal number) {
		rows.get(rowIndex).getField(columnIndex).setNumber(number);
		fireModelChanged();
	}
	
	/**
	 * Removes the number of the field. Changes i.e. Z32.1 to Z.
	 * @param rowIndex Row number.
	 * @param columnIndex Field number.
	 */
	public void removeNumber(int rowIndex, int columnIndex) {
		rows.get(rowIndex).getField(columnIndex).removeNumber();
		fireModelChanged();
	}
	
	/**
	 * Returns the maximum size of fields in a row in the program.
	 * @return maximum size;
	 */
	public int getMaxFieldSize() {
		int maxSize = 0;
		for(int i = 0; i < rows.size(); i++) {
			if(rows.get(i).size() > maxSize) {
				maxSize = rows.get(i).size();
			}
		}
		return maxSize;
	}
	
	/**
	 * Returns the amount of rows the program.
	 * @return the amount;
	 */
	public int sizeRow() {
		return rows.size();
	}
	
	/**
	 * Returns the amount of elements.
	 * @return the amount;
	 */
	public int sizeElements() {
		return elementList.size();
	}
	
	/**
	 * Adds an element to the model.
	 * The element includes shapes and toolPathes derived from the shapes.
	 * @param element The element to be added.
	 */
	public void addElement(Element element) {
		elementList.add(element);
		fireModelChanged();
	}
	
	/**
	 * Return an element at index.
	 * The element includes shapes and toolPathes derived from the shapes.
	 * @param index The index
	 * @return The element at index
	 */
	public Element getElement(int index) {
		return elementList.get(index);
	}
	
	/**
	 * Clears the whole program.
	 */
	public void clear() {
		this.rows.clear();
		this.elementList.clear();
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
	 * Reads from an ArrayList with G-Code lines as String and loads it into the model.
	 * @param lines
	 */
	public void readFromArrayList(ArrayList<String> lines) {
		for(int i = 0; i < lines.size(); i++) {
			readFromRow(lines.get(i));
		}
	}
	
	/**
	 * Reads from a G-Code text file and loads it into the model.
	 * @param file File-Object..
	 * @throws IOException
	 */
	public void readFromFile(File file) throws IOException {		
		String rowBuffer;
		
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		
		while((rowBuffer = bufferedReader.readLine()) != null) {
			readFromRow(rowBuffer);
		}
		bufferedReader.close();
		fireModelChanged();
	}
	
	/**
	 * Parses a String with a G-Code line and adds it to the model.
	 * @param line The String with the G-Code
	 */
	public void readFromRow(String line) {
		Row row = new Row();
		Field field;
		
		// Read comments
		int index = line.indexOf(';');
		if(index > -1) {      // Found a comment
			row.setComment(line.substring(index + 1).trim());
			line = line.substring(0, index);   // Cut comment
		}
		
		Scanner textLine = new Scanner(line);    // Split into fields
		
		while(textLine.hasNext()) {
			String fieldBuffer = new String(textLine.next());
			
			try {						
				field = GCodeHelpers.parseField(fieldBuffer);
				row.addField(field);
			} catch(NumberFormatException e) {
				Main.log.log(Level.SEVERE, "Could not read parameter. Field skipped. {0}", new Object[] { e });
			} catch(IllegalArgumentException e) {
				Main.log.log(Level.SEVERE, "Could not read parameter. Field skipped. {0}", new Object[] { e });
			}
		}	
	
		this.addRow(row);
		textLine.close();	
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
		for(int i = 0; i < rows.size(); i++) {
			out += rows.get(i) + "\n";
		}
		return out;
	}
	
}
