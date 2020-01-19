/*********************************************************************\
 * Tupel.java - xmlCam G-Code Generator                              *
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

package controller;

import java.math.BigDecimal;

import org.w3c.dom.Node;

/**
 * This class represents a tuple with a specific amount of numeric values like (a1,a2,...,an).
 * @author Christian Kirsch
 *
 */
public class Tuple {

	public static final int POINT = 0;
	public static final int BEZIER = 1;
	public static final int SPLINE = 2; 
	
	private String[] parameters;
	private int type;

	/**
	 * Constructs an new tuple an fills it with values from an xml node.
	 * The default node type of a point will set.
	 * @param node The node with the values
	 */
	public Tuple(Node node) {
		this(node, 0);
	}
	
	/**
	 * Constructs an new tuple an fills it with values from an xml node.
	 * @param node The node with the values
	 * @param type The type of the node
	 */
	public Tuple(Node node, int type) {
		this.parameters = node.getTextContent().split(",");
		this.type = type;
	}
	
	/**
	 * Returns the value from the tuple at the specified position.
	 * @param index The position of the value
	 * @return The value as a BigDecimal object
	 */
	public BigDecimal getValue(int index) {
		return new BigDecimal(parameters[index].trim());
	}

	/**
	 * Returns the type of the tuple. Usually the tupel defines a point.
	 * @return The type of the tuple
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * Returns the size of values in the tuple;
	 * @return The size of values
	 */
	public int getLength() {
		return this.parameters.length;
	}
	
	/**
	 * Calulates the euclidean distance between two tuples. 
	 * If the length of the tuples is not equal, only the higher values will be skipped.
	 * @param other The seond tuple
	 * @return The euclidean distance
	 */
	public double distance(Tuple other) {
		int n = Math.min(parameters.length, other.getLength());
		double distance = 0;
		
		for(int i = 0; i < n;i ++) {
			double difference = Double.parseDouble(parameters[i]) - other.getValue(i).doubleValue();
			distance = Math.sqrt(Math.pow(difference, 2) + Math.pow(distance, 2));
		}
		return distance;
	}
}
