/*********************************************************************\
 * TableViewDummyModelChangeListener.java - xmlCam G-Code Generator  *
 * Copyright (C) 2025, Christian Kirsch                              *
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

import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * This class represents a ToolPath which consists an list of points with x and y coordinates.
 */
public class ToolPath extends ArrayList<Point2D.Double> {
	
	private static final long serialVersionUID = 1L;
	protected String name;
	private boolean pocket;
	
	public ToolPath(String name, boolean pocket) {
		this(name);
		this.pocket = pocket;
	}

	public ToolPath(String name) {
		super();
		this.name = name;
		this.pocket = false;
	}
	
	/**
	 * Adds a new coordinate to the toolPath.
	 * @param coords The new coordinates.
	 */
	public void addPoint(double x, double y) {
		add(new Point2D.Double(x, y));
	}
	
	/**
	 * Adds a new coordinate to the toolPath.
	 * @param coords The new coordinates.
	 */
	public void addPoint(Point2D.Double point) {
		add(point);
	}
	
	/**
	 * Returns the x value of the coordinate at the index of the toolPath.
	 * @param index The index
	 * @return The x value
	 */
	public BigDecimal getX(int index) {
		return new BigDecimal(get(index).getX());
	}
	
	/**
	 * Returns the y value of the coordinate at the index of the toolPath.
	 * @param index The index
	 * @return The y value
	 */
	public BigDecimal getY(int index) {
		return new BigDecimal(get(index).getY());
	}
	
	/**
	 * Returns the name of the toolPath.
	 * @return The name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Concatenates a ToolPath with this ToolPath.
	 * @param toolPath The ToolPath to concatenate
	 */
	public void concatToolPathes(ToolPath toolPath) {
		for(int i = 0; i < toolPath.size(); i++) {
			this.add(toolPath.get(i));
		}
	}

	/**
	 * Returns true if it is a pocket ToolPath
	 * @return true
	 */
	public boolean isPocket() {
		return pocket;
	}
	
}
