/*********************************************************************\
 * GraphicViewComponentListener.java - xmlCam G-Code Generator       *
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

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * This class calculates the scrollbar extends for the graphic view, if the component has been resized.
 * @author Christian Kirsch
 */

public class GraphicViewComponentListener implements ComponentListener {
	
	private GraphicView graphicView;

	public GraphicViewComponentListener(GraphicView graphicView) {
		this.graphicView = graphicView;
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {
	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
	}

	@Override
	public void componentResized(ComponentEvent arg0) {
		graphicView.getxBar().setVisibleAmount(graphicView.getWidth() - 60);
		if(graphicView.getyBar().getValue() * -1 <= graphicView.getyBar().getVisibleAmount()) {
			graphicView.getyBar().setValue((graphicView.getHeight() - 60) * -1);
		}
		graphicView.getyBar().setVisibleAmount(graphicView.getHeight() - 60);
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
	}

}
