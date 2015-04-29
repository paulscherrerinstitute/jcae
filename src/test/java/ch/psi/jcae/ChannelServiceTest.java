package ch.psi.jcae;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import gov.aps.jca.CAException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.psi.jcae.annotation.CaChannel;
import ch.psi.jcae.annotation.CaCompositeChannel;
import ch.psi.jcae.impl.DefaultChannelService;

public class ChannelServiceTest {
	
	private static Logger logger = Logger.getLogger(ChannelServiceTest.class.getName());
	
	private ChannelService factory;
	
	private static TestChannels testChannels;
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		testChannels = new TestChannels();
		testChannels.start();
	}
	
	@AfterClass
	public static void tearDownClass() throws Exception {
		testChannels.stop();
	}
	
	@Before
	public void setUp() throws Exception {
		DefaultChannelService s = new DefaultChannelService();
		s.getMacros().put("PREFIX", TestChannels.PREFIX);
		factory = s;
	}

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
			Channel<Double> bean = factory.createChannel(new ChannelDescriptor<Double>(Double.class, TestChannels.ANALOG_OUT));
			e = System.currentTimeMillis();
			
			// Check if channel is connected
			if(! bean.isConnected()){
				fail("Channel ["+bean.getName()+"] is not CONNECTED");
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
		TestObjectTwo one = new TestObjectTwo();

		// Manage Bean
		Map<String,String> m = new HashMap<String,String>();
//		m.put("PREFIX", TestChannels.PREFIX);
		factory.createAnnotatedChannels(one, m);

		// Check to get values
		Channel<String> tc = one.getType();
		if(! tc.isConnected()){
			fail("Channel ["+tc.getName()+"] is not CONNECTED");
		}
		logger.info(String.format("%s - %s", tc.getName(), tc.getValue()));
		
		// Check to set values
		for(int i=0;i<10;i++){
			String val = "value"+i;
			tc.setValue(val);
			Thread.sleep(10);
			String v = tc.getValue();
			if(!v.equals(val)){
				fail(String.format("Value set [%s] does not correspond to the value that was retrieved [%s]", val, v));
			}
		}
		// Write something else into the test channel to prepare the environment for the next test
		tc.setValue(""+System.currentTimeMillis());
		
		
		
		logger.info("[Start] Channel list check");
		
		// Check whether all list channels are connected
		assertTrue(one.getMylist().size()==4); // Check whether there are 4 channels in the list
		for (Channel<String> l : one.getMylist()) {
			if(! l.isConnected()){
				fail("Channel ["+l.getName()+"] is not CONNECTED");
			}
			logger.info(String.format("%s - %s", l.getName(), l.getValue()));
		}

		
	}
	
	
	@Test
	public void testDestructionRecreate() throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {

		for(int i=0;i<10;i++){
			DefaultChannelService factory1 = new DefaultChannelService();
			
			long s = System.currentTimeMillis();
			Channel<String> bean = factory1.createChannel(new ChannelDescriptor<String>(String.class, TestChannels.BINARY_OUT));
			long e = System.currentTimeMillis();
			
			// Print the elapsed time for creating the channel
			logger.info("Elapsed time: "+(e-s));
			
			// Check if channel is connected
			if(! bean.isConnected()){
				fail("Channel ["+bean.getName()+"] is not CONNECTED");
			}
			
			bean.destroy();
			Thread.sleep(100);
			
			// Destroy the context every now and then
			if(i%3==0){
				factory1.destroy();
			}
		}
	}
	
	@Test
	public void annotationTestDryrun() throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {
		
		DefaultChannelService factory1 = new DefaultChannelService(true);
		
		// Create test bean to manage
		TestObject one = new TestObject();

		// Manage Bean
		Map<String,String> m = new HashMap<String,String>();
		m.put("PREFIX", "DOES-NOT-EXIST:");
		factory1.createAnnotatedChannels(one, m);

		// Check to get values
		Channel<String> tc = one.getType();
		if(! tc.isConnected()){
			fail("Channel ["+tc.getName()+"] is not CONNECTED");
		}
		logger.info(String.format("%s - %s", tc.getName(), tc.getValue()));
		
		// Check to set values
		for(int i=0;i<10;i++){
			String val = "value"+i;
			tc.setValue(val);
			Thread.sleep(10);
			String v = tc.getValue();
			if(!v.equals(val)){
				fail(String.format("Value set [%s] does not correspond to the value that was retrieved [%s]", val, v));
			}
		}
		// Write something else into the test channel to prepare the environment for the next test
		tc.setValue(""+System.currentTimeMillis());
		
		
		// Check to get values
		tc = one.getTypeTwo();
		if(! tc.isConnected()){
			fail("Channel ["+tc.getName()+"] is not CONNECTED");
		}
		logger.info(String.format("%s - %s", tc.getName(), tc.getValue()));
		
		// Check to set values
		for(int i=0;i<10;i++){
			String val = "value"+i;
			tc.setValue(val);
			Thread.sleep(10);
			String v = tc.getValue();
			if(!v.equals(val)){
				fail(String.format("Value set [%s] does not correspond to the value that was retrieved [%s]", val, v));
			}
		}
		// Write something else into the test channel to prepare the environment for the next test
		tc.setValue(""+System.currentTimeMillis());
		
		
		// Check to get values
		tc = one.getTypeThree();
		if(! tc.isConnected()){
			fail("Channel ["+tc.getName()+"] is not CONNECTED");
		}
		logger.info(String.format("%s - %s", tc.getName(), tc.getValue()));
		
		
		
		
		logger.info("[Start] Channel list check");
		
		// Check whether all list channels are connected
		assertTrue(one.getMylist().size()==4); // Check whether there are 4 channels in the list
		for (Channel<String> l : one.getMylist()) {
			if(! l.isConnected()){
				fail("Channel ["+l.getName()+"] is not CONNECTED");
			}
			logger.info(String.format("%s - %s", l.getName(), l.getValue()));
		}

		
	}
	
	
	/**
	 * Test class containing ChannelBean attributes with annotations. 
	 */
	private class TestObject {
		
		@CaChannel( name="${PREFIX}SOUT1", type=String.class, monitor=false)
		private Channel<String> type;

		@CaChannel( name={"${PREFIX}SOUT2", "${PREFIX}SOUT3", "${PREFIX}SOUT4", "${PREFIX}SOUT5"}, type=String.class, monitor=true)
		private List<Channel<String>> mylist;
		
		@CaCompositeChannel(type=String.class, name="${PREFIX}SOUT1", readback="${PREFIX}SOUT2")
		private Channel<String> typeTwo;
		
		@CaChannel( name="${NON-EXISTING-MACRO}SOUT1", type=String.class, monitor=false) // Use of non specified macro
		private Channel<String> typeThree;
		
		public Channel<String> getType() {
			return type;
		}

		public List<Channel<String>> getMylist() {
			return(mylist);
		}

		public Channel<String> getTypeTwo() {
			return typeTwo;
		}

		public Channel<String> getTypeThree() {
			return typeThree;
		}
	}
	
	private class TestObjectTwo {
		
		@CaChannel( name="${PREFIX}SOUT1", type=String.class, monitor=false)
		private Channel<String> type;

		@CaChannel( name={"${PREFIX}SOUT2", "${PREFIX}SOUT3", "${PREFIX}SOUT4", "${PREFIX}SOUT5"}, type=String.class, monitor=true)
		private List<Channel<String>> mylist;
		
		@CaCompositeChannel(type=String.class, name="${PREFIX}SOUT1", readback="${PREFIX}SOUT2")
		private Channel<String> typeTwo;
		
		public Channel<String> getType() {
			return type;
		}

		public List<Channel<String>> getMylist() {
			return(mylist);
		}
	}

}
