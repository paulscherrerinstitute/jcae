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

public class MacroResolverTest {

	private static final Logger logger = Logger.getLogger(MacroResolverTest.class.getName());
	
	/**
	 * Test method for
	 * {@link ch.psi.jcae.impl.MacroResolver#format(java.lang.String, java.util.Map)}
	 * .
	 */
	@Test
	public void testFormat() {
		Map<String, String> map = new HashMap<String,String>();
		map.put("name", "fido");
		map.put("owner", "Jane Doe");
		map.put("gender", "him");

		String format = "My dog is named ${name}, and ${owner} owns ${gender}.";
		String result = MacroResolver.format(format, map);
		logger.info(result);
		
		assertEquals("My dog is named fido, and Jane Doe owns him.", result);
	}
	
	/**
	 * Test whether a non existing macro is replaced the correct way
	 */
	@Test
	public void testFormatNonexistingMacro() {
		Map<String, String> map = new HashMap<String,String>();
		map.put("macro1", "fido");
		map.put("macro2", "Jane Doe");

		String format = "${macro2}-${macro1}-${macrono}-${macro2}";
		String result = MacroResolver.format(format, map);
		logger.info(result);
		
		assertEquals(map.get("macro2")+"-"+map.get("macro1")+"-${macrono}-"+map.get("macro2"), result);
	}

}
