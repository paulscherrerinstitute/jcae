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
		long s;
		long e;
		for(int i=0;i<100;i++){
			
			s = System.currentTimeMillis();
			Channel<Double> bean = factory.createChannel(new ChannelDescriptor<>(Double.class, TestChannels.ANALOG_OUT));
			e = System.currentTimeMillis();
			
			// Check if channel is connected
			if(! bean.isConnected()){
				Assert.fail("Channel ["+bean.getName()+"] is not CONNECTED");
			}
			
			// Print the elapsed time for creating the channel
			logger.info("Attempt: "+i+" - Elapsed time: "+(e-s) +" - Value: "+ bean.getValue());
			bean.setValue((double) System.currentTimeMillis()); // Change the value so that one can see a difference in the next log output
			
			bean.destroy();
			Thread.sleep(100); // Need to have a sleep here to ensure that the channel is really destroyed (destroy() just does a flushIO() - there is no confirmation that things are already destroyed after the return of this method)
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
	public void annotationTest() throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {
		
		// Create test bean to manage
		TestObject one = new TestObject();

		// Manage Bean
		factory.createAnnotatedChannels(one, TestChannels.PREFIX);

		// Check to get values
		Channel<String> tc = one.getType();
		if(! tc.isConnected()){
			Assert.fail("Channel ["+tc.getName()+"] is not CONNECTED");
		}
		logger.info(String.format("%s - %s", tc.getName(), tc.getValue()));
		
		// Check to set values
		for(int i=0;i<10;i++){
			String val = "value"+i;
			tc.setValue(val);
			Thread.sleep(10);
			String v = tc.getValue();
			if(!v.equals(val)){
				Assert.fail(String.format("Value set [%s] does not correspond to the value that was retrieved [%s]", val, v));
			}
		}
		// Write something else into the test channel to prepare the environment for the next test
		tc.setValue(""+System.currentTimeMillis());
		
		
		
		logger.info("[Start] Channel list check");
		
		// Check whether all list channels are connected
		Assert.assertTrue(one.getMylist().size()==4); // Check whether there are 4 channels in the list
		for (Channel<String> l : one.getMylist()) {
			if(! l.isConnected()){
				Assert.fail("Channel ["+l.getName()+"] is not CONNECTED");
			}
			logger.info(String.format("%s - %s", l.getName(), l.getValue()));
		}

		
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
		
		@CaChannel( name="SOUT1", type=String.class, monitor=false)
		private Channel<String> type;

		@CaChannel( name={"SOUT2", "SOUT3", "SOUT4", "SOUT5"}, type=String.class, monitor=true)
		private List<Channel<String>> mylist;
		
		public Channel<String> getType() {
			return type;
		}

		public List<Channel<String>> getMylist() {
			return(mylist);
		}
		
	}

}
