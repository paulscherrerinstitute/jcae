/*
 * Copyright (C) 2011 Paul Scherrer Institute
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ch.psi.jcae;

import ch.psi.jcae.impl.ChannelImpl;
import ch.psi.jcae.impl.ChannelServiceImpl;
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
 * 
 * @author ebner
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

		ChannelServiceImpl factory = ChannelServiceImpl.getFactory();

		ChannelImpl<int[]> bean = factory.createChannelBean(int[].class, dataChannel, false);

		ChannelImpl<Integer> mode = factory.createChannelBean(Integer.class, sizeChannel, false);

		int oldmode = mode.getValue();
		logger.log(Level.INFO, "Mode: {0}", mode.getValue());
		logger.log(Level.INFO, "Size: {0}", bean.getSize());

		bean.destroy();

		mode.setValue(1);

		bean = factory.createChannelBean(int[].class, dataChannel, false);

		logger.log(Level.INFO, "Mode: {0}", mode.getValue());
		logger.log(Level.INFO, "Size: {0}", bean.getSize());

		bean.destroy();

		mode.setValue(oldmode);

		bean = factory.createChannelBean(int[].class, dataChannel, false);

		logger.log(Level.INFO, "Mode: {0}", mode.getValue());
		logger.log(Level.INFO, "Size: {0}", bean.getSize());

		// Destroy context of the factory
		ChannelServiceImpl.getFactory().getChannelFactory().destroyContext();
	}

}
