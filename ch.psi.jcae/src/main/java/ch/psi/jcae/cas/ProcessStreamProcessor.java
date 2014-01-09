/**
 * 
 * Copyright 2011 Paul Scherrer Institute. All rights reserved.
 * 
 * This code is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This code is distributed in the hope that it will be useful, but without any
 * warranty; without even the implied warranty of merchantability or fitness for
 * a particular purpose. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this code. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package ch.psi.jcae.cas;

import java.io.*;
import java.util.logging.Logger;

/**
 * Runnable to read out data from an input stream
 * @author ebner
 *
 */
public class ProcessStreamProcessor implements Runnable {
	
	
	private static Logger logger = Logger.getLogger(ProcessStreamProcessor.class.getName());
	
	InputStream is;

	/**
	 * Constructor
	 * @param is	InputStream to read out
	 */
	ProcessStreamProcessor(InputStream is) {
		this.is = is;
	}

	public void run() {
		try {
			// Read from input stream and terminate if end of stream is reached
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null){
				logger.info(line);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
