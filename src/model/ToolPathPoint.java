package model;

/**
 * This class represents a point in the 2D toolpath. Every point must have x and y values and can have a comment.
 */

public class ToolPathPoint {
	
	private double x;
	private double y;
	private String comment;
	
	public ToolPathPoint(double x, double y, String comment) {
		this(x,y);
		this.comment = comment;
	}
	
	public ToolPathPoint(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public String getComment() {
		return comment;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}

}
