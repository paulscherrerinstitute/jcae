/**
 * 
 * Copyright 2010 Paul Scherrer Institute. All rights reserved.
 * 
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This code is distributed in the hope that it will be useful,
 * but without any warranty; without even the implied warranty of
 * merchantability or fitness for a particular purpose. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this code. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package ch.psi.jcae.impl;

import java.util.concurrent.CountDownLatch;

import gov.aps.jca.event.ConnectionEvent;
import gov.aps.jca.event.ConnectionListener;

/**
 * Listener to decrement the passed latch if a connection was established 
 * @author ebner
 */
public class ConnectListener implements ConnectionListener {

	/**
	 * Thread save counter object that is also used for synchronizations
	 */
	private final CountDownLatch latch;
	
	/**
	 * Constructor
	 * @param latch
	 */
	public ConnectListener(CountDownLatch latch){
		this.latch = latch;
	}

	/* (non-Javadoc)
	 * @see gov.aps.jca.event.ConnectionListener#connectionChanged(gov.aps.jca.event.ConnectionEvent)
	 */
	@Override
	public void connectionChanged(ConnectionEvent event) {
		if(event.isConnected()){
			// Decrement latch
			latch.countDown();
		}
	}
}
