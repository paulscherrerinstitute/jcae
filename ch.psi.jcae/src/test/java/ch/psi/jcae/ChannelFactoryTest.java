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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import junit.framework.Assert;

import gov.aps.jca.CAException;
import gov.aps.jca.Channel;
import gov.aps.jca.Channel.ConnectionState;

import org.junit.Before;
import org.junit.Test;

import ch.psi.jcae.impl.JCAChannelFactory;

/**
 * JUnit test class for testing the <code>ChannelFactory</code> class
 * @author ebner
 *
 */
public class ChannelFactoryTest {
	
	private static Logger logger = Logger.getLogger(ChannelFactoryTest.class.getName());

	private JCAChannelFactory factory;
	
	@Before
	public void setUp() throws Exception {
		// Use jca.properties to override default settings
		factory = new JCAChannelFactory();
	}

	/**
	 * Test creation of a single channel
	 * @throws CAException 
	 * @throws InterruptedException 
	 */
	@Test
	public void testCreateChannel() throws CAException, InterruptedException {
		// Create channel
		long s = System.currentTimeMillis();
		Channel channel = factory.createChannel(TestChannels.BINARY_OUT);
		long e = System.currentTimeMillis();
		
		// Print the elapsed time for creating the channel
		logger.info("Elapsed time: "+(e-s));
		
		// Check if channel is connected
		if(! channel.getConnectionState().equals(ConnectionState.CONNECTED)){
			Assert.fail("Channel ["+channel.getName()+"] is not CONNECTED but: "+channel.getConnectionState().getName());
		}
	}
	
	/**
	 * Test creation of a single channel that does not exist. It is expected that an 
	 * CAException is thrown.
	 * @throws CAException 
	 * @throws InterruptedException 
	 */
	@Test(expected=CAException.class)
	public void testCreateChannelNonexistent() throws CAException, InterruptedException {
		Channel channel = factory.createChannel(TestChannels.BINARY_OUT_NOT_EXIST);
		logger.info("Channel ["+channel.getName()+"]: "+channel.getConnectionState().getName());
	}
	
	/**
	 * Test creation of multiple channels at once
	 * @throws CAException 
	 * @throws InterruptedException 
	 */
	@Test
	public void testCreateChannels() throws CAException, InterruptedException {
		List<String> channelNames = new ArrayList<String>();
		channelNames.add(TestChannels.BINARY_OUT);
		channelNames.add(TestChannels.BINARY_IN);
		channelNames.add(TestChannels.CHARACTER_WAVEFORM);
		
		long s = System.currentTimeMillis();
		List<Channel> channels = factory.createChannels(channelNames);
		long e = System.currentTimeMillis();
		
		// Print time elapsed for creating channels
		logger.info("Elapsed time: "+(e-s));
		
		// Check if all channels are connected
		for(Channel c: channels){
			if(! c.getConnectionState().equals(ConnectionState.CONNECTED)){
				Assert.fail("Channel ["+c.getName()+"] is not CONNECTED but: "+c.getConnectionState().getName());
			}
		}
	}
	
	/**
	 * Test creation of multiple channels at once. One of the specified channels does not exist.
	 * It is expected that an CAException is thrown.
	 * @throws CAException 
	 * @throws InterruptedException 
	 */
	@Test(expected=CAException.class)
	public void testCreateChannelsNonexistent() throws CAException, InterruptedException {
		List<String> channelNames = new ArrayList<String>();
		channelNames.add(TestChannels.BINARY_OUT);
		channelNames.add(TestChannels.BINARY_OUT_NOT_EXIST);
		channelNames.add(TestChannels.CHARACTER_WAVEFORM);
		
		// Create channels
		factory.createChannels(channelNames);
	}
	
	/**
	 * Test createChannels function to behave correctly if null or an empty 
	 * name list is passed.
	 * @throws CAException 
	 * @throws InterruptedException 
	 */
	@Test
	public void testCreateChannelsNullEmpty() throws CAException, InterruptedException {
		List<String> channelNames = new ArrayList<String>();
		
		// Check factory behavior if an empty name list is passed
		List<Channel> channels = factory.createChannels(channelNames);
		if(channels.size()!=0){
			Assert.fail("Factory does not return an empty Channel List if an empty name List is passed to the create function");
		}
		
		// Check factory behavior if null is passed
		channels = factory.createChannels(channelNames);
		if(channels.size()!=0){
			Assert.fail("Factory does not return an empty Channel List if an empty name List is passed to the create function");
		}
		
	}
}
