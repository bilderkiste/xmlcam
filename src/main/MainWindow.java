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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import controller.MenuBarListener;
import controller.TableViewActionListener;
import controller.TableViewDummyModelChangeListener;
import misc.Settings;
import model.Program;
import view.GraphicView;
import view.GraphicViewActionListener;
import view.GraphicViewMenuBarListener;
import view.ProgramModelListener;
import view.TableViewDummyModel;
import xml.ScriptValidator;
import xml.ScriptValidatorErrorView;
import xml.XMLView;
import xml.XMLViewActionListener;

/**
 * This class creates the GUI for xmlCAM.
 * @author Christian Kirsch
 *
 */

public class MainWindow extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private final String WINDOW_TITLE = new String("xmlCAM");
	private Program programModel;
	private JTabbedPane tabbedPane;
	private XMLView xmlEditorPane;
	private JMenuBar menuBar;
	private GraphicView graphicView;
	private boolean unsavedXML;
	private boolean unsavedGCode;
	
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
		this.unsavedXML = false;
		this.unsavedGCode = false;
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle(getCurrentTitle());
		
		tabbedPane = createTabbedPane();
		this.add(tabbedPane);
		
		this.setJMenuBar(createMenuBar());
		
		URL iconURL = getClass().getResource("/xmlCAM_icon.png");
        if (iconURL != null) {
            Image icon = new ImageIcon(iconURL).getImage();
            this.setIconImage(icon);
        }
		
        // Add this listener to set the unsaved G-Code Flag to true if model has changed.
        programModel.addProgrammModelListener(new ProgramModelChangeListener());
        
		this.pack();
		this.setMinimumSize(new Dimension(400, 400));
		
		this.setVisible(true);
	}
	
	/**
	 * Returns the current window title with the opened filenames for XML and G-Code files.
	 * @return The title as a String.
	 */
	public String getCurrentTitle() {
		StringBuilder title = new StringBuilder(WINDOW_TITLE);
		if(currentXMLFile != null) {
			title.append(" - " + currentXMLFile.getName());
			if(unsavedXML) {
				title.append(" *");
			}
		}
		if(currentGCodeFile != null) {
			title.append(" - " + currentGCodeFile.getName());
			if(unsavedGCode) {
				title.append(" *");
			}
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
		this.setUnsavedGCode(false);
		this.setTitle(getCurrentTitle());
		// TODO: Make Item accessible via ActionCommand
		menuBar.getMenu(1).getItem(1).setEnabled(true);
		
	}
	
	/**
	 * Clears the the G-Code file, which is currently opened.
	 * This shut be used, if the file is just closed. The window title and menu bar will updated as well.
	 */
	public void clearCurrentGCodeFile() {
		//this.currentGCodeFile = null;
		this.setTitle(getCurrentTitle());
		// TODO: Make Item accessible via ActionCommand
		//menuBar.getMenu(1).getItem(1).setEnabled(false);
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
		this.unsavedXML = false;
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
		this.unsavedXML = false;
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
		JCheckBoxMenuItem checkBoxMenuItem;
		
		menuBar = new JMenuBar();
		
		MenuBarListener menuBarListener = new MenuBarListener(programModel, xmlEditorPane, this);
		
		menu = new JMenu("XML");
		menu.setMnemonic(KeyEvent.VK_X);
		menuBar.add(menu);
		
		menuItem = new JMenuItem("Neu");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		menuItem.setActionCommand("new_xml");
		menuItem.addActionListener(menuBarListener);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Öffnen");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		menuItem.setActionCommand("open_xml");
		menuItem.addActionListener(menuBarListener);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Speichern");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		menuItem.setActionCommand("qsave_xml");
		menuItem.addActionListener(menuBarListener);
		menu.add(menuItem);
		menuItem.setEnabled(false);
		
		menuItem = new JMenuItem("Speichern unter");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.SHIFT_MASK |ActionEvent.CTRL_MASK));
		menuItem.setActionCommand("save_xml");
		menuItem.addActionListener(menuBarListener);
		menu.add(menuItem);
		
		menu = new JMenu("G-Code");
		menu.setMnemonic(KeyEvent.VK_G);
		menuBar.add(menu);
		
		menuItem = new JMenuItem("Leeren");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK));
		menuItem.setActionCommand("new_gcode");
		menuItem.addActionListener(menuBarListener);
		menu.add(menuItem);
		
		/*menuItem = new JMenuItem("Öffnen", KeyEvent.VK_F);
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setActionCommand("open_gcode");
		menuItem.addActionListener(menuBarListener);
		menu.add(menuItem);*/
		
		menuItem = new JMenuItem("Speichern");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
		menuItem.setActionCommand("qsave_gcode");
		menuItem.addActionListener(menuBarListener);
		menu.add(menuItem);
		menuItem.setEnabled(false);
		
		menuItem = new JMenuItem("Speichern unter");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.SHIFT_MASK | ActionEvent.ALT_MASK));
		menuItem.setActionCommand("save_gcode");
		menuItem.addActionListener(menuBarListener);
		menu.add(menuItem);
			
		menu = new JMenu("Grafikansicht");
		menuBar.add(menu);
		
		GraphicViewMenuBarListener graphicViewMenuBarListener = new GraphicViewMenuBarListener(graphicView);
		
		checkBoxMenuItem = new JCheckBoxMenuItem("Zeige Formen");
		checkBoxMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.CTRL_MASK));
		checkBoxMenuItem.setSelected(graphicView.getGraphicViewCanvasView().isShapeVisible());
		checkBoxMenuItem.setActionCommand("show_shapes");
		checkBoxMenuItem.addActionListener(graphicViewMenuBarListener);
		menu.add(checkBoxMenuItem);
		
		checkBoxMenuItem = new JCheckBoxMenuItem("Zeige G0");
		checkBoxMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.CTRL_MASK));
		checkBoxMenuItem.setSelected(graphicView.getGraphicViewCanvasView().isG0lineVisible());
		checkBoxMenuItem.setActionCommand("show_g0");
		checkBoxMenuItem.addActionListener(graphicViewMenuBarListener);
		menu.add(checkBoxMenuItem);
		
		checkBoxMenuItem = new JCheckBoxMenuItem("Zeige G1");
		checkBoxMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, ActionEvent.CTRL_MASK));
		checkBoxMenuItem.setSelected(graphicView.getGraphicViewCanvasView().isG1lineVisible());
		checkBoxMenuItem.setActionCommand("show_g1");
		checkBoxMenuItem.addActionListener(graphicViewMenuBarListener);
		menu.add(checkBoxMenuItem);
		
		checkBoxMenuItem = new JCheckBoxMenuItem("Zeige Punkte");
		checkBoxMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, ActionEvent.CTRL_MASK));
		checkBoxMenuItem.setSelected(graphicView.getGraphicViewCanvasView().isPointVisible());
		checkBoxMenuItem.setActionCommand("show_points");
		checkBoxMenuItem.addActionListener(graphicViewMenuBarListener);
		menu.add(checkBoxMenuItem);
		
		checkBoxMenuItem = new JCheckBoxMenuItem("Zeige Raster");
		checkBoxMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_5, ActionEvent.CTRL_MASK));
		checkBoxMenuItem.setSelected(graphicView.getGraphicViewCanvasView().isGridVisible());
		checkBoxMenuItem.setActionCommand("show_grid");
		checkBoxMenuItem.addActionListener(graphicViewMenuBarListener);
		menu.add(checkBoxMenuItem);
	
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
		xmlEditorPane.setFont(xmlEditorPane.getFont().deriveFont(Font.PLAIN, Settings.xmlFontSize));
		
		xmlEditorPane.setText("<?xml version=\"1.0\"?>\n");
		
		xmlEditorPane.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
		xmlEditorPane.setCodeFoldingEnabled(true);
		
		// TODO: Delete stuff
		/*try {
			File file = new File("program.xml");
			FileReader inputStream = new FileReader(file);
			xmlEditorPane.read(inputStream, null);
			this.currentXMLFile = file;
			this.setTitle(getCurrentTitle());
		} catch (IOException e) {
			Main.log.log(Level.WARNING, "Error reading file: " + e);
		}*/
		// until here
		
		xmlEditorPane.setScriptvalidator(new ScriptValidator()); 
		xmlEditorPane.getScriptvalidator().start();
		
		xmlEditorPane.getDocument().addDocumentListener(new DocumentListener() {
			
		    @Override
		    public void insertUpdate(DocumentEvent e) {
		    }

		    @Override
		    public void removeUpdate(DocumentEvent e) {
		    }

		    @Override
		    public void changedUpdate(DocumentEvent e) {
		        unsavedXML = true;
		        setTitle(getCurrentTitle());
		    }
			
		});
	    
		//JScrollPane scrollPane = new JScrollPane(xmlEditorPane);
		RTextScrollPane scrollPane = new RTextScrollPane(xmlEditorPane);
		
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
		
		graphicView = new GraphicView(programModel);
		panel.add(graphicView, BorderLayout.CENTER);
	
		JPanel optionPanel = new JPanel();
		GraphicViewActionListener graphicViewActionListener = new GraphicViewActionListener(graphicView);
		
		JButton zoomIn = new JButton("+");
		zoomIn.setActionCommand("zoom_in");
		zoomIn.addActionListener(graphicViewActionListener);
		optionPanel.add(zoomIn);
	
		JButton zoomOut = new JButton("-");
		zoomOut.setActionCommand("zoom_out");
		zoomOut.addActionListener(graphicViewActionListener);
		optionPanel.add(zoomOut);
		
		AbstractAction zoomInAction = new AbstractAction() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        zoomIn.doClick();
		    }
		};
		
		AbstractAction zoomOutAction = new AbstractAction() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        zoomOut.doClick();
		    }
		};
	
		KeyStroke ksPlus = KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, InputEvent.CTRL_DOWN_MASK);
		KeyStroke ksMinus = KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, InputEvent.CTRL_DOWN_MASK);
		
		/*KeyStroke ksPlusNormal = KeyStroke.getKeyStroke(KeyEvent.VK_PLUS,InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
		KeyStroke ksPlusNumpad = KeyStroke.getKeyStroke(KeyEvent.VK_ADD, InputEvent.CTRL_DOWN_MASK);
		KeyStroke ksMinusNormal = KeyStroke.getKeyStroke(KeyEvent.VK_MINUS,InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
		KeyStroke ksMinusNumpad = KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, InputEvent.CTRL_DOWN_MASK);*/
			
		panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ksPlus, "zoom_in");
		panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ksMinus, "zoom_out");
		panel.getActionMap().put("zoom_in", zoomInAction);
		panel.getActionMap().put("zoom_out", zoomOutAction);
		
		panel.add(optionPanel, BorderLayout.SOUTH);
		
		return panel;
	}

	public boolean isUnsavedXML() {
		return unsavedXML;
	}

	public void setUnsavedXML(boolean unsavedXML) {
		this.unsavedXML = unsavedXML;
	}

	public boolean isUnsavedGCode() {
		return unsavedGCode;
	}

	public void setUnsavedGCode(boolean unsavedGCode) {
		this.unsavedGCode = unsavedGCode;
	}
	
	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}

	class ProgramModelChangeListener implements ProgramModelListener {

		@Override
		public void modelChanged(Program model) {
			setUnsavedGCode(true);
			setTitle(getCurrentTitle());
		}
		
	}

}
