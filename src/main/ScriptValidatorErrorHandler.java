/*********************************************************************\
 * ScriptValidatorErrorHandler.java - xmlCam G-Code Generator        *
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

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * This class handles the validation errors from the script validator.
 * It returns the message of the error, the column and row, where the error occurred.
 * @author Christian Kirsch
 */

public class ScriptValidatorErrorHandler implements ErrorHandler {
	
	private int lineNumber;
	private int columnNumber;
	private String message;
	
	/**
	 * Constructs the ErrorHandler
	 */
	public ScriptValidatorErrorHandler() {
		lineNumber = 0;
		columnNumber = 0;
		message = null;
	}

	/**
	 * Gets invoked if a warning occurs.
	 */
	@Override
	public void warning(SAXParseException e) throws SAXException {
		lineNumber = e.getLineNumber();
		columnNumber = e.getColumnNumber();
		message = new String("Warning: " + e.getMessage());
		throw e;
	}
	
	/**
	 * Gets invoked if an error occurs.
	 */
    @Override
    public void error(SAXParseException e) throws SAXException {
		lineNumber = e.getLineNumber();
		columnNumber = e.getColumnNumber();
		message = new String("Error: " + e.getMessage());
		throw e;
    }

	/**
	 * Gets invoked if an fatal error occurs.
	 */
    @Override
    public void fatalError(SAXParseException e) throws SAXException {
		lineNumber = e.getLineNumber();
		columnNumber = e.getColumnNumber();
		message = new String("Fatal error: " + e.getMessage());
		throw e;
    }

    /**
     * Returns the line number where the error occurs.
     * @return the line number
     */
	public int getLineNumer() {
		return lineNumber;
	}
	
    /**
     * Returns the column number where the error occurs.
     * @return the column number
     */
	public int getColumnNumer() {
		return columnNumber;
	}
	 
    /**
     * Returns the error message.
     * @return the error message
     */
	public String getMessage() {
		return message;
	}

}
