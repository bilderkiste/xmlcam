/*********************************************************************\
 * ScriptValidatorErrorView.java - xmlCam G-Code Generator           *
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

package main;

import java.awt.Dimension;

import javax.swing.JTextPane;

/**
 * This class implements the view that shows the errors from the script validator.
 * @author Christian Kirsch
 *
 */

public class ScriptValidatorErrorView extends JTextPane implements ScriptValidatorErrorListener {

	private static final long serialVersionUID = 1L;

	public ScriptValidatorErrorView() {
		this.setEditable(false);
		this.setPreferredSize(new Dimension(this.getWidth(), 35));
	}
	
	/**
	 * Gets invoked from the script validator when an error occurs.
	 * @param errorHandler the error handler whith the information about the error
	 */
	@Override
	public void errorOccured(ScriptValidatorErrorHandler errorHandler) {
		this.setText("Line " + errorHandler.getLineNumer() + ": " + errorHandler.getMessage());
	}

	/**
	 * Gets invoked from the script validator when when no error was found.
	 */
	@Override
	public void noErrorFound() {
		this.setText(new String());;
		
	}

}
