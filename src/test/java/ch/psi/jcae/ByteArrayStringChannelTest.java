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

public class ByteArrayStringChannelTest {
	
	private static Logger logger = Logger.getLogger(ByteArrayStringChannelTest.class.getName());
	
	private DefaultChannelService cservice;
	private Channel<ByteArrayString> b;
	
	@Before
	public void setUp() throws Exception {
		cservice = new DefaultChannelService();
		b = cservice.createChannel(new ChannelDescriptor<ByteArrayString>(ByteArrayString.class, TestChannels.CHARACTER_WAVEFORM));
	}

	@After
	public void tearDown() throws Exception {
		cservice.destroy();
	}

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
