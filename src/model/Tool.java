package model;

public class Tool {

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
