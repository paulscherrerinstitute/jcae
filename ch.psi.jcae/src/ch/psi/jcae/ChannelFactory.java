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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.aps.jca.CAException;
import gov.aps.jca.Channel;
import gov.aps.jca.Context;
import gov.aps.jca.Channel.ConnectionState;

/**
 * Factory class for creating and connecting Channel objects (more easily).
 * The factory establishes channels asynchronously.
 * @author ebner
 *
 */
public class ChannelFactory {
	// Get Logger
	private static final Logger logger = Logger.getLogger(ChannelFactory.class.getName());
	
	private Context context;
	private JcaeProperties properties  = JcaeProperties.getInstance();
	
	/**
	 * Constructor - Create ChannelFactory object. Create required context via the 
	 * an instance of the ContextFactory created with the default constructor (no arguments).
	 * If the file <code>jca.properties</code> is present in the classpath default properties
	 * are overwritten if specified in the file.
	 * @throws CAException
	 */
	public ChannelFactory() throws CAException{
		context = ContextFactory.getInstance().createContext();	
	}
	
	/**
	 * Constructor - Create ChannelFactory object using the passed context.
	 * @param context
	 */
	public ChannelFactory(Context context){
		this.context = context;
	}
	
	/**
	 * Constructor - Create ChannelFactory object using the passed context and default timeout
	 * for channel creation.
	 * 
	 * @param context	JCA Context
	 * @param timeout	Timeout in milliseconds (for creating channel(s))
	 */
	public ChannelFactory(Context context, long timeout){
		this.context = context;
		
		// Overwrite timeout
		properties.setChannelTimeout(timeout);
	}
	
	
	/**
	 * Create specified channel
	 * @param channelName
	 * @return	Channel object for the specified channel name
	 * @throws CAException 
	 * @throws InterruptedException 
	 */
	public Channel createChannel(String channelName) throws CAException, InterruptedException{
		
		// If there is no active context, recreate a context
		if(context==null){
			context = ContextFactory.getInstance().createContext();
		}
		
		int cnt = 0;
		while (cnt <= properties.getConnectionRetries()) {
			cnt++;
			try{
				CountDownLatch latch = new CountDownLatch(1);
				
				Channel channel = context.createChannel(channelName, new ConnectListener(latch));
				context.flushIO();
				
				boolean t;
				t = latch.await(properties.getChannelTimeout(), TimeUnit.MILLISECONDS);
				
				if(t==false){
					throw new CAException("Timout ["+properties.getChannelTimeout()+"] occured while creating channel "+channelName);
				}
				
				return(channel);
			}
			catch(Exception e){
				if(cnt<=properties.getConnectionRetries()){
					logger.log(Level.WARNING, "Unable to connect to channel "+channelName+" - will retry");
				}
				else{
					throw new CAException("Unable to connect to channel "+channelName, e);
				}
			}
		}
		
		throw new CAException("Something went wrong while creating the channel "+channelName+" - Theoretically this exception should never occure");
	}
	
	/**
	 * Create a number of channels at once. This function offers much more performance
	 * if multiple channels need to be connected at once instead of creating each channel
	 * individually via the <code>createChannel()</code>.
	 * @param channelNames		Names of channels to create
	 * @return	List of Channel object for the specified channel names
	 * @throws CAException
	 * @throws InterruptedException 
	 */
	public List<Channel> createChannels(List<String> channelNames) throws CAException, InterruptedException{
		
		// If there is no active context, recreate a context
		if(context==null){
			context = ContextFactory.getInstance().createContext();
		}
		
		List<Channel> channels = new ArrayList<Channel>();
		
		// Return an empty Channel List if null or an empty name List is passed to this function 
		if(channelNames==null || channelNames.size()==0){
			return(channels);
		}

		// Connect channels here
		CountDownLatch latch = new CountDownLatch(channelNames.size());
		
		// Create channels
		
		for(String name: channelNames){
			channels.add(context.createChannel(name, new ConnectListener(latch)));
		}
		context.flushIO();

		// Wait until channels are connected
		boolean t;
		t = latch.await(properties.getChannelTimeout(), TimeUnit.MILLISECONDS);
		
		// Check whether a timeout has occured
		if(t==false){
			// Timeout occurred
			
			// Cleanup all established channels
			StringBuffer b = new StringBuffer();
			for(Channel channel: channels){
				if(! channel.getConnectionState().equals(ConnectionState.CONNECTED)){
					b.append(channel.getName());
					b.append(":");
				}
				// Destroy not connected channels
				channel.destroy();
			}
			
			throw new CAException("Not all Channels are connected. Channels not connected: "+b.toString());
		}
		
		return(channels);
	}
	
	/**
	 * Destroy CA context of the factory
	 * @throws IllegalStateException
	 * @throws CAException
	 */
	public void destroyContext() throws IllegalStateException, CAException{
		if(context!=null){
			context.destroy();
		}
		context=null;
	}
	
}

