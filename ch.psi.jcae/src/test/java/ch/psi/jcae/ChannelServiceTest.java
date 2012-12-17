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

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import gov.aps.jca.CAException;
import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.psi.jcae.annotation.CaChannel;
import ch.psi.jcae.impl.DefaultChannel;
import ch.psi.jcae.impl.DefaultChannelService;

/**
 * @author ebner
 *
 */
public class ChannelServiceTest {
	
	// Get Logger
	private static Logger logger = Logger.getLogger(ChannelServiceTest.class.getName());
	
	private ChannelService factory;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		factory = new DefaultChannelService();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		factory.destroy();
	}

	/**
	 * Test method for {@link ch.psi.jcae.impl.DefaultChannelService#createChannelBean(java.lang.Class, java.lang.String, boolean)}.
	 * @throws CAException 
	 * @throws InterruptedException 
	 * @throws ChannelException 
	 * @throws TimeoutException 
	 * @throws ExecutionException 
	 */
	@Test
	public void testCreateChannelBean() throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {
		for(int i=0;i<100;i++){
			
			long s = System.currentTimeMillis();
			Channel<String> bean = factory.createChannel(new ChannelDescriptor<>(String.class, TestChannels.BINARY_OUT));
			long e = System.currentTimeMillis();
			
			// Print the elapsed time for creating the channel
			logger.info("Elapsed time: "+(e-s));
			
			// Check if channel is connected
			if(! bean.isConnected()){
				Assert.fail("Channel ["+bean.getName()+"] is not CONNECTED");
			}
			
			bean.destroy();
			Thread.sleep(100);
		}
		
	}
	
	
	/**
	 * Test whether manager is creating/connecting all the annotated ChannelBean attributes;
	 * 
	 * @throws CAException
	 * @throws InterruptedException
	 * @throws ChannelException 
	 * @throws TimeoutException 
	 * @throws ExecutionException 
	 */
	@Test
	public void manageTest() throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {
		
		// Create test bean to manage
		TestObject one = new TestObject();

		// Manage Bean
		factory.createAnnotatedChannels(one, TestChannels.PREFIX);

		// Check to get values
		one.getType().getValue();
		logger.fine(one.getType().getName()+ " - " + one.getType().getValue());
		List<DefaultChannel<String>> x = one.getMylist();
		for (DefaultChannel<String> l : x) {
			l.getValue();
			logger.info( l.getName() + " - " + l.getValue());
		}

		// Check to set values
		one.getType().setValue("value");
		String v = one.getType().getValue(true);
		if(!v.equals("value")){
			Assert.fail("Value set does not correspond to the value that was retrieved");
		}
		
		// Write something else into the test channel to prepare the environment for the next test
		one.getType().setValue("old value");
	}
	
	
	@Test
	public void testDestructionRecreate() throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {

		for(int i=0;i<10;i++){
			DefaultChannelService factory1 = new DefaultChannelService();
			
			long s = System.currentTimeMillis();
			Channel<String> bean = factory1.createChannel(new ChannelDescriptor<>(String.class, TestChannels.BINARY_OUT));
			long e = System.currentTimeMillis();
			
			// Print the elapsed time for creating the channel
			logger.info("Elapsed time: "+(e-s));
			
			// Check if channel is connected
			if(! bean.isConnected()){
				Assert.fail("Channel ["+bean.getName()+"] is not CONNECTED");
			}
			
			bean.destroy();
			Thread.sleep(100);
			
			// Destroy the context every now and then
			if(i%3==0){
				factory1.destroy();
			}
		}
	}
	
	
	/**
	 * Test class containing ChannelBean attributes with annotations. 
	 * @author ebner
	 *
	 */
	private class TestObject {
		
		@CaChannel( name="SOUT1", type=String.class, monitor=true)
		private DefaultChannel<String> type;

		@CaChannel( name={"SOUT2", "SOUT3", "SOUT4", "SOUT5"}, type=String.class, monitor=true)
		private List<DefaultChannel<String>> mylist;
		
		public DefaultChannel<String> getType() {
			return type;
		}

		public List<DefaultChannel<String>> getMylist() {
			return(mylist);
		}
		
	}

}
