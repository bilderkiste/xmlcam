package model;

/**
 * This class represents the milling tool and defines the properties of the tool. 
 */

public class Tool {

	/**
	 * The diameter of the milling tool.
	 */
	private double diameter;
	private String name;
	
	public Tool(double diameter) {
		this(diameter, null);
	}
	
	public Tool(double diameter, String name) {
		this.diameter = diameter;
		this.name = name;
	}
	
	public double getDiameter() {
		return diameter;
	}
	
	public double getRadius() {
		return diameter / 2;
	}
	
	public String toString( ) {
		return new String("Tool: diameter " + diameter + " name " + name );
	}
}
