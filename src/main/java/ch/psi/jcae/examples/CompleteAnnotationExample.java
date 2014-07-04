package ch.psi.jcae.examples;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import gov.aps.jca.CAException;
import ch.psi.jcae.ChannelException;
import ch.psi.jcae.annotation.CaChannel;
import ch.psi.jcae.annotation.CaPostDestroy;
import ch.psi.jcae.annotation.CaPostInit;
import ch.psi.jcae.annotation.CaPreDestroy;
import ch.psi.jcae.annotation.CaPreInit;
import ch.psi.jcae.impl.DefaultChannel;
import ch.psi.jcae.impl.DefaultChannelService;

public class CompleteAnnotationExample {

	public static void main(String[] args) throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {
		// Get channel factory
        DefaultChannelService service = new DefaultChannelService();

        ChannelBeanContainerComplete container = new ChannelBeanContainerComplete();
        
        // Connect to channel(s) in the container
        service.createAnnotatedChannels(container);
        
        Double value = container.getCurrent().getValue();
        String unit = container.getUnit().getValue();
        Logger.getLogger(CompleteAnnotationExample.class.getName()).log(Level.INFO, "Current: {0} [{1}]", new Object[]{value, unit});
        
        // Disconnect channel(s) in the container
        service.destroyAnnotatedChannels(container);
        
        // Destroy context of the factory
        service.destroy();
	}
}

/**
 * Container class
 */
class ChannelBeanContainerComplete {

	@CaChannel(type=Double.class, name="ARIDI-PCT:CURRENT", monitor=true)
	private DefaultChannel<Double> current;
	
	@CaChannel(type=String.class, name="ARIDI-PCT:CURRENT.EGU", monitor=true)
	private DefaultChannel<String> unit;

	@CaPreInit
	public void preInit(){
		// Code executed before connecting the channels
	}
	
	@CaPostInit
	public void postInit(){
		// Code executed after connecting channels
	}
	
	@CaPreDestroy
	public void preDestroy(){
		// Code executed before destroying channels
	}
	
	@CaPostDestroy
	public void postDestroy(){
		// Code executed after destroying channels
	}
	
	/**
	 * @return the current
	 */
	public DefaultChannel<Double> getCurrent() {
		return current;
	}
	
	/**
	 * @return unit of the current
	 */
	public DefaultChannel<String> getUnit() {
		return unit;
	}
}
