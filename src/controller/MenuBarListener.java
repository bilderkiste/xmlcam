/*********************************************************************\
 * MenuBarListener.java - xmlCam G-Code Generator                    *
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
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileNameExtensionFilter;

import main.Main;
import main.MainWindow;
import model.Environment;
import model.Program;
import model.Settings;
import xml.XMLView;

/**
 * This class implements the listener for actions performed by the menu bar.
 * @author Christian Kirsch
 *
 */
public class MenuBarListener implements ActionListener {
	
	private Environment env;
	private XMLView xmlEditorPane;
	private JFrame parentWindow;
	
	/**
	 * Constructs a new menu bar listener.
	 * @param programModel The model with the changeable items
	 * @param xmlEditorPane The editorPane with the XML script
	 * @param parentWindow The main window object
	 */
	public MenuBarListener(Environment env, XMLView xmlEditorPane, JFrame parentWindow) {
		this.env = env;
		this.xmlEditorPane = xmlEditorPane;
		this.parentWindow = parentWindow;
	}

	/**
	 * Gets invoked if an action in the menu bar occurred.
	 */
	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		JMenuItem menuItem = (JMenuItem) actionEvent.getSource();
		
		if(menuItem.getActionCommand() == "new_xml") {
			xmlEditorPane.setText("<?xml version=\"1.0\"?>\n");
			((MainWindow) parentWindow).clearCurrentXMLFile();
			((MainWindow) parentWindow).getTabbedPane().setSelectedIndex(0);
		} else if(menuItem.getActionCommand() == "open_xml") {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileFilter(new FileNameExtensionFilter("XML Dateien", "xml"));
			fileChooser.setCurrentDirectory(env.getSettings().getUserDir());
			int returnVal = fileChooser.showOpenDialog(parentWindow);
			if (returnVal == JFileChooser.APPROVE_OPTION) {	
				try {
					FileReader inputStream = new FileReader(fileChooser.getSelectedFile());
					xmlEditorPane.read(inputStream, null);
					((MainWindow) parentWindow).setCurrentXMLFile(fileChooser.getSelectedFile());
					((MainWindow) parentWindow).getTabbedPane().setSelectedIndex(0);
				} catch (IOException e) {
					Main.log.log(Level.SEVERE, "Error reading file: " + e);
				}

			}
		} else if(menuItem.getActionCommand() == "qsave_xml") {
			try {
				File file = ((MainWindow) parentWindow).getCurrentXMLFile();
				FileWriter filewriter = new FileWriter(file);
				filewriter.write(xmlEditorPane.getText());
				filewriter.flush();
				filewriter.close();
				((MainWindow) parentWindow).setCurrentXMLFile(file);
			} catch (IOException e) {
				Main.log.log(Level.SEVERE, "Error writing file: " + e);
			}
		} else if(menuItem.getActionCommand() == "save_xml") {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileFilter(new FileNameExtensionFilter("XML Dateien", "xml"));
			fileChooser.setCurrentDirectory(env.getSettings().getUserDir());
			int returnVal = fileChooser.showSaveDialog(parentWindow);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					if(fileChooser.getFileFilter() instanceof FileNameExtensionFilter && fileChooser.getSelectedFile().toString().lastIndexOf('.') == -1) {
						String[] extensions = ((FileNameExtensionFilter) fileChooser.getFileFilter()).getExtensions();
						fileChooser.setSelectedFile(new File(fileChooser.getSelectedFile() + "." + extensions[0]));
					}
					FileWriter filewriter = new FileWriter(fileChooser.getSelectedFile());
					filewriter.write(xmlEditorPane.getText());
					filewriter.flush();
					filewriter.close();
					((MainWindow) parentWindow).setCurrentXMLFile(fileChooser.getSelectedFile());
				} catch (IOException e) {
					Main.log.log(Level.SEVERE, "Error writing file: " + e);
				}
			}
		} else if(menuItem.getActionCommand() == "new_gcode") {
			env.getProgram().clear();
			((MainWindow) parentWindow).clearCurrentGCodeFile();
			((MainWindow) parentWindow).getTabbedPane().setSelectedIndex(1);
		/*} else if(menuItem.getActionCommand() == "open_gcode") {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileFilter(new FileNameExtensionFilter("G-Code Dateien", "gcode"));
			fileChooser.setCurrentDirectory(Settings.userDir);
			int returnVal = fileChooser.showOpenDialog(parentWindow);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					programModel.clear();
					programModel.readFromFile(fileChooser.getSelectedFile());
					((MainWindow) parentWindow).setCurrentGCodeFile(fileChooser.getSelectedFile());
				} catch (IOException e) {
					Main.log.log(Level.SEVERE, "Error reading file: " + e);
				}
			}*/
		} else if(menuItem.getActionCommand() == "qsave_gcode") {
			if(((MainWindow) parentWindow).getCurrentGCodeFile() != null) {
				try {
					File file = ((MainWindow) parentWindow).getCurrentGCodeFile();
					env.getProgram().writeToFile(file);
					((MainWindow) parentWindow).setCurrentGCodeFile(file);
				} catch (IOException e) {
					Main.log.log(Level.SEVERE, "Error writing file: " + e);
				}
			}
		} else if(menuItem.getActionCommand() == "save_gcode") {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileFilter(new FileNameExtensionFilter("G-Code Dateien", "gcode"));
			//fileChooser.setFileFilter(new FileNameExtensionFilter("Textdateien", "txt"));
			fileChooser.setCurrentDirectory(env.getSettings().getUserDir());
			int returnVal = fileChooser.showSaveDialog(parentWindow);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					if(fileChooser.getFileFilter() instanceof FileNameExtensionFilter && fileChooser.getSelectedFile().toString().lastIndexOf('.') == -1) {
						String[] extensions = ((FileNameExtensionFilter) fileChooser.getFileFilter()).getExtensions();
						fileChooser.setSelectedFile(new File(fileChooser.getSelectedFile() + "." + extensions[0]));
					}
					env.getProgram().writeToFile(fileChooser.getSelectedFile());
					((MainWindow) parentWindow).setCurrentGCodeFile(fileChooser.getSelectedFile());
				} catch (IOException e) {
					Main.log.log(Level.SEVERE, "Error writing file: " + e);
				}
			}
		}
	}

}
