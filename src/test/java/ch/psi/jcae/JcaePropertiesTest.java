/**
 * 
 * Copyright 2010 Paul Scherrer Institute. All rights reserved.
 * 
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This code is distributed in the hope that it will be useful,
 * but without any warranty; without even the implied warranty of
 * merchantability or fitness for a particular purpose. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this code. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

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
