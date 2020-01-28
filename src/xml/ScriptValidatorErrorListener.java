/*********************************************************************\
 * ScriptValidatorErrorListener.java - xmlCam G-Code Generator       *
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

package xml;

/**
 * This interface provides the method which gets invoked from the script validator when an error occurs or when no error was found.
 * @author Christian Kirsch
 *
 */

public interface ScriptValidatorErrorListener {
	
	/**
	 * Gets invoked from the script validator when an error occurs.
	 * @param errorHandler the error handler whith the information about the error
	 */
	public void errorOccured(ScriptValidatorErrorHandler errorHandler);
	
	/**
	 * Gets invoked from the script validator when when no error was found.
	 */
	public void noErrorFound();
	
}
