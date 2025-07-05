/*********************************************************************\
 * GraphicViewRuler.java - xmlCam G-Code Generator                *
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

/**
 * This class paints the rulers for the Graphic view.
 * The unit of the rulers are millimeters.
 * @author Christian Kirsch
 */

public class GraphicViewRuler extends JPanel {
	
	private static final long serialVersionUID = 1L;
	public final static int X_RULER = 0;
	public final static int Y_RULER = 1;
	public final static int Z_RULER = 2;
	
	private int axis;
	private GraphicView graphicView;
	
	/**
	 * Constructs a new ruler.
	 * @param axis The axis which the ruler shall displayed
	 */
	public GraphicViewRuler(int axis, GraphicView graphicView) {
		this.setBackground(Color.LIGHT_GRAY);
		this.axis = axis;
		this.graphicView = graphicView;
	}
	
	/**
	 * Paints the ruler.
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		if(axis == 0) {
			for(int i = Settings.workbench.getXMin() * graphicView.getScale(); i <= (Settings.workbench.getXMin() + Settings.workbench.getXDimension()) * graphicView.getScale(); i += Settings.gridStep) {
				g.drawLine(i - Settings.workbench.getXMin() * graphicView.getScale() - graphicView.getxBar().getValue(), this.getHeight(), i - Settings.workbench.getXMin() * graphicView.getScale() - graphicView.getxBar().getValue(), this.getHeight() - 30);
				g.drawString(Integer.toString(i / graphicView.getScale()), i - Settings.workbench.getXMin() * graphicView.getScale() + 2 - graphicView.getxBar().getValue(), this.getHeight() - 4);
			}
		
		} else if(axis == 1) {
			for(int i = Settings.workbench.getYMin() * graphicView.getScale(); i <= (Settings.workbench.getYMin() + Settings.workbench.getYDimension()) * graphicView.getScale(); i += Settings.gridStep) {
				g.drawLine(0, this.getHeight() - (i - Settings.workbench.getYMin() * graphicView.getScale() + (graphicView.getyBar().getValue() + graphicView.getyBar().getVisibleAmount())), 30, this.getHeight() - (i - Settings.workbench.getYMin() * graphicView.getScale() + (graphicView.getyBar().getValue() + graphicView.getyBar().getVisibleAmount())));
				g.drawString(Integer.toString(i / graphicView.getScale()), 0, this.getHeight() - (i - Settings.workbench.getYMin() * graphicView.getScale() + 2 + (graphicView.getyBar().getValue() + graphicView.getyBar().getVisibleAmount())));
			}
		}
		
	}
	
}
