package ch.psi.jcae.examples;

import ch.psi.jcae.Channel;
import ch.psi.jcae.ChannelDescriptor;
import ch.psi.jcae.ChannelException;
import ch.psi.jcae.ChannelService;
import ch.psi.jcae.impl.DefaultChannelService;
import gov.aps.jca.CAException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AsynchronousExample {

    public static void main(String[] args) throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {

        // Get channel factory
        ChannelService context = new DefaultChannelService();

        // Connect to channel
        Channel<String> channel = context.createChannel(new ChannelDescriptor<String>(String.class, "ARIDI-PCT:CURRENT"));

        // Get value
        Future<String> futureValue = channel.getValueAsync();
//        Future<String> future = channel.setValueAsync("value");
        
        // ... Do lots of stuff
        System.out.println("... doing heavy work ...");
        
        String value = futureValue.get();
//        String valueset = future.get();
        Logger.getLogger(AsynchronousExample.class.getName()).log(Level.INFO, "{0}", value);

        // Disconnect from channel
        channel.destroy();

        // Close all connections
        context.destroy();
    }
}
