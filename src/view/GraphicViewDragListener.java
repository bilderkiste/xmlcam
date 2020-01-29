/*********************************************************************\
 * GraphicViewDrag.java - xmlCam G-Code Generator                    *
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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class GraphicViewDragListener implements MouseListener, MouseMotionListener {
	
	private GraphicView graphicView;
	private int xPressed, yPressed;
	
	public GraphicViewDragListener(GraphicView graphicView) {
		this.graphicView = graphicView;
		xPressed = 0;
		yPressed = 0;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent event) {
		xPressed = event.getX();
		yPressed = event.getY();
	}

	@Override
	public void mouseReleased(MouseEvent event) {
	}

	@Override
	public void mouseDragged(MouseEvent event) {
		graphicView.getxBar().setValue(graphicView.getxBar().getValue() - (event.getX() - xPressed));
		xPressed = event.getX();
		graphicView.getyBar().setValue(graphicView.getyBar().getValue() - (event.getY() - yPressed));
		yPressed = event.getY();
	}

	@Override
	public void mouseMoved(MouseEvent event) {
	}

}
