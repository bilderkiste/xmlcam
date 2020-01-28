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

import misc.Settings;

/**
 * This class implements the actions for the components located in the GraphicView.
 * @author Christian Kirsch
 */

public class GraphicViewActionListener implements ActionListener {
	
	private GraphicView graphicView;
	
	/**
	 * Constructs a new listener.
	 * @param graphicView The canvas (or panel), that paint the view
	 */
	public GraphicViewActionListener(GraphicView graphicView) {
		this.graphicView = graphicView;
	}

	/**
	 * Gets invoked by an action from the components of the GraphicView.
	 */
	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		AbstractButton actionButton = (AbstractButton) actionEvent.getSource();
	
		if(actionButton.getActionCommand() == "show_grid") {
			graphicView.getGraphicViewCanvasView().setGridVisible(actionButton.isSelected());
			graphicView.repaint();
		} else if(actionButton.getActionCommand() == "zoom_in") {
			graphicView.setScale(graphicView.getScale() + 1);
			graphicView.getxBar().setMaximum(Settings.workbench.getXDimension() * graphicView.getScale());
			graphicView.getyBar().setMaximum(Settings.workbench.getYDimension() * graphicView.getScale());
			graphicView.setZoomDisplay(graphicView.getScale());
			graphicView.repaint();
			System.out.println(graphicView.getxValue());
		} else if(actionButton.getActionCommand() == "zoom_out") {
			graphicView.setScale(graphicView.getScale() - 1);
			graphicView.getxBar().setMaximum(Settings.workbench.getXDimension() * graphicView.getScale());
			graphicView.getyBar().setMaximum(Settings.workbench.getYDimension() * graphicView.getScale());
			graphicView.setZoomDisplay(graphicView.getScale());
			graphicView.repaint();
			System.out.println(graphicView.getxValue());
		}
	}

}
