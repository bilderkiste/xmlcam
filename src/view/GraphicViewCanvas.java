/*********************************************************************\
 * GraphicViewCanvas.java - xmlCam G-Code Generator                  *
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

package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JPanel;

import misc.Settings;
import model.Program;
import model.ToolPath;

/**
 * This class paints the canvas where the G moves are displayed.
 * @author Christian Kirsch
 */
public class GraphicViewCanvas extends JPanel implements ProgramModelListener {
	
	private static final long serialVersionUID = 1L;
	private Program programModel;
	private boolean gridVisible, g0lineVisible, g1lineVisible, pointVisible, shapeVisible;
	private GraphicView graphicView;
	
	/**
	 * Constructs a new canvas.
	 * @param programModel The program model with G-Code to be displayed
	 */
	public GraphicViewCanvas(Program programModel, GraphicView graphicView) {
		this.programModel = programModel;
		this.graphicView = graphicView;
		this.setBackground(Color.DARK_GRAY);
		this.gridVisible = true;
		this.g0lineVisible = true;
		this.g1lineVisible = true;
		this.pointVisible = true;
		this.shapeVisible = true;
		
		GraphicViewDragListener dragger = new GraphicViewDragListener(graphicView);
		this.addMouseListener(dragger);
		this.addMouseMotionListener(dragger);
	}
	
	/**
	 * Paints the canvas.
	 */
	@Override
    public void paintComponent(Graphics g){
    	double x1 = Settings.workbench.getXMin(), y1 = Settings.workbench.getYMin(), x2 = 0, y2 = 0;
    	int index = -1;
    	boolean draw; // check if a line draw is needed for x and y. (i.e. a G0 Z6 does not need a draw)
    	super.paintComponent(g);
    	
    	Graphics2D g2 = (Graphics2D) g;
    	
    	// Canvas Transform
    	g2.translate(0, this.getHeight());
    	g2.scale(1,-1);
    	
    	// Transform for elements and workbench
		AffineTransform paintAt = new AffineTransform();
		paintAt.scale(graphicView.getScale(), graphicView.getScale());
		int yScrollBarValueInv = graphicView.getyBar().getMaximum() - graphicView.getyBar().getVisibleAmount() - graphicView.getyBar().getValue();
		paintAt.translate(0 - (graphicView.getxBar().getValue() / graphicView.getScale()), yScrollBarValueInv  / -graphicView.getScale());
		System.out.println(graphicView.getxBar().getValue() + " - " + yScrollBarValueInv);
		
		// Antialiasing aktivieren
		//g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    	// Paint workbench rectangle
    	g2.setColor(Color.WHITE);
    	Rectangle2D workbench = new Rectangle2D.Double(0, 0, Settings.workbench.getXDimension(), Settings.workbench.getYDimension());
    	Shape transformedWorkbench = paintAt.createTransformedShape(workbench);
    	g2.fill(transformedWorkbench);
    	
    	// Paint shapes
    	if(shapeVisible) {
    		for(int i = 0; i < programModel.sizeElements(); i++) {
    			g2.setColor(Color.BLUE);
    			AffineTransform originalAt = programModel.getElement(i).getTransform();

    			Path2D.Double shape = new Path2D.Double(programModel.getElement(i).getShape(), originalAt);
    			shape.transform(paintAt);
    			g2.draw(shape);
    		}
    	};
    	
    	if(shapeVisible) {
    		for(int i = 0; i < programModel.sizeElements(); i++) {
    			Path2D.Double shape = programModel.getElement(index).getShape();
    			PathIterator pi = shape.getPathIterator(null, 0.1); 
    		    
    		    double[] coords = new double[2];
    		    double x1, y1;
    		    while (!pi.isDone()) {
    	        	int segmentType = pi.currentSegment(coords);
    	        	if(segmentType == PathIterator.SEG_MOVETO) {
    	        		x1 = coords[0];
    	        		
    	        	} else if(segmentType == PathIterator.SEG_LINETO) {
    	        		tpl.get(tpl.size() - 1).addPoint(coords[0], coords[1]);
    	        	} else if(segmentType == PathIterator.SEG_CLOSE) {
    	        		tpl.get(tpl.size() - 1).addPoint(startCoords.getX(), startCoords.getY());
    	        	} 
    	        
    	            //System.out.println(segmentType + " - " + coords[0] + " " + coords[1]);// +" " + coords[2]+ " " + coords[3] +" " + coords[4] + " " + coords[5]);
    	            pi.next();
    		    }
    		    	};
    	
    	// Paint all G0 and G1 moves
        for(int i = 0; i < programModel.sizeRow(); i++) {
        	draw = false;
        	if(programModel.getRow(i).getField(0).toString().equals("G0")) {
	        		
        		index = programModel.getRow(i).getFieldIndex('X');
        		if(index > -1) {
        			x2 = programModel.getRow(i).getField(index).getNumber().doubleValue();
        			draw = true;
        		}
        		index = programModel.getRow(i).getFieldIndex('Y');
        		if(index > -1) {
        			y2 = programModel.getRow(i).getField(index).getNumber().doubleValue();
        			draw = true;
        		}
        		if(draw) {
        			if(g0lineVisible ) {
        				g2.setColor(Color.GREEN);
        				g2.drawLine((int)(x1 * graphicView.getScale() - graphicView.getxBar().getValue()), 
        						(int)(y1 * graphicView.getScale() - yScrollBarValueInv), 
        						(int)(x2 * graphicView.getScale() - graphicView.getxBar().getValue()) , 
        						(int)(y2 * graphicView.getScale() - yScrollBarValueInv));
        			}
        			x1 = x2;
                	y1 = y2;
        		}
        		
        	} else if(programModel.getRow(i).getField(0).toString().equals("G1")) {
        		index = programModel.getRow(i).getFieldIndex('X');
        		if(index > -1) {
        			x2 = programModel.getRow(i).getField(index).getNumber().doubleValue();
        			draw = true;
        		}
        		index = programModel.getRow(i).getFieldIndex('Y');
        		if(index > -1) {
        			y2 = programModel.getRow(i).getField(index).getNumber().doubleValue();
        			draw = true;
        		}
        		
    			if(pointVisible) {
	        		g2.setColor(Color.RED);
        			g2.drawOval((int)(x1 * graphicView.getScale() - graphicView.getxBar().getValue()) - 2, 
    						(int)(y1 * graphicView.getScale() - yScrollBarValueInv) - 2, 4, 4);
    			}
        		if(draw) {
        			if(g1lineVisible) {
        				g2.setColor(Color.BLACK);
        				g2.drawLine((int)(x1 * graphicView.getScale() - graphicView.getxBar().getValue()), 
        						(int)(y1 * graphicView.getScale() - yScrollBarValueInv), 
        						(int)(x2 * graphicView.getScale() - graphicView.getxBar().getValue()) , 
        						(int)(y2 * graphicView.getScale() - yScrollBarValueInv));
        			}
        			x1 = x2;
            		y1 = y2;
            		
            		// Draw the last point from G1 move but not the zero point
           			/*try {
	            		if(pointVisible && !programModel.getRow(i + 1).getField(0).toString().equals("G1")) {
	    	        		g.setColor(Color.RED);
	            			g.drawOval(GraphicViewHelpers.convertX(x1, graphicView.getScale(), graphicView.getxBar()) - 2, 
	            						GraphicViewHelpers.convertY(y1, this.getHeight(), graphicView.getScale(), graphicView.getyBar()) - 2,
	            						4, 4);
	            		}
           			} catch(IndexOutOfBoundsException e) {
           					g.setColor(Color.RED);
           					g.drawOval(GraphicViewHelpers.convertX(x1, graphicView.getScale(), graphicView.getxBar()) - 2, 
            						GraphicViewHelpers.convertY(y1, this.getHeight(), graphicView.getScale(), graphicView.getyBar()) - 2,
            						4, 4);
           			}*/
        		}
        	}
        }
        
        if(gridVisible) {
        	paintGrid(g);
        }
    	        
    } 
	
	/**
	 * Paints the grid.
	 * @param g The graphics object
	 */
	private void paintGrid(Graphics g) {
		g.setColor(Color.DARK_GRAY);
		
		for(int i = 0; i < Settings.workbench.getXDimension() * graphicView.getScale(); i += Settings.gridStep) {
			for(int j = 0; j < Settings.workbench.getYDimension() * graphicView.getScale(); j += Settings.gridStep) {
				g.drawLine(i - graphicView.getxBar().getValue(), this.getHeight() - j - (graphicView.getyBar().getValue() + graphicView.getyBar().getVisibleAmount()),
						i - graphicView.getxBar().getValue(), this.getHeight() - j - (graphicView.getyBar().getValue() + graphicView.getyBar().getVisibleAmount()));
			}
		}
	}
	
	/**
	 * Returns if the grid is visible or not.
	 * @return True if visible
	 */
	public boolean isGridVisible() {
		return gridVisible;
	}

	/**
	 * Sets that the grid is visible or not.
	 * If it is visible, the paint function will draw the grid.
	 * @param gridVisible Set true for visible
	 */
	public void setGridVisible(boolean gridVisible) {
		this.gridVisible = gridVisible;
	}

	/**
	 * Returns if the G0 moves are visible or not.
	 * @return True if visible
	 */
	public boolean isG0lineVisible() {
		return g0lineVisible;
	}

	/**
	 * Sets that the G0 moves are visible or not.
	 * If it is visible, the paint function will draw moves.
	 * @param gridVisible Set true for visible
	 */
	public void setG0lineVisible(boolean g0lineVisible) {
		this.g0lineVisible = g0lineVisible;
	}

	/**
	 * Returns if the G1 moves are visible or not.
	 * @return True if visible
	 */
	public boolean isG1lineVisible() {
		return g1lineVisible;
	}

	/**
	 * Sets that the G1 moves are visible or not.
	 * If it is visible, the paint function will draw moves.
	 * @param gridVisible Set true for visible
	 */
	public void setG1lineVisible(boolean g1lineVisible) {
		this.g1lineVisible = g1lineVisible;
	}

	/**
	 * Returns if the G1 points are visible or not.
	 * @return True if visible
	 */
	public boolean isPointVisible() {
		return pointVisible;
	}

	/**
	 * Sets that the G1 points are visible or not.
	 * If it is visible, the paint function will draw the points.
	 * @param gridVisible Set true for visible
	 */
	public void setPointVisible(boolean pointVisible) {
		this.pointVisible = pointVisible;
	}

	/**
	 * Gets invoked each time a change in G-Code program model occurs.
	 * @param model The model, where the change occurs  
	 */
	@Override
	public void modelChanged(Program model) {
		this.repaint();
	}

}
