package ch.psi.jcae.server;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import sun.misc.Signal;
import sun.misc.SignalHandler;

import gov.aps.jca.CAException;
import gov.aps.jca.JCALibrary;
import gov.aps.jca.cas.ProcessVariable;
import gov.aps.jca.cas.ServerContext;
import gov.aps.jca.configuration.DefaultConfiguration;

import com.cosylab.epics.caj.cas.util.DefaultServerImpl;
import java.util.ArrayList;

public class CaServer implements SignalHandler {

	private static Logger logger = Logger.getLogger(CaServer.class.getName());

	private DefaultConfiguration configuration;
	private ServerContext context;
	private DefaultServerImpl server;

	/**
	 * Constructor: Create instance of a Channel Access server.
	 * 
	 * @param processVariables
	 *            List of process variables that are served by the server
	 */
	public CaServer(List<ProcessVariable> processVariables) {
		server = new DefaultServerImpl();
		// Register process variables on server
		for (ProcessVariable pv : processVariables) {
			server.registerProcessVaribale(pv);
		}

		/**
		 * Create JCA configuration (see: epics environment variables
		 * http://www.
		 * aps.anl.gov/epics/base/R3-14/8-docs/CAref.html#Configurin2)
		 */
		configuration = new DefaultConfiguration("context");
		configuration.setAttribute("class", JCALibrary.CHANNEL_ACCESS_SERVER_JAVA);
	}

	/**
	 * Get configuration of the Channel Access server. This object can be used
	 * to set Epics environment variables listed on
	 * http://www.aps.anl.gov/epics/base/R3-14/8-docs/CAref.html#Configurin2)
	 * 
	 * like: configuration.setAttribute("auto_beacon_addr_list", "false");
	 * configuration.setAttribute("beacon_addr_list", "129.129.130.255");
	 * 
	 * Changes to the configuration will only take effect if they are set before
	 * starting the server.
	 * 
	 * @return Default configuration of the Channel Access server
	 */
	public DefaultConfiguration getConfiguration() {
		return (configuration);
	}

	/**
	 * Start Channel Access server
	 * 
	 * @throws CAException
	 */
	public void start() throws CAException {
		logger.info("Start Channel Access Server");

		context = JCALibrary.getInstance().createServerContext(configuration, server);
		context.run(0);
	}

	/**
	 * Start the server in a new thread
	 */
	public void startAsDeamon() {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					start();
				} catch (CAException e) {
					logger.log(Level.SEVERE, "Exception occured while running channel access server", e);
				}
			}
		});
		t.start();
	}

	/**
	 * Stop Channel Access server
	 * 
	 * @throws IllegalStateException
	 * @throws CAException
	 */
	public void stop() throws IllegalStateException, CAException {
		logger.fine("Stop Channel Access Server");
		context.shutdown();
		context.destroy();
	}

	@Override
	public void handle(Signal signal) {

		try {
			stop();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "An Exception occured while shutting down the Channel Access Server", e);
		}

		logger.info("Server stopped successfully");
		System.exit(0);
	}

	public static void main(String[] args) {
		List<ProcessVariable> processVariables = new ArrayList<ProcessVariable>();
		ProcessVariableIntWaveformVariable waveform = new ProcessVariableIntWaveformVariable("JCAE-TEST-VARWAVE", null);
		ProcessVariableIntWaveformSize size = new ProcessVariableIntWaveformSize("JCAE-TEST-VARWAVE:SIZE", null, waveform);

		// processVariables.add(new ProcessVariableInt(channelBaseName+".INT",
		// null));
		// processVariables.add(new
		// ProcessVariableIntWaveform(channelBaseName+".INTWF", null));
		// processVariables.add(new
		// ProcessVariableExecute(channelBaseName+".EXEC",null, script));
		// processVariables.add(new
		// ProcessVariableDoubleWaveform(channelBaseName+".WAVE",null, 5));

		processVariables.add(size);
		processVariables.add(waveform);
		// Create server
		CaServer s = new CaServer(processVariables);
		s.startAsDeamon();

	}
}
