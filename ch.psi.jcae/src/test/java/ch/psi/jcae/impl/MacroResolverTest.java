/**
 * Copyright (c) 2012 Paul Scherrer Institute. All rights reserved.
 */

package ch.psi.jcae.impl;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.junit.Test;

import ch.psi.jcae.impl.MacroResolver;

/**
 * @author ebner
 * 
 */
public class MacroResolverTest {

	private static final Logger logger = Logger.getLogger(MacroResolverTest.class.getName());
	
	/**
	 * Test method for
	 * {@link ch.psi.jcae.impl.MacroResolver#format(java.lang.String, java.util.Map)}
	 * .
	 */
	@Test
	public void testFormat() {
		Map<String, String> map = new HashMap<>();
		map.put("name", "fido");
		map.put("owner", "Jane Doe");
		map.put("gender", "him");

		String format = "My dog is named ${name}, and ${owner} owns ${gender}.";
		String result = MacroResolver.format(format, map);
		logger.info(result);
		
		assertEquals("My dog is named fido, and Jane Doe owns him.", result);
	}

}
