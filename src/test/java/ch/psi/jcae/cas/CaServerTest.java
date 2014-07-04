package ch.psi.jcae.cas;

import static org.junit.Assert.*;
import gov.aps.jca.CAException;
import gov.aps.jca.cas.ProcessVariable;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.psi.jcae.Channel;
import ch.psi.jcae.ChannelDescriptor;
import ch.psi.jcae.ChannelException;
import ch.psi.jcae.ChannelService;
import ch.psi.jcae.cas.CaServer;
import ch.psi.jcae.cas.ProcessVariableInt;
import ch.psi.jcae.impl.DefaultChannelService;

public class CaServerTest {
	
	private static final Logger logger = Logger.getLogger(CaServerTest.class.getName());

	@Test
	public void testCaServer() {
		try{
			// Start Channel Access Server
			List<ProcessVariable> processVariables = new ArrayList<ProcessVariable>();
			ProcessVariableInt statusPV = new ProcessVariableInt("CH-PSI-CAS:TEST", null);
			
			processVariables.add(statusPV);
			// Create server
			
			
			CaServer s = new CaServer(processVariables);
			s.startAsDaemon();
//			s.start();
			
			// Change value of the channel to something ...
			Thread.sleep(5000);
			logger.info("Set 10");
			statusPV.setValue(10);
//			
			Thread.sleep(5000);
			logger.info("Set 20");
			statusPV.setValue(20);
			
			s.stop();
//			// Wait moreless for ever ...
//			Thread.sleep(100000000000l);
		}
		catch(Exception e){
			e.printStackTrace();
			fail("Exception occured: "+e.getMessage());
		}
	}
	
	@Test
	public void testCaClient() throws InterruptedException, ChannelException, TimeoutException, ExecutionException, IllegalStateException, CAException{
		
		List<ProcessVariable> processVariables = new ArrayList<ProcessVariable>();
		ProcessVariableInt statusPV = new ProcessVariableInt("CH-PSI-CAS:TEST", null);
		
		processVariables.add(statusPV);
		// Create server
		
		
		CaServer s = new CaServer(processVariables);
		s.startAsDaemon();
		
		ChannelService factory = new DefaultChannelService();
		Channel<Integer> b = factory.createChannel(new ChannelDescriptor<Integer>(Integer.class, "CH-PSI-CAS:TEST", true));
		b.addPropertyChangeListener(new PropertyChangeListener() {
			
			public void propertyChange(PropertyChangeEvent evt) {
				System.out.println("Changed - "+evt.getNewValue());
			}
		});
		
		for(int i=0;i<10;i++){
			int v = b.getValue();
			System.out.println("Value: "+v);
			b.setValue((v+1));
		}
		
		s.stop();
		
		factory.destroy();
		
	}
	
	@Test
	public void testDoubleVariable(){
		
		List<ProcessVariable> processVariables = new ArrayList<ProcessVariable>();
		ProcessVariableDouble pv = new ProcessVariableDouble("CH-PSI-CAS:TESTDOUBLE", null);
		processVariables.add(pv);
		
		CaServer s = new CaServer(processVariables);
		s.startAsDaemon();
		
		double value = 1.0;
		for(int i = 0;i<100;i++){
			pv.setValue(value);
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
			
			if(i%10==0){
				value++;
			}
		}
		
	}

}
