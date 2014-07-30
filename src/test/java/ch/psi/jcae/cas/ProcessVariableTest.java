package ch.psi.jcae.cas;

import static org.junit.Assert.*;
import gov.aps.jca.CAException;
import gov.aps.jca.cas.ProcessVariable;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Array;
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
import ch.psi.jcae.impl.DefaultChannelService;

public class ProcessVariableTest {

	private static final Logger logger = Logger.getLogger(ProcessVariableTest.class.getName());

	@Test
	public void testProcessVariableDouble() throws ChannelException, InterruptedException, TimeoutException, ExecutionException, IllegalStateException, CAException {
		String channelName = "CH-PSI-CAS:DOUBLE_TEST";
		List<ProcessVariable> processVariables = new ArrayList<ProcessVariable>();
		ProcessVariableDouble statusPV = new ProcessVariableDouble(channelName, null);

		processVariables.add(statusPV);
		CaServer s = new CaServer(processVariables);
		s.startAsDaemon();

		ChannelService factory = new DefaultChannelService();
		Channel<Double> b = factory.createChannel(new ChannelDescriptor<Double>(Double.class, channelName, true));
		AssertPropertyChangeListener listener = new AssertPropertyChangeListener();
		b.addPropertyChangeListener(listener);

		for (int i = 0; i < 10; i++) {
			Double v = b.getValue() + 1;
			listener.setCurrentValue(v);

			b.setValue(v);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}

		s.stop();

		factory.destroy();
	}

	@Test
	public void testProcessVariableDoubleWaveform() throws ChannelException, InterruptedException, TimeoutException, ExecutionException, IllegalStateException, CAException {
		String channelName = "CH-PSI-CAS:DOUBLEWAVEFORM_TEST";
		List<ProcessVariable> processVariables = new ArrayList<ProcessVariable>();
		ProcessVariableDoubleWaveform statusPV = new ProcessVariableDoubleWaveform(channelName, null, 10);

		processVariables.add(statusPV);
		CaServer s = new CaServer(processVariables);
		s.startAsDaemon();

		ChannelService factory = new DefaultChannelService();
		Channel<double[]> b = factory.createChannel(new ChannelDescriptor<double[]>(double[].class, channelName, true));
		AssertPropertyChangeListener listener = new AssertPropertyChangeListener();
		b.addPropertyChangeListener(listener);

		for (int i = 0; i < 10; i++) {
			double[] v = b.getValue();
			for (int j = 0; j < v.length; ++j) {
				++v[j];
			}
			listener.setCurrentValue(v);

			b.setValue(v);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}

		s.stop();

		factory.destroy();
	}

	@Test
	public void testProcessVariableInteger() throws ChannelException, InterruptedException, TimeoutException, ExecutionException, IllegalStateException, CAException {
		String channelName = "CH-PSI-CAS:INTEGER_TEST";
		List<ProcessVariable> processVariables = new ArrayList<ProcessVariable>();
		ProcessVariableInteger statusPV = new ProcessVariableInteger(channelName, null);

		processVariables.add(statusPV);
		CaServer s = new CaServer(processVariables);
		s.startAsDaemon();

		ChannelService factory = new DefaultChannelService();
		Channel<Integer> b = factory.createChannel(new ChannelDescriptor<Integer>(Integer.class, channelName, true));
		AssertPropertyChangeListener listener = new AssertPropertyChangeListener();
		b.addPropertyChangeListener(listener);

		for (int i = 0; i < 10; i++) {
			Integer v = b.getValue() + 1;
			listener.setCurrentValue(v);

			b.setValue(v);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}

		s.stop();

		factory.destroy();
	}

	@Test
	public void testProcessVariableIntegerWaveform() throws ChannelException, InterruptedException, TimeoutException, ExecutionException, IllegalStateException, CAException {
		String channelName = "CH-PSI-CAS:INTEGERWAVEFORM_TEST";
		List<ProcessVariable> processVariables = new ArrayList<ProcessVariable>();
		ProcessVariableDoubleWaveform statusPV = new ProcessVariableDoubleWaveform(channelName, null, 10);

		processVariables.add(statusPV);
		CaServer s = new CaServer(processVariables);
		s.startAsDaemon();

		ChannelService factory = new DefaultChannelService();
		Channel<int[]> b = factory.createChannel(new ChannelDescriptor<int[]>(int[].class, channelName, true));
		AssertPropertyChangeListener listener = new AssertPropertyChangeListener();
		b.addPropertyChangeListener(listener);

		for (int i = 0; i < 10; i++) {
			int[] v = b.getValue();
			for (int j = 0; j < v.length; ++j) {
				++v[j];
			}
			listener.setCurrentValue(v);

			b.setValue(v);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}

		s.stop();

		factory.destroy();
	}

	private class AssertPropertyChangeListener implements PropertyChangeListener {
		private Object currentValue;

		public void setCurrentValue(Object currentValue) {
			this.currentValue = currentValue;
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (Channel.PROPERTY_VALUE.equals(evt.getPropertyName())) {
				Object evtValue = evt.getNewValue();
				if (evtValue.getClass().isArray() && currentValue.getClass().isArray()) {
					int evtLength = Array.getLength(evtValue);
					int currentLength = Array.getLength(this.currentValue);

					assertEquals(currentLength, evtLength);
					for (int i = 0; i < currentLength; ++i) {
						assertEquals(Array.get(this.currentValue, i), Array.get(evtValue, i));
					}

				} else if (!evtValue.getClass().isArray() && !currentValue.getClass().isArray()) {
					assertEquals(this.currentValue, evtValue);
				} else {
					throw new RuntimeException("Both elements should have the same type");
				}
			}
		}
	}
}
