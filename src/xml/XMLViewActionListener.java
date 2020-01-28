/*********************************************************************\
 * XMLViewActionListener.java - xmlCam G-Code Generator              *
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

package xml;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import controller.Generator;
import model.Program;

/**
 * This class implements the controller for the button commands in the XMLView.
 * @author Christian Kirsch
 */

public class XMLViewActionListener implements ActionListener {

	private Program programModel;
	private XMLView editorPane;
	
	/**
	 * Constructs a new listener.
	 * @param programModel The model with the changeable items
	 * @param editorPane The editorPane with the XML script
	 */
	public XMLViewActionListener(Program programModel, XMLView editorPane) {
		this.programModel = programModel;
		this.editorPane = editorPane;
	}
	
	/**
	 * Gets invoked by an action from the buttons of the XMLView.
	 */
	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		JButton actionButton = (JButton) actionEvent.getSource();
		
		if(actionButton.getActionCommand() == "generate_gcode") {
			Generator generator = new Generator(programModel, editorPane);
			generator.generate();
		}
		
	}

}
