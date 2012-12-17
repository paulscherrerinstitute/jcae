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

package ch.psi.jcae.examples;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import gov.aps.jca.CAException;
import ch.psi.jcae.ChannelException;
import ch.psi.jcae.ChannelService;
import ch.psi.jcae.annotation.CaChannel;
import ch.psi.jcae.impl.DefaultChannel;
import ch.psi.jcae.impl.DefaultChannelService;

public class AnnotationExample {

	public static void main(String[] args) throws InterruptedException, TimeoutException, ChannelException, CAException, ExecutionException {
		// Get channel factory
        ChannelService service = new DefaultChannelService();

        ChannelBeanContainer container = new ChannelBeanContainer();
        
        // Connect to channel(s) in the container
        service.createAnnotatedChannels(container);
        
        Double value = container.getCurrent().getValue();
        String unit = container.getUnit().getValue();
        Logger.getLogger(AnnotationExample.class.getName()).log(Level.INFO, "Current: {0} [{1}]", new Object[]{value, unit});
        
        // Disconnect channel(s) in the container
        service.destroyAnnotatedChannels(container);
        
        // Destroy context of the factory
        service.destroy();
	}
}

/**
 * Container class
 */
class ChannelBeanContainer {

	@CaChannel(type=Double.class, name="ARIDI-PCT:CURRENT", monitor=true)
	private DefaultChannel<Double> current;
	
	@CaChannel(type=String.class, name="ARIDI-PCT:CURRENT.EGU", monitor=true)
	private DefaultChannel<String> unit;

	/**
	 * @return the current
	 */
	public DefaultChannel<Double> getCurrent() {
		return current;
	}
	
	/**
	 * @return unit of the current
	 */
	public DefaultChannel<String> getUnit() {
		return unit;
	}
}
