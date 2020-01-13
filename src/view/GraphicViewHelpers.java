/*********************************************************************\
 * GraphicViewHelpers.java - xmlCam G-Code Generator                 *
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

import main.Settings;

/**
 * This class provides some helper methods for the GraphicView.
 * @author Christian Kirsch
 */
public class GraphicViewHelpers {
	
	/**
     * Converts the coordinate system from Quadrant IV to Quadrant I and move the coordinate system for negative workbenches.
     * @param coordinate The input coordinate
     * @param componentHeight The current component height
     * @return the output coordinate
     */
	public static int convertY(int coordinate, int componentHeight) {
		return componentHeight - (coordinate - Settings.workbench.getYMin());
    }
    
	/**
     * Moves the coordinate system for negative workbenches.
     * @param coordinate The input coordinate
     * @return The output coordinate
     */
	public static int convertX(int coordinate) {
		return coordinate - Settings.workbench.getXMin();
    }
}
