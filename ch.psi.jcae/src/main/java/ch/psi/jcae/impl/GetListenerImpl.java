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

import gov.aps.jca.CAStatus;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.event.GetEvent;
import gov.aps.jca.event.GetListener;

/**
 * Utility class for asynchronous get operation on a Channel Access channel
 * @author ebner
 *
 */
public class GetListenerImpl implements GetListener
{
	/**
	 * Channel DBR value
	 */
    private DBR value;
    
    private final CountDownLatch latch;
    
    /**
     * Constructor
     * @param latch
     */
    public GetListenerImpl(CountDownLatch latch){
    	this.latch = latch;
    }
    
    @Override
    public void getCompleted(GetEvent ev) {
	    value = ev.getDBR();
	    
	    if (ev.getStatus() == CAStatus.NORMAL){
		    latch.countDown();
	    }
	}
    
    /**
     * Get value returned by the answer to the get request.
     * @return	Value returned by get request
     */
    public DBR getValue() {
        return value;
    }
}
