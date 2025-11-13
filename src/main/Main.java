/*********************************************************************\
 * Main.java - xmlCam G-Code Generator                               *
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

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import misc.Settings;
import model.Program;

/**
 * The entry class for xmlCam.
 * The logger will declared as static variable.
 * @author Christian Kirsch
 */

public class Main {
	
	public static final Logger log = Logger.getLogger("Logger");
	
	public static final String version = new String("0.96dev");

	public static void main (String[] arguments) {
		String[] arg = null;
		for(int i = 0; i < arguments.length; i++) {
			arg = arguments[i].split("=");
			if(arg[i].equals("loglevel")) {
				log.setLevel(Level.parse(arg[1].toUpperCase()));
			} else if(arg[i].equals("help")) {
				System.out.println("Options:");
				System.out.println("loglevel=<level>  <level>=fine|warning|error");
				System.exit(0);
			} else {
				System.out.println("Type option help for more information.");
				System.exit(0);
			}
		}
		
		try {
			Handler consoleHandler = new ConsoleHandler();
			Handler fileHandler = new FileHandler("xmlCAM-Logfile.txt");
			consoleHandler.setLevel(Level.ALL);
			fileHandler.setLevel(Level.ALL);
			log.setUseParentHandlers(false);
			log.addHandler(consoleHandler);
			log.addHandler(fileHandler);
			log.log(Level.FINE , "Logger at level " + log.getLevel());
		} catch (SecurityException e) {
			log.log(Level.WARNING, e.toString());
		} catch (IOException e) {
			log.log(Level.WARNING, "Error writing log file: " + e);
		}
		
		Main.log.log(Level.INFO, "Welcome to xmlCAM build " + version);
		
		Program program = new Program();
		Settings.readSettings();
		new MainWindow(program);
	}
	
}
