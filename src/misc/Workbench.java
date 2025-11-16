/*********************************************************************\
 * Workbench.java - xmlCam G-Code Generator                    *
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

package misc;

/**
 * This class represents the CNC workbench. It returns the size of and dimensions. 
 * @author Christian Kirsch 
 */

public class Workbench {

	/**
	 * The minimum and maximum values of x and y.
	 */
	private int xMin, yMin, xMax, yMax;
	
	protected Workbench(int xMin, int yMin, int xMax, int yMax) {
		/*if(xMin >= 0) {
			this.xMin = xMin;
		} else {
			this.xMin = 0;
		}
		if(yMin >= 0) {
			this.yMin = yMin;
		} else {
			this.yMin = 0;
		}*/
		this.xMin = xMin;
		this.yMin = yMin;
		this.xMax = xMax;
		this.yMax = yMax;
	}
	
	/**
	 * Returns the x size im mm of the workbench.
	 * @return the x size
	 */
	public int getXDimension() {
		return xMax - xMin;
	}
	
	/**
	 * Returns the y size im mm of the workbench.
	 * @return the y size
	 */
	public int getYDimension() {
		return yMax - yMin;
	}

	/**
	 * Returns the minimal value of x.
	 * @return the minimal value.
	 */
	public int getXMin() {
		return xMin;
	}

	public int getYMin() {
		return yMin;
	}

	public int getXMax() {
		return xMax;
	}

	public int getYMax() {
		return yMax;
	}

	@Override
	public String toString() {
		return new String("(" + xMin + ", " + yMin + ", " + xMax + ", " + yMax + ")");
	}
	
}
