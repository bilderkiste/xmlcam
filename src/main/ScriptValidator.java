/*********************************************************************\
 * ScriptValidator.java - xmlCam G-Code Generator                    *
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

import java.awt.Color;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.xml.XMLConstants;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

import view.XMLView;

public class ScriptValidator extends Thread {
	
	private XMLView editorPane;
	private boolean interrupt;
	private Validator xmlValidator;
	private ScriptValidatorErrorHandler errorHandler;
	private ArrayList<ScriptValidatorErrorListener> errorListener;
	
	public ScriptValidator() {
		this.interrupt = false;
		
		errorListener = new ArrayList<ScriptValidatorErrorListener>();
		
	    try {
	    	SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = schemaFactory.newSchema(new StreamSource(new InputStreamReader(getClass().getResourceAsStream("/main/Schema.xsd"))));
			this.xmlValidator = schema.newValidator();
			errorHandler = new ScriptValidatorErrorHandler();
			this.xmlValidator.setErrorHandler(errorHandler);
		} catch (SAXException e) {
			Main.log.log(Level.SEVERE, e.toString());
		}
	}
	
	public ScriptValidator(XMLView editorPane) {
		this();
		this.editorPane = editorPane;
	}

	@Override
	public void run() {
		if(editorPane == null) {
			return;
		}
		
		Main.log.log(Level.INFO, "Start new validator thread no. " + this.getId() + ".");
		DefaultStyledDocument document = (DefaultStyledDocument) editorPane.getStyledDocument();
		MutableAttributeSet attributes = editorPane.getInputAttributes();
	
		while(!interrupt) {
			StyleConstants.setForeground(attributes, Color.BLACK);
			document.setCharacterAttributes(0, document.getLength(), attributes, true);
			
			StyleConstants.setForeground(attributes, Color.RED);
			
			try {
				xmlValidator.validate(new StreamSource(new StringReader(editorPane.getText())));
				fireNoErrorFound();
			} catch (SAXException e) {
				String xml = editorPane.getText();
				int j = 0, k = 0, line = 1;
	
				j = xml.indexOf("\n");
				while(j > -1) {
					line++;
					if(line == errorHandler.getLineNumer()) {
						k = xml.indexOf("\n", j + 1);
						if(k == -1) { // if there is no line break, find the end of string
							k = j + xml.substring(j).length();
						}
						break;
					}
					j = xml.indexOf("\n", j + 1);
				}
				document.setCharacterAttributes(j, k - j, attributes, false);
				fireErrorOccured(errorHandler);
			} catch (IOException e) {
				Main.log.log(Level.WARNING, e.toString());
			} catch (FactoryConfigurationError e) {
				Main.log.log(Level.WARNING, e.toString());
			}
			
		    try {
				Thread.sleep(5000);
			} catch(InterruptedException e) {
				interrupt = true;
				Main.log.log(Level.WARNING, "Validator thread was interrupted. " + e);
			}
		}
		Main.log.log(Level.INFO, "Validator thread "+ this.getId() + " was interrupted.");
	}
	
	/**
	 * Interrupts the validator thread.
	 */
	public void interrupt() {
		interrupt = true;
	}
	
	/**
	 * Checks if the thread is interrupted and dead.
	 * @return true if is interrupted
	 */
	public boolean isInterrupt() {
		return interrupt;
	}

	/**
	 * Returns the XMLView which is validated
	 * @return the XMLView
	 */
	public XMLView getEditorPane() {
		return editorPane;
	}

	/**
	 * Sets a new XMLView.
	 * @param editorPane the new XMLView.
	 */
	public void setEditorPane(XMLView editorPane) {
		this.editorPane = editorPane;
	}
	
	/**
	 * Adds an new error listener, which get invoked from the script validator.
	 * @param listener the listener
	 */
	public void addErrorListener(ScriptValidatorErrorListener listener) {
		errorListener.add(listener);
	}
	
	/**
	 * Informs all listener if an error occured.
	 * @param errorHandler the handler with error information
	 */
	private void fireErrorOccured(ScriptValidatorErrorHandler errorHandler) {
		for(int i = 0; i < errorListener.size(); i++) {
			errorListener.get(i).errorOccured(errorHandler);
		}
	}
	
	/**
	 * Informs all listener that no error was found.
	 */
	private void fireNoErrorFound() {
		for(int i = 0; i < errorListener.size(); i++) {
			errorListener.get(i).noErrorFound();
		}
	}
}


