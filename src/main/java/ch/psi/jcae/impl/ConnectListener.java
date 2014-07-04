package ch.psi.jcae.impl;

import java.util.concurrent.CountDownLatch;

import gov.aps.jca.event.ConnectionEvent;
import gov.aps.jca.event.ConnectionListener;

/**
 * Listener to decrement the passed latch if a connection was established 
 */
public class ConnectListener implements ConnectionListener {

	/**
	 * Thread save counter object that is also used for synchronizations
	 */
	private final CountDownLatch latch;
	
	public ConnectListener(CountDownLatch latch){
		this.latch = latch;
	}

	@Override
	public void connectionChanged(ConnectionEvent event) {
		if(event.isConnected()){
			// Decrement latch
			latch.countDown();
		}
	}
}
