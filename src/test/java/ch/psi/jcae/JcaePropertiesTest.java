package ch.psi.jcae;


import java.util.logging.Logger;

import org.junit.Test;

import ch.psi.jcae.impl.JcaeProperties;

public class JcaePropertiesTest {
	
	private static final Logger logger = Logger.getLogger(JcaePropertiesTest.class.getName());

	@Test
	public void load(){
		JcaeProperties properties = JcaeProperties.getInstance();
		properties.loadProperties();
		
		logger.info("Server port: "+properties.getServerPort());
		logger.info("Auto address list: "+properties.isAutoAddressList());
		logger.info("Address list: "+properties.getAddressList());
	}
}
