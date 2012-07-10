/*
 * $Source: /cvs/X/XASEC/ch.psi.sls.xasec.caserver/src/ch/psi/sls/xasec/caserver/ProcessStreamProcessor.java,v $
 * $Author: ebner $
 * $Revision: 1.1 $   $Date: 2010/03/30 09:41:54 $
 */
package ch.psi.jcae.server;

import java.io.*;
import java.util.logging.Logger;

public class ProcessStreamProcessor extends Thread {
	
	
	private static Logger logger = Logger.getLogger(ProcessStreamProcessor.class.getName());
	
	InputStream is;

	ProcessStreamProcessor(InputStream is) {
		this.is = is;
	}

	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null)
				logger.info(line);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
