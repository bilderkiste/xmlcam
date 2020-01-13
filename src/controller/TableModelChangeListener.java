/* TableViewActionListener.java - xmlCam G-Code Generator            *
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

import javax.swing.table.AbstractTableModel;

/**
 * The interface for all classes that will implement a change listener for table cell changes. 
 * @author Christian Kirsch
 *
 */
public interface TableModelChangeListener {
	
	/**
	 * Gets invoked, if a change in the table model occurred.
	 */
	public void tableModelChanged(AbstractTableModel tableModel, Object aValue, int rowIndex, int columnIndex);

}
