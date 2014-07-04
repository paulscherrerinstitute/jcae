package ch.psi.jcae.cas;

import java.io.*;
import java.util.logging.Logger;

/**
 * Runnable to read out data from an input stream
 */
public class ProcessStreamProcessor implements Runnable {
	
	
	private static Logger logger = Logger.getLogger(ProcessStreamProcessor.class.getName());
	
	InputStream is;

	/**
	 * Constructor
	 * @param is	InputStream to read out
	 */
	ProcessStreamProcessor(InputStream is) {
		this.is = is;
	}

	public void run() {
		try {
			// Read from input stream and terminate if end of stream is reached
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null){
				logger.info(line);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
