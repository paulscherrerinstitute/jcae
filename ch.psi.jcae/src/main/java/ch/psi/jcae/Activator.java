/**
 * Copyright (c) 2012 Paul Scherrer Institute. All rights reserved.
 */

package ch.psi.jcae;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.logging.Logger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import ch.psi.jcae.impl.DefaultChannelService;

/**
 * @author ebner
 * 
 */
public class Activator implements BundleActivator {

	private static final Logger logger = Logger.getLogger(Activator.class.getName());
	private ChannelService service;
	
	public void start(BundleContext context) throws Exception {
		logger.info("Start ChannelService");
		Hashtable<String, String> properties = new Hashtable<>();
		properties.put("id", "blablub");
		service = new DefaultChannelService();
		context.registerService(ChannelService.class.getName(), service, (Dictionary<String,String>) properties);
	}

	public void stop(BundleContext context) throws Exception {
		// Services are automatically unregistered
		logger.info("Stop ChannelService");
		service.destroy();
	}

}