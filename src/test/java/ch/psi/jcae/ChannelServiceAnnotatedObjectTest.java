package ch.psi.jcae;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
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
import ch.psi.jcae.impl.DefaultChannelService;

public class ChannelServiceAnnotatedObjectTest {
	
	private static final Logger logger = Logger.getLogger(ChannelServiceAnnotatedObjectTest.class.getName());
	
	private ChannelService cservice;
	
	private HashMap<String, Long> timestamps = new HashMap<String,Long>();
	private boolean errorInSequence = false;
	
	@Before
	public void setUp() throws Exception {
		// Get default factory
		DefaultChannelService s = new DefaultChannelService(true);
		s.getMacros().put("PREFIX", TestChannels.PREFIX);
		cservice = s;
	}

	@After
	public void tearDown() throws Exception {
		cservice.destroy();
	}

	@Test
	public void testConnectChannelBeans() throws CAException, ChannelException, InterruptedException, TimeoutException, ExecutionException {
		TestObject object = new TestObject();
		Map<String,String> m = new HashMap<String,String>();
//		m.put("PREFIX", TestChannels.PREFIX);
		try{
			cservice.createAnnotatedChannels(object, m);
		}
		catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		// Check whether pre and post methods are executed
		if(timestamps.get("pre")!=null && timestamps.get("post")!=null){
			if(timestamps.get("pre")>timestamps.get("post")){
				fail("Pre is not executed before post");
			}
		}
		else{
			fail("Pre or post did not get executed");
		}
		
		cservice.destroyAnnotatedChannels(object);
		
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
		
		@CaChannel( name="${PREFIX}BI", type=Integer.class, monitor=true)
		private Channel<Integer> field1 = null;

		@CaChannel( name="${PREFIX}BI", type=Integer.class, monitor=true)
		private List<Channel<Integer>> field2 = null;
		
		@CaChannel( name="${PREFIX}BI", type=Integer.class, monitor=true)
		private Collection<Channel<Integer>> field3 = null; // Parent interface of List
		
		
		@CaPreInit
		public void preInit(){
			logger.info("Execute PRE - Timestamp: "+System.currentTimeMillis());
			timestamps.put("pre", System.currentTimeMillis());
			
			if(field1 != null || field2 != null || field3 != null){
				logger.warning("Channels already created");
				errorInSequence = true;
			}
		}
		
		@CaPostInit
		public void postInit() throws InterruptedException, TimeoutException, ChannelException, ExecutionException{
			System.out.println("VALUE: "+field1.getValue());
			int i = field1.getValue();
			System.out.println(i);
			logger.info("Execute POST - Timestamp: "+System.currentTimeMillis());
			timestamps.put("post", System.currentTimeMillis());
			
			if(field1 == null || field2 == null || field3 == null){
				logger.warning("Channels were not created before executing post");
				errorInSequence = true;
			}
		}
		
		@CaPreDestroy
		public void preDestroy(){
			logger.info("Execute PRE destroy - Timestamp: "+System.currentTimeMillis());
			timestamps.put("preDestroy", System.currentTimeMillis());
			
			if(field1 == null || field2 == null || field3 == null){
				logger.warning("Channels are already destroyed before executing pre destroy");
				errorInSequence = true;
			}
		}
		
		@CaPostDestroy
		public void postDestroy(){
			logger.info("Execute POST destroy - Timestamp: "+System.currentTimeMillis());
			timestamps.put("postDestroy", System.currentTimeMillis());
			
			if(field1 != null || field2 != null || field3 != null){
				logger.warning("ChannelBean was not destroyed before executing post destroy");
				errorInSequence = true;
			}
		}
		
	}
}