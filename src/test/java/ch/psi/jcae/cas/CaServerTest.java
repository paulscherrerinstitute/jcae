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

import org.junit.Test;

import ch.psi.jcae.Channel;
import ch.psi.jcae.ChannelDescriptor;
import ch.psi.jcae.ChannelException;
import ch.psi.jcae.ChannelService;
import ch.psi.jcae.cas.CaServer;
import ch.psi.jcae.cas.ProcessVariableGeneric;
import ch.psi.jcae.impl.DefaultChannelService;

public class CaServerTest {

	private static final Logger logger = Logger.getLogger(CaServerTest.class.getName());

	@Test
	public void testCaServer() {
		try {
			// Start Channel Access Server
			List<ProcessVariable> processVariables = new ArrayList<ProcessVariable>();
			ProcessVariableGeneric<Integer> statusPV = new ProcessVariableGeneric<Integer>("CH-PSI-CAS:TEST", null, Integer.class);

			processVariables.add(statusPV);
			// Create server

			CaServer s = new CaServer(processVariables);
			s.startAsDaemon();
			// s.start();

			// Change value of the channel to something ...
			Thread.sleep(5000);
			logger.info("Set 10");
			statusPV.setValue(10);
			//
			Thread.sleep(5000);
			logger.info("Set 20");
			statusPV.setValue(20);

			s.stop();
			// // Wait moreless for ever ...
			// Thread.sleep(100000000000l);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occured: " + e.getMessage());
		}
	}

	@Test
	public void testCaClient() throws InterruptedException, ChannelException, TimeoutException, ExecutionException, IllegalStateException, CAException {

		List<ProcessVariable> processVariables = new ArrayList<ProcessVariable>();
		ProcessVariableGeneric<Integer> statusPV = new ProcessVariableGeneric<Integer>("CH-PSI-CAS:TEST", null, Integer.class);

		processVariables.add(statusPV);
		// Create server

		CaServer s = new CaServer(processVariables);
		s.startAsDaemon();

		ChannelService factory = new DefaultChannelService();
		Channel<Integer> b = factory.createChannel(new ChannelDescriptor<Integer>(Integer.class, "CH-PSI-CAS:TEST", true));
		b.addPropertyChangeListener(new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent evt) {
				System.out.println("Changed - " + evt.getNewValue());
			}
		});

		for (int i = 0; i < 10; i++) {
			int v = b.getValue();
			System.out.println("Value: " + v);
			b.setValue((v + 1));
		}

		s.stop();

		factory.destroy();

	}

	@Test
	public void testDoubleVariable() {
		List<ProcessVariable> processVariables = new ArrayList<ProcessVariable>();
		ProcessVariableGeneric<Double> pv = new ProcessVariableGeneric<Double>("CH-PSI-CAS:TESTDOUBLE", null, Double.class);
		processVariables.add(pv);

		CaServer s = new CaServer(processVariables);
		s.startAsDaemon();

		double value = 1.0;
		for (int i = 0; i < 100; i++) {
			pv.setValue(value);

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}

			assertEquals(value, pv.getValue().doubleValue(), 0.00000001);

			if (i % 10 == 0) {
				value++;
			}
		}
	}

	@Test
	public void testDoubleArray() {
		int arraySize = 5;
		List<ProcessVariable> processVariables = new ArrayList<ProcessVariable>();
		ProcessVariableGeneric<double[]> pv = new ProcessVariableGeneric<double[]>("CH-PSI-CAS:TESTDOUBLE", null, double[].class, arraySize);
		processVariables.add(pv);

		CaServer s = new CaServer(processVariables);
		s.startAsDaemon();

		double value = 1.0;
		double[] valueArray = new double[arraySize];
		for (int j = 0; j < arraySize; ++j) {
			valueArray[j] = value;
		}

		for (int i = 0; i < 100; i++) {
			pv.setValue(valueArray);

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}

			assertArrayEquals(valueArray, pv.getValue(), 0.00000001);

			if (i % 10 == 0) {
				value++;
				for (int j = 0; j < arraySize; ++j) {
					valueArray[j] = value;
				}
			}
		}
	}

}
