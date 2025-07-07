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

import javax.swing.JPanel;

import misc.Settings;
import model.Program;

/**
 * This class paints the canvas where the G moves are displayed.
 * @author Christian Kirsch
 */
public class GraphicViewCanvas extends JPanel implements ProgramModelListener {
	
	private static final long serialVersionUID = 1L;
	private Program programModel;
	private boolean gridVisible, g0lineVisible, g1lineVisible, pointVisible;
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
		
		GraphicViewDragListener dragger = new GraphicViewDragListener(graphicView);
		this.addMouseListener(dragger);
		this.addMouseMotionListener(dragger);
	}
	
	/**
	 * Paints the canvas.
	 */
	@Override
    public void paint(Graphics g){
    	double x1 = Settings.workbench.getXMin(), y1 = Settings.workbench.getYMin(), x2 = 0, y2 = 0;
    	int index = -1;
    	boolean draw; // check if a line draw is needed for x and y. (i.e. a G0 Z6 does not need a draw)
    	super.paint(g);
    	
    	// Paint workbench rectangle
    	g.setColor(Color.WHITE);
    	g.fillRect(0, this.getHeight() - Settings.workbench.getYDimension() * graphicView.getScale() - (graphicView.getyBar().getValue() + graphicView.getyBar().getVisibleAmount()), (Settings.workbench.getXDimension() * graphicView.getScale()) - graphicView.getxBar().getValue(), Settings.workbench.getYDimension() * graphicView.getScale());
    	
    	// Paint all G0 and G1 moves
        for(int i = 0; i < programModel.size(); i++) {
        	draw = false;
        	if(programModel.getLine(i).getField(0).toString().equals("G0")) {
	        		
        		index = programModel.getLine(i).getFieldIndex('X');
        		if(index > -1) {
        			x2 = programModel.getLine(i).getField(index).getNumber().doubleValue();
        			draw = true;
        		}
        		index = programModel.getLine(i).getFieldIndex('Y');
        		if(index > -1) {
        			y2 = programModel.getLine(i).getField(index).getNumber().doubleValue();
        			draw = true;
        		}
        		if(draw) {
        			if(g0lineVisible ) {
        				g.setColor(Color.GREEN);
        				g.drawLine(GraphicViewHelpers.convertX(x1, graphicView.getScale(), graphicView.getxBar()), 
        						GraphicViewHelpers.convertY(y1, this.getHeight(), graphicView.getScale(), graphicView.getyBar()), 
        						GraphicViewHelpers.convertX(x2, graphicView.getScale(), graphicView.getxBar()), 
        						GraphicViewHelpers.convertY(y2, this.getHeight(), graphicView.getScale(), graphicView.getyBar()));
        			}
        			x1 = x2;
                	y1 = y2;
        		}
        		
        	} else if(programModel.getLine(i).getField(0).toString().equals("G1")) {
        		index = programModel.getLine(i).getFieldIndex('X');
        		if(index > -1) {
        			x2 = programModel.getLine(i).getField(index).getNumber().doubleValue();
        			draw = true;
        		}
        		index = programModel.getLine(i).getFieldIndex('Y');
        		if(index > -1) {
        			y2 = programModel.getLine(i).getField(index).getNumber().doubleValue();
        			draw = true;
        		}
        		
    			if(pointVisible) {
	        		g.setColor(Color.RED);
        			g.drawOval(GraphicViewHelpers.convertX(x1, graphicView.getScale(), graphicView.getxBar()) - 2, 
        						GraphicViewHelpers.convertY(y1, this.getHeight(), graphicView.getScale(), graphicView.getyBar()) - 2,
        						4, 4);
    			}
        		if(draw) {
        			if(g1lineVisible) {
        				g.setColor(Color.BLACK);
	        			g.drawLine(GraphicViewHelpers.convertX(x1, graphicView.getScale(), graphicView.getxBar()),
	        						GraphicViewHelpers.convertY(y1, this.getHeight(), graphicView.getScale(), graphicView.getyBar()),
	    							GraphicViewHelpers.convertX(x2, graphicView.getScale(), graphicView.getxBar()),
	    							GraphicViewHelpers.convertY(y2, this.getHeight(), graphicView.getScale(), graphicView.getyBar()));
        			}
        			x1 = x2;
            		y1 = y2;
            		
            		// Draw the last point from G1 move but not the zero point
           			try {
	            		if(pointVisible && !programModel.getLine(i + 1).getField(0).toString().equals("G1")) {
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
           			}
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
