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

package ch.psi.jcae.converter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

import gov.aps.jca.CAException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.psi.jcae.ChannelBean;
import ch.psi.jcae.ChannelBeanFactory;

/**
 * @author ebner
 *
 */
public class IntegerStringConverterBeanTest {
	
	// Get Logger
	private static Logger logger = Logger.getLogger(IntegerStringConverterBeanTest.class.getName());
	
	private IntegerStringConverterBean b;
	private ChannelBean<Integer> cbean;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		String channel = "MTEST-PC-JCAE:BI";
		cbean = ChannelBeanFactory.getFactory().createChannelBean(Integer.class, channel, true);
		b = new IntegerStringConverterBean(cbean);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link ch.psi.jcae.converter.AbstractConverterBean#getValue()}.
	 * @throws CAException 
	 * @throws InterruptedException 
	 */
	@Test
	public void testGetSetValue() throws CAException, InterruptedException {
		
		cbean.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				logger.info("Value cbean changed: "+evt.getNewValue());
			}
		});
		
		b.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				logger.info("Value changed: "+evt.getNewValue());
			}
		});
		
//		Thread.sleep(100000);
	}

}
