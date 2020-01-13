/*********************************************************************\
 * GraphicView.java - xmlCam G-Code Generator                        *
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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import main.Settings;
import model.Program;

/**
 * This class implements the GraphicView.
 * The GraphicView displays all G-Code moves stored in the G-Code program model.
 * @author Christian Kirsch
 *
 */
public class GraphicView extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private Program programModel;
	private GraphicViewCanvas graphicViewCanvas;
	
	/**
	 * Constructs a GrahpicView including the canvas and the rulers.
	 * @param programModel The program model, that holds the G-Code
	 */
	public GraphicView(Program programModel) {
		this.programModel = programModel;

		this.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		
		GraphicViewRuler rulerY = new GraphicViewRuler(GraphicViewRuler.Y_RULER);
		rulerY.setMinimumSize(new Dimension(40, Settings.workbench.getYDimension()));
		rulerY.setPreferredSize(new Dimension(40, Settings.workbench.getYDimension()));
		constraints.gridx = 0;
		constraints.gridy = 0;
		this.add(rulerY, constraints);
		
		graphicViewCanvas = new GraphicViewCanvas(this.programModel);
		programModel.addProgrammModelListener(graphicViewCanvas);
		graphicViewCanvas.setMinimumSize(new Dimension(Settings.workbench.getXDimension(), Settings.workbench.getYDimension()));
		graphicViewCanvas.setPreferredSize(new Dimension(Settings.workbench.getXDimension(), Settings.workbench.getYDimension()));
		
		constraints.gridx = 1;
		constraints.gridy = 0;
		this.add(graphicViewCanvas, constraints);
		
		GraphicViewRuler rulerX = new GraphicViewRuler(GraphicViewRuler.X_RULER);
		rulerX.setMinimumSize(new Dimension(Settings.workbench.getXDimension(), 40));
		rulerX.setPreferredSize(new Dimension(Settings.workbench.getXDimension(), 40));
		constraints.gridx = 1;
		constraints.gridy = 1;
		this.add(rulerX, constraints);
	}

	/**
	 * Returns the canvas of the GraphicView.
	 * @return The canvas
	 */
	public GraphicViewCanvas getGraphicViewCanvasView() {
		return graphicViewCanvas;
	}

}
