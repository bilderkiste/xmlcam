/*********************************************************************\
 * XMLView.java - xmlCam G-Code Generator                            *
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

import javax.swing.JTextPane;

import main.ScriptValidator;

/**
 * This class implements the XML View.
 * It extends a textpane where the program can be written through an XML Script.
 * This will the translated to G-Code by the generator.
 * @author Christian Kirsch
 *
 */

public class XMLView extends JTextPane {
	
	private static final long serialVersionUID = 1L;
	private ScriptValidator scriptValidator;
	
	/**
	 * Constructs an XML-View.
	 */
	public XMLView() {
		super();
		this.scriptValidator = null;
	}

	/**
	 * Returns the validator, that validates the XML script for correctness.
	 * @return The validator
	 */
	public ScriptValidator getScriptvalidator() {
		return scriptValidator;
	}
	
	/**
	 * Sets a new validator, that validates the XML script for correctness.
	 * @param scriptvalidator The new validator
	 */
	public void setScriptvalidator(ScriptValidator scriptvalidator) {
		this.scriptValidator = scriptvalidator;
		this.scriptValidator.setEditorPane(this);
	}

}
