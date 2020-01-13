/*********************************************************************\
 * TableViewActionListener.java - xmlCam G-Code Generator            *
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JTable;

import model.Field;
import model.Line;
import model.Program;

/**
 * This class implements the controller for the button commands in the TableView.
 * @author Christian Kirsch
 */
public class TableViewActionListener implements ActionListener {
	
	private Program programModel;
	private JTable tableView;
	
	/**
	 * Constructs a new listener.
	 * @param programModel The model with the changeable items
	 * @param tableView The JTable view (not the table model) 
	 */
	public TableViewActionListener(Program programModel, JTable tableView) {
		this.tableView = tableView;
		this.programModel = programModel;
	}

	/**
	 * Gets invoked by an action from the buttons of the TableView.
	 */
	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		JButton actionButton = (JButton) actionEvent.getSource();
		
		int[] selectedRowCollection = tableView.getSelectedRows();

		if(actionButton.getActionCommand() == "add_line") {
			if(selectedRowCollection.length > 0) {
				programModel.addLine(new Line(), selectedRowCollection[0]);
			} else {
				programModel.addLine(new Line());
			}
		} else if(actionButton.getActionCommand() == "remove_line") {
			if(selectedRowCollection.length > 0) {
				programModel.removeLines(selectedRowCollection);
			}
		} else if(actionButton.getActionCommand() == "add_field") {
			if(selectedRowCollection.length > 0) {
				programModel.addField(selectedRowCollection[0], new Field());
			}
		}
		
	}

}
