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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import gov.aps.jca.CAException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.psi.jcae.Channel;
import ch.psi.jcae.ChannelDescriptor;
import ch.psi.jcae.ChannelException;
import ch.psi.jcae.impl.DefaultChannelService;
import ch.psi.jcae.impl.type.ByteArrayString;

/**
 * @author ebner
 *
 */
public class ByteArrayStringChannelTest {
	
	// Get Logger
	private static Logger logger = Logger.getLogger(ByteArrayStringChannelTest.class.getName());
	
	private DefaultChannelService cservice;
	private Channel<ByteArrayString> b;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		cservice = new DefaultChannelService();
		b = cservice.createChannel(new ChannelDescriptor<>(ByteArrayString.class, TestChannels.CHARACTER_WAVEFORM));
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		cservice.destroy();
	}

	/**
	 * Test method for {@link ch.psi.jcae.converter.AbstractConverterBean#getValue()}.
	 * @throws CAException 
	 * @throws InterruptedException 
	 * @throws ChannelException 
	 * @throws TimeoutException 
	 * @throws ExecutionException 
	 */
	@Test
	public void testGetSetValue() throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {
		
		ByteArrayString ovalue = b.getValue();
		logger.info("Channel value as String: "+ ovalue.getValue());
		
		// Set new String
		ByteArrayString value = new ByteArrayString("Some more string");
		b.setValue(value);
		
		ByteArrayString value2 = b.getValue();
		logger.info("Channel value as String: "+value2.getValue());

		assertEquals(value.getValue(), value2.getValue());
		
		// Reset previous channel value
		b.setValue(ovalue);
		
	}

}
