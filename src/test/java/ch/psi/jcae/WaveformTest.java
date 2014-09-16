package ch.psi.jcae;

import ch.psi.jcae.cas.CaServer;
import ch.psi.jcae.cas.ProcessVariableGeneric;
import ch.psi.jcae.impl.DefaultChannelService;
import gov.aps.jca.CAException;
import gov.aps.jca.cas.ProcessVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

/**
 * Tests waveform size changes Waveform size changes require that all the
 * channels in the context for the waveform are closed and that then a new
 * channel is created.
 */
public class WaveformTest {

	private static final Logger logger = Logger.getLogger(WaveformTest.class.getName());

	@Test
	public void testWaveform() throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {
		int arraySize = 10;
		String dataChannel = "JCAE-TEST-VARWAVE";
		String sizeChannel = "JCAE-TEST-VARWAVE:SIZE";

		List<ProcessVariable> processVariables = new ArrayList<ProcessVariable>();
		ProcessVariableGeneric<int[]> waveform = new ProcessVariableGeneric<int[]>(dataChannel, null, int[].class, arraySize);
		processVariables.add(waveform);
		ProcessVariableGeneric<Integer> size = new ProcessVariableGeneric<Integer>(sizeChannel, null, Integer.class);
		processVariables.add(size);

		// Create server
		CaServer s = new CaServer(processVariables);
		s.startAsDaemon();

		DefaultChannelService cservice = new DefaultChannelService();

		Channel<int[]> bean = cservice.createChannel(new ChannelDescriptor<int[]>(int[].class, dataChannel));

		Channel<Integer> mode = cservice.createChannel(new ChannelDescriptor<Integer>(Integer.class, sizeChannel));

		int oldmode = mode.getValue();
		logger.log(Level.INFO, "Mode: {0}", mode.getValue());
		logger.log(Level.INFO, "Size: {0}", bean.getSize());

		bean.destroy();

		mode.setValue(1);

		bean = cservice.createChannel(new ChannelDescriptor<int[]>(int[].class, dataChannel));

		logger.log(Level.INFO, "Mode: {0}", mode.getValue());
		logger.log(Level.INFO, "Size: {0}", bean.getSize());

		bean.destroy();

		mode.setValue(oldmode);

		bean = cservice.createChannel(new ChannelDescriptor<int[]>(int[].class, dataChannel));

		logger.log(Level.INFO, "Mode: {0}", mode.getValue());
		logger.log(Level.INFO, "Size: {0}", bean.getSize());

		// Destroy context of the factory
		cservice.destroy();
	}

}
