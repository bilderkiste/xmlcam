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

import javax.swing.JScrollBar;

import misc.Settings;

/**
 * This class provides some helper methods for the GraphicView.
 * @author Christian Kirsch
 */
public class GraphicViewHelpers {
	
	/**
     * Calculates the Y point for the G-Code coordinate on the drawing canvas.
     * It converts the coordinate system from Quadrant IV to Quadrant I and move the coordinate system for negative workbenches, consider the zoom scale and position of the scrollbar. 
     * @param coordinate The input coordinate
     * @param componentHeight The current component height
     * @param scale The zoom scale
     * @param scrollBar The scrollbar, that belongs to this axis
     * @return the output coordinate
     */
	public static int convertY(double coordinate, int componentHeight, int scale, JScrollBar scrollBar) {
		return (int) (coordinate * scale + scrollBar.getValue() - Settings.workbench.getYMin() * scale);
    }
    
	/**
	 * Calculates the X point for the G-Code coordinate on the drawing canvas.
     * It moves the coordinate system for negative workbenches, consider the zoom scale and position of the scrollbar.
     * @param coordinate The input coordinate
     * @param scale The zoom scale
     * @param scrollBar The scrollbar, that belongs to this axis
     * @return The output coordinate
     */
	public static int convertX(double coordinate, int scale, JScrollBar scrollBar) {
		return (int) (coordinate * scale - scrollBar.getValue() - Settings.workbench.getXMin() * scale);	
    }
}
