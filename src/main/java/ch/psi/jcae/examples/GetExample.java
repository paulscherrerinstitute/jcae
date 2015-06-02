package ch.psi.jcae.examples;

import ch.psi.jcae.Channel;
import ch.psi.jcae.ChannelDescriptor;
import ch.psi.jcae.ChannelException;
import ch.psi.jcae.ChannelService;
import ch.psi.jcae.impl.DefaultChannelService;
import gov.aps.jca.CAException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GetExample {

    public static void main(String[] args) throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {

        // Get channel factory
        ChannelService factory = new DefaultChannelService();

        // Connect to channel
        Channel<String> channel = factory.createChannel(new ChannelDescriptor<String>(String.class, "ARIDI-PCT:CURRENT", true));

        // Get value
        String value = channel.getValue();
        Logger.getLogger(GetExample.class.getName()).log(Level.INFO, "{0}", value);

        // Wait for a specific value
        channel.waitForValueAsync("").get(1L, TimeUnit.SECONDS);
        
        Future<String> future = channel.waitForValueAsync("");
        while(!future.isDone()){
        	
        }
        
        // Disconnect from channel
        channel.destroy();

        // Close all connections
        factory.destroy();
    }
}
