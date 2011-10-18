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

import java.util.logging.Level;
import java.util.logging.Logger;
import gov.aps.jca.CAException;
import ch.psi.jcae.ChannelBean;
import ch.psi.jcae.ChannelBeanFactory;
import ch.psi.jcae.annotation.CaChannel;
import ch.psi.jcae.annotation.CaPostDestroy;
import ch.psi.jcae.annotation.CaPostInit;
import ch.psi.jcae.annotation.CaPreDestroy;
import ch.psi.jcae.annotation.CaPreInit;

public class CompleteAnnotationExample {

	public static void main(String[] args) throws CAException, InterruptedException {
		// Get channel factory
        ChannelBeanFactory factory = ChannelBeanFactory.getFactory();

        ChannelBeanContainerComplete container = new ChannelBeanContainerComplete();
        
        // Connect to channel(s) in the container
        factory.createChannelBeans(container);
        
        Double value = container.getCurrent().getValue();
        String unit = container.getUnit().getValue();
        Logger.getLogger(CompleteAnnotationExample.class.getName()).log(Level.INFO, "Current: {0} [{1}]", new Object[]{value, unit});
        
        // Disconnect channel(s) in the container
        factory.destroyChannelBeans(container);
        
        // Destroy context of the factory
        ChannelBeanFactory.getFactory().getChannelFactory().destroyContext();
	}
}

/**
 * Container class
 */
class ChannelBeanContainerComplete {

	@CaChannel(type=Double.class, name="ARIDI-PCT:CURRENT", monitor=true)
	private ChannelBean<Double> current;
	
	@CaChannel(type=String.class, name="ARIDI-PCT:CURRENT.EGU", monitor=true)
	private ChannelBean<String> unit;

	@CaPreInit
	public void preInit(){
		// Code executed before connecting the channels
	}
	
	@CaPostInit
	public void postInit(){
		// Code executed after connecting channels
	}
	
	@CaPreDestroy
	public void preDestroy(){
		// Code executed before destroying channels
	}
	
	@CaPostDestroy
	public void postDestroy(){
		// Code executed after destroying channels
	}
	
	/**
	 * @return the current
	 */
	public ChannelBean<Double> getCurrent() {
		return current;
	}
	
	/**
	 * @return unit of the current
	 */
	public ChannelBean<String> getUnit() {
		return unit;
	}
}
