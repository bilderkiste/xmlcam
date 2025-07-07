/*********************************************************************\
 * TableViewDummyModel.java - xmlCam G-Code Generator                *
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

package view;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import controller.TableModelChangeListener;
import model.Program;

/**
 * This class implements the model for the table in the TableView.
 */

public class TableViewDummyModel extends AbstractTableModel implements ProgramModelListener  {
	
	private static final long serialVersionUID = 1L;
	private Program programModel;
	private ArrayList<TableModelChangeListener> tableModelChangeListener;
	
	/**
	 * Constructs a new table model.
	 * @param model The program model, that holds the G-Code
	 */
	public TableViewDummyModel(Program model) {	
		this.programModel = model;
		tableModelChangeListener = new ArrayList<TableModelChangeListener>();
	}
	
	/**
	 * Adds a listener to the list that's notified each time a change to the table model occurs.
	 * @param listener The listener
	 */
	public void addTableModelChangeListener(TableModelChangeListener listener) {
		tableModelChangeListener.add(listener);
	}
	
	/**
	 * Returns the program model, that holds the G-Code.
	 * @return
	 */
	public Program getProgramModel() {
		return programModel;
	}

	/**
	 * Gets invoked each time a change in G-Code program model occurs.
	 * @param model The model, where the change occurs  
	 */
	@Override
	public void modelChanged(Program model) {
		this.fireTableChanged(null);
	}

	/**
	 * Returns the number of columns in the model. 
	 * A JTable uses this method to determine how many columns it should create and display by default.
	 */
	@Override
	public int getColumnCount() {
		return programModel.getMaxFieldSize() + 1; // +1 is for the comment
	}

	/**
	 * Returns the number of rows in the model. 
	 * A JTable uses this method to determine how many rows it should display. This method should be quick, as it is called frequently during rendering.
	 */
	@Override
	public int getRowCount() {
		return programModel.size();
	}

	/**
	 * Returns the Field object inf the cell at position rowIndex and columnIndex.
	 * If j is the last column return a String-Object for the comment.
	 * If there is no Field at rowIndex, columnIndex, return null;
	 * @param rowIndex The row index of the cell
	 * @param columnIndex The column index of the cell
	 * @return The field object or null
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		try {
			if(columnIndex == getColumnCount() - 1) {
				return programModel.getLine(rowIndex).getComment();
			} else {
				
				return programModel.getLine(rowIndex).getField(columnIndex);
			}
		} catch(IndexOutOfBoundsException e) {
			return null;
		}
	}
	
	/**
	 * Returns the name of the column at columnIndex. This is used to initialize the table's column header name.
	 * @param column The index of the column
	 * @return The name of the column
	 */
	@Override
	public String getColumnName(int column) {
		if(column == programModel.getMaxFieldSize()) {
			return new String("Kommentar");
		} else {
			return super.getColumnName(column);
		}
	}

	/**
	 * Returns true if the cell at rowIndex and columnIndex is editable. Otherwise, setValueAt on the cell will not change the value of that cell.
	 * @param rowIndex The row index of the cell
	 * @param columnIndex The column index of the cell
	 * @return True if the cell is editable
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}
	
	/**
	 * Sets the value in the cell at columnIndex and rowIndex to aValue.
	 * This method invokes all listeners in the controller to take over the changes into the G-Code program model.
	 */
	@Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		for(int i = 0; i < tableModelChangeListener.size(); i++) {
			tableModelChangeListener.get(i).tableModelChanged(this, aValue, rowIndex, columnIndex);
		}
	}

}
