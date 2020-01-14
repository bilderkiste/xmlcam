/*********************************************************************\
 * MainWindow.java - xmlCam G-Code Generator                         *
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

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import controller.XMLViewActionListener;
import controller.GraphicViewActionListener;
import controller.MenuBarListener;
import controller.TableViewActionListener;
import controller.TableViewDummyModelChangeListener;
import model.Program;
import view.GraphicView;
import view.TableViewDummyModel;
import view.XMLView;

/**
 * This class creates the GUI for xmlCam.
 * @author Christian Kirsch
 *
 */

public class MainWindow extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private final String WINDOW_TITLE = new String("xmlCam");
	private Program programModel;
	private XMLView xmlEditorPane;
	private JMenuBar menuBar;
	/**
	 * The current which was open by the XML open file dialog.
	 */
	private File currentXMLFile;
	
	/**
	 * The current which was open by the G-Code open file dialog.
	 */
	private File currentGCodeFile;
	
	/**
	 * Constructs the main window.
	 * @param programModel The ProgramModel
	 */
	public MainWindow(Program programModel) {
		this.programModel = programModel;
		this.currentGCodeFile = null;
		this.currentXMLFile = null;
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle(getCurrentTitle());
		
		this.add(createTabbedPane());
		
		this.setJMenuBar(createMenuBar());
		
		this.pack();
		this.setVisible(true);
	}
	
	/**
	 * Returns the current window title with the opened filenames for XML and G-Code files.
	 * @return The title as a String.
	 */
	private String getCurrentTitle() {
		StringBuilder title = new StringBuilder(WINDOW_TITLE);
		if(currentXMLFile != null) {
			title.append(" - " + currentXMLFile.getName());
		}
		if(currentGCodeFile != null) {
			title.append(" - " + currentGCodeFile.getName());
		}
		return title.toString();
	}
	
	/**
	 * Returns the G-Code file, which is currently opened.
	 * @return The G-Code file.
	 */
	public File getCurrentGCodeFile() {
		return currentGCodeFile;
	}

	/**
	 * Sets the G-Code file, which is currently opened.
	 * The window title and menu bar will updated as well.
	 * @param currentGCodeFile The G-Code file
	 */
	public void setCurrentGCodeFile(File currentGCodeFile) {
		this.currentGCodeFile = currentGCodeFile;
		this.setTitle(getCurrentTitle());
		// TODO: Make Item accessible via ActionCommand
		menuBar.getMenu(1).getItem(2).setEnabled(true);
		
	}
	
	/**
	 * Clears the the G-Code file, which is currently opened.
	 * This shut be used, if the file is just closed. The window title and menu bar will updated as well.
	 */
	public void clearCurrentGCodeFile() {
		this.currentGCodeFile = null;
		this.setTitle(getCurrentTitle());
		// TODO: Make Item accessible via ActionCommand
		menuBar.getMenu(1).getItem(2).setEnabled(false);
	}
	
	/**
	 * Returns the XML file, which is currently opened.
	 * @return The XML file.
	 */
	public File getCurrentXMLFile() {
		return currentXMLFile;
	}

	/**
	 * Sets the XML file, which is currently opened.
	 * The window title and menu bar will updated as well.
	 * @param currentXMLFile The XML file
	 */
	public void setCurrentXMLFile(File currentXMLFile) {
		this.currentXMLFile = currentXMLFile;
		this.setTitle(getCurrentTitle());
		// TODO: Make Item accessible via ActionCommand
		menuBar.getMenu(0).getItem(2).setEnabled(true);
		
	}
	
	/**
	 * Clears the the XML file, which is currently opened.
	 * This shut be used, if the file is just closed. The window title and menu bar will updated as well.
	 */
	public void clearCurrentXMLFile() {
		this.currentXMLFile = null;
		this.setTitle(getCurrentTitle());
		// TODO: Make Item accessible via ActionCommand
		menuBar.getMenu(0).getItem(2).setEnabled(false);
	}


	/**
	 * Creates the menu bar.
	 * @return The menu bar object
	 */
	private JMenuBar createMenuBar() {
		JMenu menu;
		JMenuItem menuItem;
		
		menuBar = new JMenuBar();
		
		MenuBarListener menuBarListener = new MenuBarListener(programModel, xmlEditorPane, this);
		
		menu = new JMenu("XML");
		menu.setMnemonic(KeyEvent.VK_X);
		menuBar.add(menu);
		
		menuItem = new JMenuItem("Neu", KeyEvent.VK_N);
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setActionCommand("new_xml");
		menuItem.addActionListener(menuBarListener);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Öffnen", KeyEvent.VK_F);
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setActionCommand("open_xml");
		menuItem.addActionListener(menuBarListener);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Speichern", KeyEvent.VK_Q);
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setActionCommand("qsave_xml");
		menuItem.addActionListener(menuBarListener);
		menu.add(menuItem);
		menuItem.setEnabled(false);
		
		menuItem = new JMenuItem("Speichern unter", KeyEvent.VK_S);
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setActionCommand("save_xml");
		menuItem.addActionListener(menuBarListener);
		menu.add(menuItem);
		
		menu = new JMenu("G-Code");
		menu.setMnemonic(KeyEvent.VK_G);
		menuBar.add(menu);
		
		menuItem = new JMenuItem("Neu", KeyEvent.VK_N);
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setActionCommand("new_gcode");
		menuItem.addActionListener(menuBarListener);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Öffnen", KeyEvent.VK_F);
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setActionCommand("open_gcode");
		menuItem.addActionListener(menuBarListener);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Speichern", KeyEvent.VK_Q);
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setActionCommand("qsave_gcode");
		menuItem.addActionListener(menuBarListener);
		menu.add(menuItem);
		menuItem.setEnabled(false);
		
		menuItem = new JMenuItem("Speichern unter", KeyEvent.VK_S);
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setActionCommand("save_gcode");
		menuItem.addActionListener(menuBarListener);
		menu.add(menuItem);
			
		return menuBar;
	}
	
	/**
	 * Creates the TabbedPane.
	 * @return The tabbedpane
	 */
	private JTabbedPane createTabbedPane() {
		JTabbedPane tabbedpane = new JTabbedPane (JTabbedPane.TOP,JTabbedPane.SCROLL_TAB_LAYOUT );
		
		tabbedpane.addTab("XML-Ansicht", createXMLView());
		tabbedpane.addTab("Tabellenansicht", createTableView());
		tabbedpane.addTab("Grafikansicht", createGraphicView());
		
		return tabbedpane;
	}
	
	/**
	 * Creates the createXMLView.
	 * @return The panel with the XMLView
	 */
	private JPanel createXMLView() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		xmlEditorPane = new XMLView();  
		xmlEditorPane.setContentType("text/plain");
		
		xmlEditorPane.setText("<?xml version=\"1.0\"?>\n");
		
		// TODO: Delete stuff
		try {
			File file = new File("program.xml");
			FileReader inputStream = new FileReader(file);
			xmlEditorPane.read(inputStream, null);
			this.currentXMLFile = file;
			this.setTitle(getCurrentTitle());
		} catch (IOException e) {
			Main.log.log(Level.WARNING, "Error reading file: " + e);
		}
		// until here
		
		xmlEditorPane.setScriptvalidator(new ScriptValidator()); 
		xmlEditorPane.getScriptvalidator().start();
	    
		JScrollPane scrollPane = new JScrollPane(xmlEditorPane);
		
	    panel.add(scrollPane, BorderLayout.CENTER);
		
	    XMLViewActionListener generatorViewButtonActionListener = new XMLViewActionListener(programModel, xmlEditorPane);
	    
		JPanel ButtonPanel = new JPanel();
		JButton generateGCodeButton = new JButton ("G-Code generieren");
		generateGCodeButton.setActionCommand("generate_gcode");
		generateGCodeButton.addActionListener(generatorViewButtonActionListener);
		ButtonPanel.add(generateGCodeButton);
	    
		panel.add(ButtonPanel, BorderLayout.SOUTH);
		
		ScriptValidatorErrorView errorView = new ScriptValidatorErrorView();
		xmlEditorPane.getScriptvalidator().addErrorListener(errorView);
		panel.add(errorView, BorderLayout.NORTH);
		
		return panel;
	}
	
	/**
	 * Creates the TableView.
	 * @return The panel with the TableView
	 */
	private JPanel createTableView() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		TableViewDummyModel tableView = new TableViewDummyModel(programModel);
		programModel.addProgrammModelListener(tableView);
		TableViewDummyModelChangeListener tableViewChangeListener = new TableViewDummyModelChangeListener();
		tableView.addTableModelChangeListener(tableViewChangeListener);
		
		JTable codeTable = new JTable(tableView);
		
		JScrollPane scrollPane = new JScrollPane(codeTable);
		codeTable.setFillsViewportHeight(true);
		//codeTable.getColumnModel().getColumn(programModel.getFieldSize()).setCellRenderer(new TableViewCellRenderer());
		
		panel.add(scrollPane, BorderLayout.CENTER);
		
		TableViewActionListener tableViewActionListener = new TableViewActionListener(programModel, codeTable);
		
		JPanel ButtonPanel = new JPanel();
		JButton addRowButton = new JButton ("Zeile hinzufügen");
		addRowButton.setActionCommand("add_line");
		addRowButton.addActionListener(tableViewActionListener);
		ButtonPanel.add(addRowButton);
		
		JButton removeRowButton = new JButton ("Zeile(n) entfernen");
		removeRowButton.setActionCommand("remove_line");
		removeRowButton.addActionListener(tableViewActionListener);
		ButtonPanel.add(removeRowButton);
		
		JButton addFieldButton = new JButton ("Feld hinzufügen");
		addFieldButton.setActionCommand("add_field");
		addFieldButton.addActionListener(tableViewActionListener);
		ButtonPanel.add(addFieldButton);
		
		panel.add(ButtonPanel, BorderLayout.SOUTH);
		
		return panel;
	}
	
	/**
	 * Creates the GraphicView.
	 * @return The panel with the GraphicView
	 */
	private JPanel createGraphicView() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		GraphicView graphicView = new GraphicView(programModel);
		panel.add(graphicView, BorderLayout.CENTER);
	
		JPanel optionPanel = new JPanel();
		GraphicViewActionListener graphicViewActionListener = new GraphicViewActionListener(graphicView.getGraphicViewCanvasView());
	
		JCheckBox gridCheckbox = new JCheckBox();
		gridCheckbox.setText("Zeige Raster");
		gridCheckbox.setSelected(graphicView.getGraphicViewCanvasView().isGridVisible());
		gridCheckbox.setActionCommand("show_grid");
		gridCheckbox.addActionListener(graphicViewActionListener);
		optionPanel.add(gridCheckbox);
		
		panel.add(optionPanel, BorderLayout.SOUTH);
		
		return panel;
	}
	
}
