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
import java.util.ArrayList;

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
	
	private ArrayList<Double> values;
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
	 * @param type The type of the node or tuple
	 */
	public Tuple(Node node, int type) {
		String[] stringValues = node.getTextContent().split(",");
		this.values = new ArrayList<Double>();
		for(int i = 0; i < stringValues.length; i++) {
			values.add(Double.parseDouble(stringValues[i]));
		}
		this.type = type;
	}
	
	/**
	 * Constructs an new tuple an fills it with values from a double array.
	 * @param values The double array
	 */
	public Tuple(double[] values) {
		this(values, 0);
	}
	
	/**
	 * Constructs an new tuple an fills it with values from a double array.
	 * @param values The double array
	 * @param type The type of the node or tuple
	 */
	public Tuple(double[] values, int type) {
		this.values = new ArrayList<Double>();
		
		for(int i = 0; i < values.length; i++) {
			this.values.add(values[i]);
		}
		this.type = type;
	}
	
	
	/**
	 * Returns the value from the tuple at the specified position.
	 * @param index The position of the value
	 * @return The value as a BigDecimal object
	 */
	public BigDecimal getValue(int index) {
		return new BigDecimal(values.get(index));
	}

	/**
	 * Returns the type of the tuple. Usually the tuple defines a point.
	 * @return The type of the tuple
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * Returns the size of values in the tuple;
	 * @return The size of values
	 */
	public int size() {
		return this.values.size();
	}
	
	/**
	 * Calulates the euclidean distance between two tuples. 
	 * If the length of the tuples is not equal, only the higher values will be skipped.
	 * @param other The second tuple
	 * @return The euclidean distance
	 */
	public double distance(Tuple other) {
		int n = Math.min(values.size(), other.size());
		double distance = 0;
		
		for(int i = 0; i < n;i ++) {
			double difference = values.get(i) - other.getValue(i).doubleValue();
			distance = Math.sqrt(Math.pow(difference, 2) + Math.pow(distance, 2));
		}
		return distance;
	}
	
	/**
	 * Compares this tuple with another. Only the tuple values will compared, not the type.
	 * @param other The other tuple to compare
	 * @return true if equal false if not
	 */
	public boolean equals(Tuple other) {
		if(values.size() != other.size()) {
			return false;
		}
		for(int i = 0; i < values.size(); i++) {
			if(values.get(i) != other.getValue(i).doubleValue()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns the tuple as a string like (a0,a1,...,an).
	 */
	public String toString() {
		StringBuilder tuple = new StringBuilder("(");
		for(int i = 0; i < values.size(); i++) {
			if(i > 0) {
				tuple.append(",");
			}
			tuple.append(values.get(i));
		}
		tuple.append(")");
		return tuple.toString();
	}
}
