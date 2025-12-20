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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;

import main.Main;

/**
 * This class load the settings for the whole program from the file settings.txt, which is located in xmlCam main folder.
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
	private static Workbench workbench;
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
	 * Reads the user settings from the file settings.txt. The file shall be located in the main folder.
	 * If an error occurs, the default setting will be loaded.
	 */
	public void readSettings() {

		try {
			String lineBuffer;
			String[] values;
			BufferedReader bufferedReader = new BufferedReader(new FileReader("settings.txt"));
			StringBuilder stringBuilder = new StringBuilder();
			int searchIndex, endIndex;
			
			/*inputStream = new FileInputStream(new File("settings.yaml"));
	    	Map<String, Object> obj = yaml.load(inputStream);*/
			
			while((lineBuffer = bufferedReader.readLine()) != null) {
				stringBuilder.append(lineBuffer);
			}
			
			bufferedReader.close();
			
			searchIndex = stringBuilder.indexOf("dialect");
			if(searchIndex > -1) {
				searchIndex = stringBuilder.indexOf("=", searchIndex);
				endIndex = stringBuilder.indexOf(";", searchIndex);
				if(searchIndex > -1 && endIndex > -1) {
					dialect = stringBuilder.substring(searchIndex + 1, endIndex).trim();
					Main.log.log(Level.FINE, "Set dialect successfully to value " + dialect + ".");
				} else {
					setDialectDefault("Wrong parameter in settings file for dialect. ");
				}
			} else {
				setDialectDefault("Could not find dialect parameter in settings file. ");
			}
			
			searchIndex = stringBuilder.indexOf("security-height");
			if(searchIndex > -1) {
				searchIndex = stringBuilder.indexOf("=", searchIndex);
				endIndex = stringBuilder.indexOf(";", searchIndex);
				if(searchIndex > -1 && endIndex > -1) {
					securityHeight = Integer.parseInt(stringBuilder.substring(searchIndex + 1, endIndex).trim());
					if(securityHeight < 0) {
						setSecurityHeightDefault("Security height must be greater than 0. ");
					} else {
						Main.log.log(Level.FINE, "Set security height successfully to value " + securityHeight + ".");
					}
				} else {
					setSecurityHeightDefault("Wrong parameter in settings file for securityHeight. ");
				}
			} else {
				setSecurityHeightDefault("Could not find securityHeight parameter in settings file. ");
			}
			
			workbench = new Workbench(0, 0, 400, 400);
			searchIndex = stringBuilder.indexOf("workbench");
			if(searchIndex > -1) {
				searchIndex = stringBuilder.indexOf("=", searchIndex);
				endIndex = stringBuilder.indexOf(";", searchIndex);
				if(searchIndex > -1 && endIndex > -1) {
					values = stringBuilder.substring(searchIndex + 1, endIndex).split(",");
					workbench = new Workbench(Integer.parseInt(values[0].trim()), Integer.parseInt(values[1].trim()), Integer.parseInt(values[2].trim()), Integer.parseInt(values[3].trim()));
					if(workbench.getXDimension() < 1 || workbench.getYDimension() < 1 ) {
						setWorkbenchDefault("Workbench has illegal dimenstions. ");
					} else {
						Main.log.log(Level.FINE, "Set workbench successfully to values " + workbench + ".");
					}
				} else {
					setWorkbenchDefault("Wrong parameter in settings file for workbench. ");
				}
			} else {
				setWorkbenchDefault("Could not find workbench parameter in settings file. ");
			}
			
			searchIndex = stringBuilder.indexOf("grid-step");
			if(searchIndex > -1) {
				searchIndex = stringBuilder.indexOf("=", searchIndex);
				endIndex = stringBuilder.indexOf(";", searchIndex);
				if(searchIndex > -1 && endIndex > -1) {
					gridStep = Integer.parseInt(stringBuilder.substring(searchIndex + 1, endIndex).trim());
					if(gridStep < 10) {
						setGridStepDefault("Scale step must be greater than 9. ");
					} else {
						Main.log.log(Level.FINE, "Set grid step for graphical view successfully to " + gridStep + ".");
					}
				} else {
					setGridStepDefault("Wrong parameter in settings file for scale step. ");
				}
			} else {
				setGridStepDefault("Could not find grid step parameter in settings file. ");
			}
			
			searchIndex = stringBuilder.indexOf("font-size");
			if(searchIndex > -1) {
				searchIndex = stringBuilder.indexOf("=", searchIndex);
				endIndex = stringBuilder.indexOf(";", searchIndex);
				if(searchIndex > -1 && endIndex > -1) {
					xmlFontSize = Integer.parseInt(stringBuilder.substring(searchIndex + 1, endIndex).trim());
					if(xmlFontSize < 0 || xmlFontSize > 30) {
						setFontSizeDefault("Font size must be greater than 0 and smaller than 30. ");
					} else {
						Main.log.log(Level.FINE, "Set font size for XML-Editor successfully to " + xmlFontSize + "pt.");
					}
				} else {
					setFontSizeDefault("Wrong parameter in settings file for font size. ");
				}
			} else {
				setFontSizeDefault("Could not find font size parameter in settings file. ");
			}
			
			searchIndex = stringBuilder.indexOf("standard-dir");
			if(searchIndex > -1) {
				searchIndex = stringBuilder.indexOf("=", searchIndex);
				endIndex = stringBuilder.indexOf(";", searchIndex);
				if(searchIndex > -1 && endIndex > -1) {
					userDir = new File(stringBuilder.substring(searchIndex + 1, endIndex).trim());
					if(!userDir.exists()) {
						setUserDirDefault("Standard directory does not exist.");
					} else {
						Main.log.log(Level.FINE, "Set standard directory for XML and G-Code successfully to " + userDir + ".");
					}
				} 
			} else {
				setUserDirDefault("Could not find standard directory for XML and G-Code in settings file.");
			}

		} catch (IOException e) {
			Main.log.log(Level.WARNING, "Failed to load settings.txt: " + e + ". Set default values.");
			setAllDefaults();
		} catch (IllegalArgumentException e) {
			Main.log.log(Level.WARNING, e + ". Skip all values in file and set default values.");
			setAllDefaults();
		} catch (Exception e) {
			Main.log.log(Level.WARNING, e + ". Skip all values in file and set default values.");
			setAllDefaults();
		}
	}
	
	/**
	 * Set all default values, if an error occured.
	 */
	private void setAllDefaults() {
		setSecurityHeightDefault("");
		setWorkbenchDefault("");
		setGridStepDefault("");
		setFontSizeDefault("");
		setUserDirDefault("");
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
	 * Set default for workbench measurest.
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
