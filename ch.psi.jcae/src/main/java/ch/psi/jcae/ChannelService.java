/**
 * Copyright (c) 2012 Paul Scherrer Institute. All rights reserved.
 */

package ch.psi.jcae;

//import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @author ebner
 *
 */
public interface ChannelService {

	public <T> Channel<T> createChannel(Descriptor<T> descriptor) throws ChannelException, InterruptedException, TimeoutException;
//	public <T> void destroyChannel(Channel<T> channel);
	public List<Channel<?>> createChannels(List<Descriptor<?>> descriptors) throws ChannelException, InterruptedException, TimeoutException;
//	public <T> void destroyChannels(Collection<Channel<?>> channels);

	
	
	// Annotation related functions
	public void createAnnotatedChannels(Object object) throws ChannelException, InterruptedException, TimeoutException;
	public void createAnnotatedChannels(Object object, Map<String,String> macros) throws ChannelException, InterruptedException, TimeoutException;
	public void destroyAnnotatedChannels(Object object) throws ChannelException;
	
	
	public void destroy();
}
