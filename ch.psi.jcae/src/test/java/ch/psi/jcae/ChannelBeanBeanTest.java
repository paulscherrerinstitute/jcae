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

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import gov.aps.jca.CAException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.psi.jcae.annotation.CaChannel;
import ch.psi.jcae.annotation.CaPostDestroy;
import ch.psi.jcae.annotation.CaPostInit;
import ch.psi.jcae.annotation.CaPreDestroy;
import ch.psi.jcae.annotation.CaPreInit;
import ch.psi.jcae.impl.ChannelBean;
import ch.psi.jcae.impl.ChannelBeanFactory;

/**
 * @author ebner
 *
 */
public class ChannelBeanBeanTest {
	
	// Get Logger
	private static final Logger logger = Logger.getLogger(ChannelBeanBeanTest.class.getName());
	
	private ChannelBeanFactory factory;
	
	private HashMap<String, Long> timestamps = new HashMap<String,Long>();
	private boolean errorInSequence = false;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		// Get default factory
		factory = ChannelBeanFactory.getFactory();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method
	 * @throws CAException 
	 * @throws InterruptedException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testConnectChannelBeans() throws CAException, ChannelException, InterruptedException, TimeoutException {
		TestObject object = new TestObject();
		factory.createChannelBeans(object, TestChannels.PREFIX);
		
		// Check whether pre and post methods are executed
		if(timestamps.get("pre")!=null && timestamps.get("post")!=null){
			if(timestamps.get("pre")>timestamps.get("post")){
				fail("Pre is not executed before post");
			}
		}
		else{
			fail("Pre or post did not get executed");
		}
		
		factory.destroyChannelBeans(object);
		
		// Check whether pre and post destroy got executed
		if(timestamps.get("preDestroy")!=null && timestamps.get("postDestroy")!=null){
			if(timestamps.get("preDestroy")>timestamps.get("postDestroy")){
				fail("Pre destroy is not executed before post destroy");
			}
		}
		else{
			fail("Pre destroy or post destroy did not get executed");
		}
		
		if(errorInSequence){
			fail("Execution sequence is not correct");
		}
		
	}
	
	public class TestObject {
		
		@CaChannel( name="BI", type=Integer.class, monitor=true)
		private ChannelBean<Integer> type;
		
		public ChannelBean<Integer> getType() {
			return type;
		}

		@CaPreInit
		public void preInit(){
			logger.info("Execute PRE - Timestamp: "+System.currentTimeMillis());
			timestamps.put("pre", System.currentTimeMillis());
			
			if(type != null){
				logger.warning("ChannelBean already created");
				errorInSequence = true;
			}
		}
		
		@CaPostInit
		public void postInit(){
			logger.info("Execute POST - Timestamp: "+System.currentTimeMillis());
			timestamps.put("post", System.currentTimeMillis());
			
			if(type == null){
				logger.warning("ChannelBean was not created before executing post");
				errorInSequence = true;
			}
		}
		
		@CaPreDestroy
		public void preDestroy(){
			logger.info("Execute PRE destroy - Timestamp: "+System.currentTimeMillis());
			timestamps.put("preDestroy", System.currentTimeMillis());
			
			if(type == null){
				logger.warning("ChannelBean already destroyed before executing pre destroy");
				errorInSequence = true;
			}
		}
		
		@CaPostDestroy
		public void postDestroy(){
			logger.info("Execute POST destroy - Timestamp: "+System.currentTimeMillis());
			timestamps.put("postDestroy", System.currentTimeMillis());
			
			if(type != null){
				logger.warning("ChannelBean was not destroyed before executing post destroy");
				errorInSequence = true;
			}
		}
		
	}
}