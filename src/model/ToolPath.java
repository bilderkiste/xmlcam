package model;

import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.util.ArrayList;

public class ToolPath extends ArrayList<Point2D.Double> {
	
	private static final long serialVersionUID = 1L;
	protected String name;

	public ToolPath(String name) {
		super();
		this.name = name;
	}
	
	/**
	 * Adds a new coordinate to the toolPath.
	 * @param coords The new coordinates.
	 */
	public void addPoint(double x, double y) {
		add(new Point2D.Double(x, y));
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
	
	public int sze() {
		System.out.println(size());
		return 0;
	}
	
}
