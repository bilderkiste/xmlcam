package model;

/**
 * This class represents the milling tool and defines the properties of the tool. 
 */

public class Tool {

	/**
	 * The diameter of the milling tool.
	 */
	private double diameter;
	
	public Tool(double diameter) {
		this.diameter = diameter;
	}
	
	public double getDiameter() {
		return diameter;
	}
	
	public double getRadius() {
		return diameter / 2;
	}
}
