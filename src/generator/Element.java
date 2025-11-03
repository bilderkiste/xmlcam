package generator;

import java.util.ArrayList;

import org.w3c.dom.Node;

import controller.Generator;
import controller.Tuple;
import model.Tool;
import model.ToolPathPoint;

abstract class Element {
	
	protected Generator gen;
	protected Node node;
	protected ArrayList<ToolPathPoint> toolPath = new ArrayList<ToolPathPoint>();
	protected Tuple zLevel;
	protected Tool tool;
	
	public Element(Node node, Generator gen) {
		this.node = node;
		this.gen = gen;
		this.tool = null;
	}
	
	public abstract void extract() throws IllegalArgumentException;
	
	public abstract void execute();
	
	public ArrayList<ToolPathPoint> getToolPath() {
		return toolPath;
	}
		
	public Tuple getZLevel() {
		return zLevel;
	}
	
	/**
	 * Adds the translation to the x and y values of a point.
	 * @param pint The point.
	 * @return The point translated.
	 */
	protected Tuple addTranslation(Tuple point) {
		point.setValue(0, point.getValue(0).doubleValue() + gen.getTranslateX().doubleValue());
		point.setValue(1, point.getValue(1).doubleValue() + gen.getTranslateY().doubleValue());
		return point;
	}

}
