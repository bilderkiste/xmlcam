/*********************************************************************\
 * JScrollBarInv.java - xmlCam G-Code Generator                      *
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

package misc;

import javax.swing.JScrollBar;

/**
 * This class implements a reversed JScrollBar. That means, that minimum is on bottom or right and maximum on top or left.
 */
public class JScrollBarInv extends JScrollBar {

	private static final long serialVersionUID = 1L;
	
	public JScrollBarInv(int orientation, int value, int extent, int min, int max) {
		super(orientation, min - extent, extent, max * -1, min * -1);
	}
	
	@Override
	public int getValue() {
		return (super.getValue() + super.getVisibleAmount()) * -1;
	}

	@Override
	public void setMaximum(int maximum) {
		super.setMinimum(maximum * -1);
	}
	
	@Override
	public void setMinimum(int minimum) {
		super.setMaximum(minimum * -1);
	}
	

	
	
}
