/*********************************************************************\
 * GraphicViewActionListener.java - xmlCam G-Code Generator          *
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;

import model.Environment;

/**
 * This class implements the actions for the components located in the GraphicView.
 * @author Christian Kirsch
 */

public class GraphicViewActionListener implements ActionListener {
	
	private GraphicView graphicView;
	private Environment env;
	
	/**
	 * Constructs a new listener.
	 * @param graphicView The canvas (or panel), that paint the view
	 */
	public GraphicViewActionListener(Environment env, GraphicView graphicView) {
		this.graphicView = graphicView;
		this.env = env;
	}

	/**
	 * Gets invoked by an action from the components of the GraphicView.
	 */
	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		AbstractButton actionButton = (AbstractButton) actionEvent.getSource();
		int scale = 0;
		if(actionButton.getActionCommand() == "zoom_in") {
			scale = graphicView.getScale();
			if(scale < 50) {
				scale += 1;
			}
			graphicView.setScale(scale);
			graphicView.getxBar().setMaximum(env.getSettings().getWorkbench().getXDimension() * graphicView.getScale());
			graphicView.getyBar().setMinimum(env.getSettings().getWorkbench().getYDimension() * graphicView.getScale() * -1);
			
			//System.out.println("bef: " + graphicView.getxBar().getValue() + ":" + graphicView.getyBar().getValue());
			//TODO: Improvement of value setting if zoomlevel has changed
			//graphicView.getxBar().setValue((graphicView.getxBar().getValue() / graphicView.getScale()) * (graphicView.getScale() + 1));
			//graphicView.getyBar().setValue(((graphicView.getyBar().getValue() + graphicView.getyBar().getVisibleAmount()) / graphicView.getScale()) * (graphicView.getScale() + 1) - graphicView.getyBar().getVisibleAmount());
			//System.out.println("aft: " + graphicView.getxBar().getValue() + ":" + graphicView.getyBar().getValue());
			graphicView.repaint();
		} else if(actionButton.getActionCommand() == "zoom_out") {
			graphicView.setScale(graphicView.getScale() - 1);
			graphicView.getxBar().setMaximum(env.getSettings().getWorkbench().getXDimension() * graphicView.getScale());
			graphicView.getyBar().setMinimum(env.getSettings().getWorkbench().getYDimension() * graphicView.getScale() * -1);
			//System.out.println("bef: " + graphicView.getxBar().getValue() + ":" + graphicView.getyBar().getValue());
			//graphicView.getxBar().setValue((graphicView.getxBar().getValue() / (graphicView.getScale() + 2)) * (graphicView.getScale() + 1));
			//graphicView.getyBar().setValue(((graphicView.getyBar().getValue() + graphicView.getyBar().getVisibleAmount()) / (graphicView.getScale() + 2)) * (graphicView.getScale() + 1) - graphicView.getyBar().getVisibleAmount());
			//System.out.println("aft: " + graphicView.getxBar().getValue() + ":" + graphicView.getyBar().getValue());
			graphicView.repaint();
		}
	}

}
