package ch.psi.jcae.examples;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.aps.jca.CAException;
import ch.psi.jcae.Channel;
import ch.psi.jcae.ChannelException;
import ch.psi.jcae.ChannelService;
import ch.psi.jcae.annotation.CaChannel;
import ch.psi.jcae.impl.DefaultChannelService;

public class AnnotationExample {

	public static void main(String[] args) throws InterruptedException, TimeoutException, ChannelException, CAException, ExecutionException {
		// Get channel factory
        ChannelService service = new DefaultChannelService();

        ChannelBeanContainer container = new ChannelBeanContainer();
        
        // Connect to channel(s) in the container
        Map<String,String> macros = new HashMap<>();
        macros.put("MACRO_1", "ARIDI");
        macros.put("MACRO_2", "PCT");
        service.createAnnotatedChannels(container, macros);
        
        Double value = container.getCurrent().getValue();
        String unit = container.getUnit().getValue();
        Logger.getLogger(AnnotationExample.class.getName()).log(Level.INFO, "Current: {0} [{1}]", new Object[]{value, unit});
        
        // Disconnect channel(s) in the container
        service.destroyAnnotatedChannels(container);
        
        // Destroy context of the factory
        service.destroy();
	}
}

/**
 * Container class
 */
class ChannelBeanContainer {

	@CaChannel(type=Double.class, name="${MACRO_1}-${MACRO_2}:CURRENT", monitor=true)
	private Channel<Double> current;
	
	@CaChannel(type=String.class, name="${MACRO_1}-${MACRO_2}:CURRENT.EGU", monitor=true)
	private Channel<String> unit;

	public Channel<Double> getCurrent() {
		return current;
	}

	public Channel<String> getUnit() {
		return unit;
	}
}
