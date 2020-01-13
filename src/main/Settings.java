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

package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;

/**
 * This class load the settings for the whole program from the file settings.txt, which is located in xmlCam main folder.
 * All settings are reachable by static variables.
 * @author Christian Kirsch
 *
 */

public abstract class Settings {
	
	/**
	 * The security height for a G0 move above the workpiece.
	 */
	public static int securityHeight;
	/**
	 * The bounds of the workbench (xmin, ymin, xmax, ymax).
	 */
	public static Workbench workbench;
	/**
	 * The ruler and grid steps for graphical view.
	 */
	public static int step;
	
	/**
	 * Reads the user settings from the file settings.txt. The file shall be located in the main folder.
	 * If an error occurs, the default setting will be loaded.
	 */
	public static void readSettings() {
	
		try {
			String lineBuffer;
			String[] values;
			BufferedReader bufferedReader = new BufferedReader(new FileReader("settings.txt"));
			StringBuilder stringBuilder = new StringBuilder();
			int searchIndex, endIndex;
			
			while((lineBuffer = bufferedReader.readLine()) != null) {
				stringBuilder.append(lineBuffer);
			}
			
			bufferedReader.close();
			
			
			searchIndex = stringBuilder.indexOf("security-height");
			if(searchIndex > -1) {
				searchIndex = stringBuilder.indexOf("=", searchIndex);
				endIndex = stringBuilder.indexOf(";", searchIndex);
				if(searchIndex > -1 && endIndex > -1) {
					securityHeight = Integer.parseInt(stringBuilder.substring(searchIndex + 1, endIndex).trim());
					if(step < 0) {
						throw new IllegalArgumentException("Security height must be greater than 0");
					}
				} else {
					throw new IllegalArgumentException("Wrong parameter in settings file for securityHeight.");
				}
			} else {
				throw new IllegalArgumentException("Could not find securityHeight parameter in settings file.");
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
						throw new IllegalArgumentException("Workbench has illegal dimenstions");
					}
				} else {
					throw new IllegalArgumentException("Wrong parameter in settings file for workbench");
				}
			} else {
				throw new IllegalArgumentException("Could not find workbench parameter in settings file");
			}
			
			searchIndex = stringBuilder.indexOf("step");
			if(searchIndex > -1) {
				searchIndex = stringBuilder.indexOf("=", searchIndex);
				endIndex = stringBuilder.indexOf(";", searchIndex);
				if(searchIndex > -1 && endIndex > -1) {
					step = Integer.parseInt(stringBuilder.substring(searchIndex + 1, endIndex).trim());
					if(step < 10) {
						throw new IllegalArgumentException("Scale step must be greater than 9");
					}
				} else {
					throw new IllegalArgumentException("Wrong parameter in settings file for scale step");
				}
			} else {
				throw new IllegalArgumentException("Could not find scale step parameter in settings file");
			}

		} catch (IOException e) {
			Main.log.log(Level.WARNING, "Failed to load settings.txt: " + e + ". Set default values.");
			setDefault();
		} catch (IllegalArgumentException e) {
			Main.log.log(Level.WARNING, e + ". Skip all values in file and set default values.");
			setDefault();
		}
	}
	
	/**
	 * Set the default values, if an error occured.
	 */
	private static void setDefault() {
		securityHeight = 5;
		workbench = new Workbench(0, 0, 400, 400);
		step = 50;
	}

}
