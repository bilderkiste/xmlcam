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
import javax.swing.JScrollBar;
import javax.swing.JTextField;

import misc.Settings;
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
	private JScrollBar xBar, yBar;
	private JTextField zoomDisplay;
	/**
	 * The scale for the zoom level.
	 */
	private int scale;
	
	/**
	 * Constructs a GrahpicView including the canvas and the rulers.
	 * @param programModel The program model, that holds the G-Code
	 */
	public GraphicView(Program programModel) {
		this.programModel = programModel;
		
		this.scale = 1;

		this.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		
		GraphicViewRuler rulerY = new GraphicViewRuler(GraphicViewRuler.Y_RULER, this);
		rulerY.setMinimumSize(new Dimension(40, Settings.workbench.getYDimension()));
		rulerY.setPreferredSize(new Dimension(40, Settings.workbench.getYDimension()));
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		this.add(rulerY, constraints);
		
		graphicViewCanvas = new GraphicViewCanvas(this.programModel, this);
		programModel.addProgrammModelListener(graphicViewCanvas);
		graphicViewCanvas.setMinimumSize(new Dimension(Settings.workbench.getXDimension(), Settings.workbench.getYDimension()));
		graphicViewCanvas.setPreferredSize(new Dimension(Settings.workbench.getXDimension(), Settings.workbench.getYDimension()));
		
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		this.add(graphicViewCanvas, constraints);
		
		GraphicViewRuler rulerX = new GraphicViewRuler(GraphicViewRuler.X_RULER, this);
		rulerX.setMinimumSize(new Dimension(Settings.workbench.getXDimension(), 40));
		rulerX.setPreferredSize(new Dimension(Settings.workbench.getXDimension(), 40));
		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.weightx = 0;
		constraints.weighty = 0;
		this.add(rulerX, constraints);
		
		GraphicViewAdjustmentListener adjustmentListener = new GraphicViewAdjustmentListener(this);
		
		xBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 1, 0, Settings.workbench.getXDimension());
		xBar.setPreferredSize(new Dimension(Settings.workbench.getXDimension(), 16));
		xBar.addAdjustmentListener(adjustmentListener);
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.weightx = 0;
		constraints.weighty = 0;
		this.add(xBar, constraints);
		
		yBar = new JScrollBar(JScrollBar.VERTICAL, 0, 1, 0, Settings.workbench.getYDimension());
		yBar.setPreferredSize(new Dimension(16, Settings.workbench.getYDimension()));
		yBar.addAdjustmentListener(adjustmentListener);

		constraints.gridx = 2;
		constraints.gridy = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		this.add(yBar, constraints);
		
		zoomDisplay = new JTextField();
		this.setZoomDisplay(this.scale);
		zoomDisplay.setEditable(false);
		constraints.gridx = 0;
		constraints.gridy = 2;
		this.add(zoomDisplay, constraints);	
		
		this.addComponentListener(new GraphicViewComponentListener(this));
	}

	/**
	 * Returns the canvas of the GraphicView.
	 * @return The canvas
	 */
	public GraphicViewCanvas getGraphicViewCanvasView() {
		return graphicViewCanvas;
	}

	/**
	 * Gets the zoom level.
	 * 100% equals scale = 1;
	 * @return The scale
	 */
	public int getScale() {
		return scale;
	}

	/**
	 * Sets the zoom level.
	 * 100% equals scale = 1;
	 * @param The new scale
	 */
	public void setScale(int scale) {
		if(scale >= 1) {
			this.scale = scale;
			this.setZoomDisplay(scale);
		}
	}

	/**
	 * Returns the reference from the x scrollbar.
	 * @return The x scrollbar
	 */
	public JScrollBar getxBar() {
		return xBar;
	}

	/**
	 * Returns the reference from the y scrollbar.
	 * @return The y scrollbar
	 */
	public JScrollBar getyBar() {
		return yBar;
	}

	/**
	 * Sets the zoom display with the zoomlevel.
	 * @param zoomDisplay The scale
	 */
	private void setZoomDisplay(int scale) {
		this.zoomDisplay.setText(scale + "x");
	}
	
}
