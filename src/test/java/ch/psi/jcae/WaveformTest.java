package ch.psi.jcae;

import ch.psi.jcae.impl.DefaultChannelService;
import ch.psi.jcae.server.CaServer;
import gov.aps.jca.CAException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

/**
 * Tests waveform size changes
 * Waveform size changes require that all the channels in the context for the waveform are closed
 * and that then a new channel is created.
 */
public class WaveformTest {

	private static final Logger logger = Logger.getLogger(WaveformTest.class.getName());
	
	@Test
	public void testWaveform() throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {

		// TODO Verify usefullness of the test
		
		// Workaround
		CaServer.main(new String[] {});

		String dataChannel = "JCAE-TEST-VARWAVE";
		String sizeChannel = "JCAE-TEST-VARWAVE:SIZE";

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
