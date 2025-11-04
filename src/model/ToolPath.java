package model;

import java.math.BigDecimal;
import java.util.ArrayList;

public class ToolPath extends ArrayList<Tuple> {
	
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
		add(new Tuple(new double[]{x, y}, Tuple.POINT));
	}
	
	/**
	 * Returns the x value of the coordinate at the index of the toolPath.
	 * @param index The index
	 * @return The x value
	 */
	public BigDecimal getX(int index) {
		return get(index).getValue(0);
	}
	
	/**
	 * Returns the y value of the coordinate at the index of the toolPath.
	 * @param index The index
	 * @return The y value
	 */
	public BigDecimal getY(int index) {
		return get(index).getValue(1);
	}
	
	/**
	 * Returns the name of the toolPath.
	 * @return The name
	 */
	public String getName() {
		return name;
	}
}
