package model;

/**
 * This class represents the milling tool and defines the properties of the tool. 
 */

public class Tool {

	private String id;
	private String type;
	/**
	 * The diameter of the milling tool.
	 */
	private double diameter;
	
	
	public Tool(String id, double diameter) {
		this(id, diameter, null);
	}
	
	public Tool(String id, double diameter, String type) {
		this.id = id;
		this.diameter = diameter;
		this.type = type;
	}
	
	public String getId() {
		return id;
	}
	
	public double getDiameter() {
		return diameter;
	}
	
	public double getRadius() {
		return diameter / 2;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String toString( ) {
		return new String("Tool " + id + ": diameter " + diameter + " type " + type );
	}
}
