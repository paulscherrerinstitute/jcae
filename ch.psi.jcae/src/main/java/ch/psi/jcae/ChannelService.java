/**
 * Copyright (c) 2012 Paul Scherrer Institute. All rights reserved.
 */

package ch.psi.jcae;

import java.util.Collection;
import java.util.List;

/**
 * @author ebner
 *
 */
public interface ChannelService {

	public <T> Channel<T> createChannel(ChannelDescriptor<T> descriptor);
	public <T> void destroyChannel(Channel<T> channel);
	
	public <T> List<Channel<T>> createChannels(List<ChannelDescriptor<?>> descriptors);
	public <T> void destroyChannels(Collection<Channel<?>> channels);

	
	// Annotation related functions
	public void createAnnotatedChannels(Object object);
	public void createAnnotatedChannels(Object object, String prefix);
	public void destroyAnnotatedChannels(Object object);
	
}
