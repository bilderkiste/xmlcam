/*********************************************************************\
 * TableViewDummyModelChangeListener.java - xmlCam G-Code Generator  *
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

package controller;

import java.util.logging.Level;

import javax.swing.table.AbstractTableModel;

import main.Main;
import model.GCodeHelpers;
import view.TableViewDummyModel;

/**
 * This class implements the listener for cell changes in the TableView.
 * If a wrong value was typed into the cell a severe will logged and the change will discarded.
 * @author Christian Kirsch
 *
 */
public class TableViewDummyModelChangeListener implements TableModelChangeListener {

	/**
	 * Gets invoked, if a change in the table model occurred.
	 * It tries to take the changes into the G-Code program model.
	 */
	@Override
	public void tableModelChanged(AbstractTableModel tableModel, Object aValue, int rowIndex, int columnIndex) {
		
		TableViewDummyModel tableView = (TableViewDummyModel) tableModel;
		String newValue = (String) aValue;
		
		if(columnIndex == tableView.getColumnCount() - 1) {
			tableView.getProgramModel().setComment(rowIndex, newValue);
		} else {
			try {
				if(!newValue.isEmpty()) {
					tableView.getProgramModel().setField(rowIndex, columnIndex, GCodeHelpers.parseField(newValue));
				} else {
					tableView.getProgramModel().removeField(rowIndex, columnIndex);
				}
			} catch(NumberFormatException e) {
				Main.log.log(Level.SEVERE, e + ": Wrong input in cell (" + rowIndex + "," + columnIndex + "). Second part must be a number.");
			} catch(IllegalArgumentException e) {
				Main.log.log(Level.SEVERE, e + ": Wrong input in cell (" + rowIndex + "," + columnIndex + "). First part must be a character.");
			} catch(IndexOutOfBoundsException e) {
				tableView.getProgramModel().addField(rowIndex, GCodeHelpers.parseField(newValue));
			}
		}
		
	}

}
