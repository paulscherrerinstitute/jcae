/**
 * Copyright (c) 2012 Paul Scherrer Institute. All rights reserved.
 */

package ch.psi.jcae;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import ch.psi.jcae.impl.ChannelServiceImpl;

/**
 * @author ebner
 * 
 */
public class Activator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		System.out.println("Start ChannelService");
		Hashtable<String, String> properties = new Hashtable<>();
		properties.put("id", "blablub");
		context.registerService(ChannelService.class.getName(), new ChannelServiceImpl(), (Dictionary<String,String>) properties);
	}

	public void stop(BundleContext context) throws Exception {
		// Services are automatically unregistered
		System.out.println("Stop ChannelService");
	}

}
