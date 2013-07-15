/**
 * Copyright (c) 2012 Paul Scherrer Institute. All rights reserved.
 */

package ch.psi.jcae;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Service to create and manage channels.
 */
public interface ChannelService {

	public <T> Channel<T> createChannel(Descriptor<T> descriptor) throws ChannelException, InterruptedException, TimeoutException;
	public List<Channel<?>> createChannels(List<Descriptor<?>> descriptors) throws ChannelException, InterruptedException, TimeoutException;
	
	// Annotation related functions
	public void createAnnotatedChannels(Object object) throws ChannelException, InterruptedException, TimeoutException;
	public void createAnnotatedChannels(Object object, Map<String,String> macros) throws ChannelException, InterruptedException, TimeoutException;
	public void createAnnotatedChannels(Object object, boolean dryrun) throws ChannelException, InterruptedException, TimeoutException;
	public void createAnnotatedChannels(Object object, Map<String,String> macros, boolean dryrun) throws ChannelException, InterruptedException, TimeoutException;
	public void destroyAnnotatedChannels(Object object) throws ChannelException;
	
	/**
	 * Get (global) macros used by this service.
	 * This function can be used to change macros used for creating channels
	 * @return
	 */
	public Map<String,String> getMacros();
	
	public void destroy();
}
