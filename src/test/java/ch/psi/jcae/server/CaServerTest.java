package ch.psi.jcae.server;

import gov.aps.jca.cas.ProcessVariable;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CaServerTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCaServer() throws InterruptedException {
			// Start Channel Access Server
			List<ProcessVariable> processVariables = new ArrayList<ProcessVariable>();
			ProcessVariableInt statusPV = new ProcessVariableInt("JCAE-TEST.INT", null);

//           processVariables.add(new ProcessVariableInt(channelBaseName+".INT", null));
//                      processVariables.add(new ProcessVariableIntWaveform(channelBaseName+".INTWF", null));
//                      processVariables.add(new ProcessVariableExecute(channelBaseName+".EXEC",null, script));
//                      processVariables.add(new ProcessVariableDoubleWaveform(channelBaseName+".WAVE",null, 5));

                        processVariables.add(statusPV);
			// Create server
			CaServer s = new CaServer(processVariables);
			s.startAsDeamon();
			
//			Thread.sleep(5000);
			statusPV.setValue(1);
			
//			Thread.sleep(5000);
			statusPV.setValue(0);
			statusPV.setValue(1);
			statusPV.setValue(0);
	}

}
