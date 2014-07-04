package ch.psi.jcae.examples;

import ch.psi.jcae.Channel;
import ch.psi.jcae.ChannelDescriptor;
import ch.psi.jcae.ChannelException;
import ch.psi.jcae.impl.DefaultChannel;
import ch.psi.jcae.impl.DefaultChannelService;
import gov.aps.jca.CAException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MonitorExample {

    public static void main(String[] args) throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {
        // Get channel factory
        DefaultChannelService service = new DefaultChannelService();

        // Create ChannelBean
        Channel<String> bean = service.createChannel(new ChannelDescriptor<String>(String.class, "ARIDI-PCT:CURRENT", true));

        // Add PropertyChangeListener to ChannelBean to get value updates
        bean.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent pce) {
                if (pce.getPropertyName().equals(DefaultChannel.PROPERTY_VALUE)) {
                    Logger.getLogger(MonitorExample.class.getName()).log(Level.INFO, "Current: {0}", pce.getNewValue());
                }
            }
        });

        // Monitor the Channel for 10 seconds
        Thread.sleep(10000);

        // Destroy ChannelBean
        bean.destroy();

        // Destroy context of the factory
        service.destroy();
    }
}