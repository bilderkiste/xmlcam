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

import main.Settings;
import model.Program;

/**
 * This class paints the canvas where the G moves are displayed.
 * @author Christian Kirsch
 */
public class GraphicViewCanvas extends JPanel implements ProgramModelListener {
	
	private static final long serialVersionUID = 1L;
	private Program programModel;
	private boolean gridVisible;
	
	/**
	 * Constructs a new canvas.
	 * @param programModel The program model with G-Code to be displayed
	 */
	public GraphicViewCanvas(Program programModel) {
		this.programModel = programModel;
		this.setBackground(Color.GRAY);
		this.gridVisible = false;
	}
	
	/**
	 * Paints the canvas.
	 */
	@Override
    public void paint(Graphics g){
    	int x1 = Settings.workbench.getXMin(), y1 = Settings.workbench.getYMin(), x2 = 0, y2 = 0, index = -1;
    	boolean draw; // check if a line draw is needed for x and y. (i.e. a G0 Z6 does not need a draw)
    	
    	super.paint(g);
    	
    	// Paint workbench rectangle
    	g.setColor(Color.WHITE);
    	g.fillRect(0, this.getHeight() - Settings.workbench.getYDimension(), Settings.workbench.getXDimension(), Settings.workbench.getYDimension());
    	
    	// Paint all G0 and G1 moves
        for(int i = 0; i < programModel.getLineSize(); i++) {
        	draw = false;
        	if(programModel.getLine(i).getField(0).toString().equals("G0")) {
        		g.setColor(Color.GREEN);
        		index = programModel.getLine(i).getFieldIndex('X');
        		if(index > -1) {
        			x2 = programModel.getLine(i).getField(index).getNumber().intValue();
        			draw = true;
        		}
        		index = programModel.getLine(i).getFieldIndex('Y');
        		if(index > -1) {
        			y2 = programModel.getLine(i).getField(index).getNumber().intValue();
        			draw = true;
        		}
        		if(draw) {
        			g.drawLine(GraphicViewHelpers.convertX(x1), GraphicViewHelpers.convertY(y1, this.getHeight()), GraphicViewHelpers.convertX(x2), GraphicViewHelpers.convertY(y2, this.getHeight()));
            		x1 = x2;
            		y1 = y2;
        		}

        	}
        	
        	if(programModel.getLine(i).getField(0).toString().equals("G1")) {
        		g.setColor(Color.BLACK);
        		index = programModel.getLine(i).getFieldIndex('X');
        		if(index > -1) {
        			x2 = programModel.getLine(i).getField(index).getNumber().intValue();
        			draw = true;
        		}
        		index = programModel.getLine(i).getFieldIndex('Y');
        		if(index > -1) {
        			y2 = programModel.getLine(i).getField(index).getNumber().intValue();
        			draw = true;
        		}
        		if(draw) {
        			g.drawLine(GraphicViewHelpers.convertX(x1), GraphicViewHelpers.convertY(y1, this.getHeight()), GraphicViewHelpers.convertX(x2), GraphicViewHelpers.convertY(y2, this.getHeight()));
            		x1 = x2;
            		y1 = y2;
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
		
		for(int i = 0; i < Settings.workbench.getXDimension(); i += Settings.step) {
			for(int j = 0; j < Settings.workbench.getYDimension(); j += Settings.step) {
				g.drawLine(i, this.getHeight() - j, i, this.getHeight() - j);
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
	 * @param gridVisible
	 */
	public void setGridVisible(boolean gridVisible) {
		this.gridVisible = gridVisible;
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
