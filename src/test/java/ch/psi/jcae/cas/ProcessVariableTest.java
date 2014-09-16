package ch.psi.jcae.cas;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import gov.aps.jca.CAException;
import gov.aps.jca.cas.ProcessVariable;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import ch.psi.jcae.Channel;
import ch.psi.jcae.ChannelDescriptor;
import ch.psi.jcae.ChannelException;
import ch.psi.jcae.ChannelService;
import ch.psi.jcae.cas.CaServer;
import ch.psi.jcae.impl.DefaultChannelService;

public class ProcessVariableTest {

	@Test
	public void testProcessVariableDouble() throws ChannelException, InterruptedException, TimeoutException, ExecutionException, IllegalStateException, CAException {
		String channelName = "CH-PSI-CAS:DOUBLE_TEST";
		List<ProcessVariable> processVariables = new ArrayList<ProcessVariable>();
		ProcessVariableGeneric<Double> statusPV = new ProcessVariableGeneric<Double>(channelName, null, Double.class);

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
	public void testProcessVariableDouble2() throws ChannelException, InterruptedException, TimeoutException, ExecutionException, IllegalStateException, CAException {
		String channelName = "CH-PSI-CAS:DOUBLE2_TEST";
		ProcessVariableGeneric<Double> statusPV = new ProcessVariableGeneric<Double>(channelName, null, Double.class);

		long time = System.currentTimeMillis();
		for (int i = 0; i < 10; i++) {
			statusPV.setValue((double) i, time + i, i);

			assertEquals((double) i, statusPV.getValue(), 0.00000001);
			assertEquals(time + i, statusPV.getTimeMillis());
			assertEquals(i, statusPV.getTimeNanoOffset());
		}
	}

	@Test
	public void testProcessVariableDoubleWaveform() throws ChannelException, InterruptedException, TimeoutException, ExecutionException, IllegalStateException, CAException {
		String channelName = "CH-PSI-CAS:DOUBLEWAVEFORM_TEST";
		List<ProcessVariable> processVariables = new ArrayList<ProcessVariable>();
		ProcessVariableGeneric<double[]> statusPV = new ProcessVariableGeneric<double[]>(channelName, null, double[].class, 10);

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
	public void testProcessVariableDoubleWaveform2() throws ChannelException, InterruptedException, TimeoutException, ExecutionException, IllegalStateException, CAException {
		String channelName = "CH-PSI-CAS:DOUBLEWAVEFORM2_TEST";
		int size = 10;
		ProcessVariableGeneric<double[]> statusPV = new ProcessVariableGeneric<double[]>(channelName, null, double[].class, size);

		long time = System.currentTimeMillis();
		double[] v = new double[size];

		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < v.length; ++j) {
				++v[j];
			}
			statusPV.setValue(v, time + i, i);

			assertArrayEquals(v, statusPV.getValue(), 0.00000001);
			assertEquals(time + i, statusPV.getTimeMillis());
			assertEquals(i, statusPV.getTimeNanoOffset());
		}
	}

	@Test
	public void testProcessVariableInteger() throws ChannelException, InterruptedException, TimeoutException, ExecutionException, IllegalStateException, CAException {
		String channelName = "CH-PSI-CAS:INTEGER_TEST";
		List<ProcessVariable> processVariables = new ArrayList<ProcessVariable>();
		ProcessVariableGeneric<Integer> statusPV = new ProcessVariableGeneric<Integer>(channelName, null, Integer.class);

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
	public void testProcessVariableInteger2() throws ChannelException, InterruptedException, TimeoutException, ExecutionException, IllegalStateException, CAException {
		String channelName = "CH-PSI-CAS:INTEGER2_TEST";
		ProcessVariableGeneric<Integer> statusPV = new ProcessVariableGeneric<Integer>(channelName, null, Integer.class);

		long time = System.currentTimeMillis();
		for (int i = 0; i < 10; i++) {
			statusPV.setValue(i, time + i, i);

			assertEquals(i, statusPV.getValue(), 0.00000001);
			assertEquals(time + i, statusPV.getTimeMillis());
			assertEquals(i, statusPV.getTimeNanoOffset());
		}
	}

	@Test
	public void testProcessVariableIntegerWaveform() throws ChannelException, InterruptedException, TimeoutException, ExecutionException, IllegalStateException, CAException {
		String channelName = "CH-PSI-CAS:INTEGERWAVEFORM_TEST";
		List<ProcessVariable> processVariables = new ArrayList<ProcessVariable>();
		ProcessVariableGeneric<double[]> statusPV = new ProcessVariableGeneric<double[]>(channelName, null, double[].class, 10);

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

	@Test
	public void testProcessVariableIntegerWaveform2() throws ChannelException, InterruptedException, TimeoutException, ExecutionException, IllegalStateException, CAException {
		String channelName = "CH-PSI-CAS:INTEGERWAVEFORM2_TEST";
		int size = 10;
		ProcessVariableGeneric<int[]> statusPV = new ProcessVariableGeneric<int[]>(channelName, null, int[].class, size);

		long time = System.currentTimeMillis();
		int[] v = new int[size];

		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < v.length; ++j) {
				++v[j];
			}
			statusPV.setValue(v, time + i, i);

			assertArrayEquals(v, statusPV.getValue());
			assertEquals(time + i, statusPV.getTimeMillis());
			assertEquals(i, statusPV.getTimeNanoOffset());
		}
	}

	@Test
	public void testProcessVariableString() throws ChannelException, InterruptedException, TimeoutException, ExecutionException, IllegalStateException, CAException {
		String channelName = "CH-PSI-CAS:STRING_TEST";
		List<ProcessVariable> processVariables = new ArrayList<ProcessVariable>();
		ProcessVariableGeneric<String> statusPV = new ProcessVariableGeneric<String>(channelName, null, String.class);

		processVariables.add(statusPV);
		CaServer s = new CaServer(processVariables);
		s.startAsDaemon();

		ChannelService factory = new DefaultChannelService();
		Channel<String> b = factory.createChannel(new ChannelDescriptor<String>(String.class, channelName, true));
		AssertPropertyChangeListener listener = new AssertPropertyChangeListener();
		b.addPropertyChangeListener(listener);

		for (int i = 0; i < 10; i++) {
			String v = b.getValue() + 1;
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
	public void testProcessVariableString2() throws ChannelException, InterruptedException, TimeoutException, ExecutionException, IllegalStateException, CAException {
		String channelName = "CH-PSI-CAS:STRING2_TEST";
		ProcessVariableGeneric<String> statusPV = new ProcessVariableGeneric<String>(channelName, null, String.class);

		long time = System.currentTimeMillis();
		for (int i = 0; i < 10; i++) {
			statusPV.setValue("" + i, time + i, i);

			assertEquals("" + i, statusPV.getValue());
			assertEquals(time + i, statusPV.getTimeMillis());
			assertEquals(i, statusPV.getTimeNanoOffset());
		}
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
