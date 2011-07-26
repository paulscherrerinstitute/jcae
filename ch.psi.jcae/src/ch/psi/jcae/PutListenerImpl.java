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

package ch.psi.jcae;

import java.util.concurrent.CountDownLatch;

import gov.aps.jca.CAStatus;
import gov.aps.jca.event.PutEvent;
import gov.aps.jca.event.PutListener;

/**
 * Utility class implementing @see gov.aps.jca.PutListener used to accomplish an 
 * asynchronous put. The class decrements the passed latch once
 * the put operation has finished successfully.
 * @author ebner
 *
 */
public class PutListenerImpl implements PutListener
{
	/**
	 * Latch to decrement if successfull event occurrs
	 */
	private final CountDownLatch latch;
	
	/**
	 * Default constructor
	 * @param latch	Latch to decrement if event occurs
	 */
	public PutListenerImpl(CountDownLatch latch){
		this.latch = latch;
	}

	@Override
	public void putCompleted(PutEvent ev) {
	    if(ev.getStatus() == CAStatus.NORMAL){
	    	latch.countDown();
	    }
	}
}
