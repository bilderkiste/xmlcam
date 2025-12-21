/*********************************************************************\
 * Settings.java - xmlCam G-Code Generator                           *
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

package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;

import org.yaml.snakeyaml.Yaml;

import main.Main;

/**
 * This class load the settings for the whole program from the file settings.yaml, which is located in xmlCam main folder.
 * All settings are reachable by static variables.
 * @author Christian Kirsch
 *
 */

public class Settings {
	
	/**
	 * The G-Code dialect.
	 */
	private String dialect;
	/**
	 * The security height for a G0 move above the workpiece.
	 */
	private int securityHeight;
	/**
	 * The bounds of the workbench (xmin, ymin, xmax, ymax).
	 */
	private Workbench workbench;
	/**
	 * The ruler and grid steps for graphical view.
	 */
	private int gridStep;
	/**
	 * The font size for the XML Editor
	 */
	private int xmlFontSize;
	
	/**
	 * The start directory for the JFileChooser Dialog
	 */
	private File userDir;
	
	/**
	 * Reads the user settings from the file settings.yaml. The file shall be located in the main folder.
	 * If an error occurs, the default setting will be loaded.
	 */
	@SuppressWarnings("unchecked")
	public void readSettings() {
    
		try {			
			Yaml yaml = new Yaml();
			FileInputStream inputStream = new FileInputStream(new File("settings.yaml"));
	    	Map<String, Object> map = yaml.load(inputStream);
	    
	    	if (map.containsKey("dialect")) {
	    		dialect = (String) map.get("dialect");
	    		Main.log.log(Level.FINE, "Set dialect successfully to value " + dialect + ".");
	    	} else {
				setDialectDefault("Could not find dialect parameter in settings file. ");
	    	}
	    	
	     	if (map.containsKey("security-height")) {
	    		try {
	    			securityHeight = (int) map.get("security-height");
					if(securityHeight < 0) {
						setSecurityHeightDefault("security-height must be greater than 0. ");
					} else {
						Main.log.log(Level.FINE, "Set security-height successfully to " + securityHeight + ".");
					}
	    		} catch (ClassCastException e) {
	    			setSecurityHeightDefault("Wrong parameter in settings for security-height. ");
	    		}
			} else {
				setSecurityHeightDefault("Could not find security-height parameter in settings file. ");
			}
	     	
	     	try {
		     	ArrayList<Integer> wb = (ArrayList<Integer>) map.get("workbench");
				workbench = new Workbench(wb.get(0), wb.get(1), wb.get(2), wb.get(3));
				if(workbench.getXDimension() < 1 || workbench.getYDimension() < 1 ) {
					setWorkbenchDefault("Workbench has illegal dimenstions. ");
				} else {
					Main.log.log(Level.FINE, "Set workbench successfully to values " + workbench + ".");
				}
	     	} catch (IndexOutOfBoundsException e) {
	     		setWorkbenchDefault("Wrong parameter in settings for workbench. ");
	     	} catch (ClassCastException e) {
	     		setWorkbenchDefault("Wrong parameter in settings for workbench. ");
    		}
	    	
	    	if (map.containsKey("grid-step")) {
	    		try {
		    		gridStep = (int) map.get("grid-step");
					if(gridStep < 10) {
						setGridStepDefault("Scale step must be greater than 9. ");
					} else {
						Main.log.log(Level.FINE, "Set grid step for graphical view successfully to " + gridStep + ".");
					}
	    		} catch (ClassCastException e) {
	    			setGridStepDefault("Wrong parameter in settings for grid-step. ");
	    		}
			} else {
				setGridStepDefault("Could not find grid-step parameter in settings file. ");
			}
	    	
	       	if (map.containsKey("font-size")) {
	    		try {
		    		xmlFontSize = (int) map.get("font-size");
		    		if(xmlFontSize < 0 || xmlFontSize > 30) {
		    			setFontSizeDefault("Font size must be greater than 0 and smaller than 30. ");
					} else {
						Main.log.log(Level.FINE, "Set font size for XML-Editor successfully to " + xmlFontSize + "pt.");
					}
	    		} catch (ClassCastException e) {
	    			setFontSizeDefault("Wrong parameter in settings for font-size. ");
	    		}
			} else {
				setFontSizeDefault("Could not find font-size parameter in settings file. ");
			}
	       	
	    	if (map.containsKey("standard-dir")) {
	    		userDir = new File(String.valueOf(map.get("standard-dir")).trim());
	    		if(!userDir.exists()) {
	    			setUserDirDefault("Standard directory does not exist. ");
				} else {
					Main.log.log(Level.FINE, "Set standard directory for XML and G-Code successfully to " + userDir + ".");
				}
			} else {
				setUserDirDefault("Could not find standard-dir parameter in settings file. ");
			}
	    	
		} catch (FileNotFoundException e) {
			Main.log.log(Level.SEVERE, "Failed to load settings.yaml. {0}", new Object[] { e });
			setAllDefaults("Set all defaults. ");
		} catch (Exception e) {
			Main.log.log(Level.SEVERE, "Error parsing yaml file. {0}", new Object[] { e });
			setAllDefaults("Set all defaults. ");
		}		
	}
	
	private void setAllDefaults(String message) {
		setDialectDefault(message);
		setSecurityHeightDefault(message);
		setWorkbenchDefault(message);
		setGridStepDefault(message);
		setFontSizeDefault(message);
		setUserDirDefault(message);
	}

	/**
	 * Set default for G-Code dialect.
	 */
	private void setDialectDefault(String message) {
		dialect = "GRBL";
		Main.log.log(Level.FINE, message + "Set G-Code dialect to default value " + dialect  + ".");
	}
	
	/**
	 * Set default for security height.
	 */
	private void setSecurityHeightDefault(String message) {
		securityHeight = 5;
		Main.log.log(Level.FINE, message + "Set security height to default value " + securityHeight  + ".");
	}
	
	/**
	 * Set default for workbench dimensions.
	 */
	private void setWorkbenchDefault(String message) {
		workbench = new Workbench(0, 0, 400, 400);
		Main.log.log(Level.FINE, message + "Set workbench to default value " + workbench + ".");
	}

	/**
	 * Set default for grid step for graphical view.
	 */
	private void setGridStepDefault(String message) {
		gridStep= 50;
		Main.log.log(Level.FINE, message + "Set grid step to default value " + gridStep + ".");
	}
	
	/**
	 * Set default for XML-Editor font size.
	 */
	private void setFontSizeDefault(String message) {
		xmlFontSize=12;
		Main.log.log(Level.FINE, message + "Set XML-Editor font size to default value " + xmlFontSize +"pt.");
	}
	
	/**
	 * Set default for user directory.
	 */
	private void setUserDirDefault(String message) {
		userDir = new File(System.getProperty("user.dir"));
		Main.log.log(Level.FINE, message + "Set standard user directory for XML and G-Code to default value " + userDir);
	}

	public String getDialect() {
		return dialect;
	}

	public int getSecurityHeight() {
		return securityHeight;
	}

	public Workbench getWorkbench() {
		return workbench;
	}

	public int getGridStep() {
		return gridStep;
	}

	public int getXmlFontSize() {
		return xmlFontSize;
	}

	public File getUserDir() {
		return userDir;
	}	
	
}
