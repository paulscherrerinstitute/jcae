package ch.psi.jcae.impl;

import gov.aps.jca.CAException;
import gov.aps.jca.Context;
import gov.aps.jca.JCALibrary;
import gov.aps.jca.configuration.DefaultConfiguration;
import gov.aps.jca.event.QueuedEventDispatcher;

/**
 * Factory class for the creation of Channel Access Context objects.
 */
public class JCAContextFactory {
	
	private JcaeProperties properties;
	
	private final static JCAContextFactory instance = new JCAContextFactory();
	
	/**
	 * Constructor - Create ContextFactory object. Checks and reads <code>jca.properties</code>
	 * properties file for overriding default configuration.
	 */
	private JCAContextFactory(){
		properties = JcaeProperties.getInstance();
	}
	
	/**
	 * Create Channel Access Context. The context will be configured based on the 
	 * configuration in the <code>jca.properties</code> properties file.
	 * @return	Channel Access Context
	 * @throws CAException	Cannot create context based on the current configuration in <code>jca.properties</code>.
	 */
	public Context createContext() throws CAException{
		// Create default context
		DefaultConfiguration configuration = new DefaultConfiguration("context");
		configuration.setAttribute("class", JCALibrary.CHANNEL_ACCESS_JAVA);
		
		configuration.setAttribute("addr_list", properties.getAddressList());
		configuration.setAttribute("auto_addr_list", properties.isAutoAddressList()+"");
		
		if(properties.isQueuedEventDispatcher()){
			// Use QueuedEventDispatcher
			DefaultConfiguration edconf = new DefaultConfiguration("event_dispatcher");
			edconf.setAttribute("class", QueuedEventDispatcher.class.getName());
			configuration.addChild(edconf);
		}
		
		if((properties.getMaxArrayBytes()!=null) && (!properties.getMaxArrayBytes().trim().isEmpty())){
			configuration.setAttribute("max_array_bytes", properties.getMaxArrayBytes());
		}       
                
                if((properties.getMaxSendArrayBytes()!=null) && (!properties.getMaxSendArrayBytes().trim().isEmpty())){
                    System.setProperty("com.cosylab.epics.caj.impl.CachedByteBufferAllocator.buffer_size", properties.getMaxSendArrayBytes());
                }
		
		// Port specified in the jca.properties file does overwrite the SHELL VARIABLE
		if(properties.getServerPort()!=null){
			configuration.setAttribute("server_port", properties.getServerPort());
		}

		Context context;
		try{
			context = JCALibrary.getInstance().createContext(configuration);
		}
		catch (CAException e) {
			// Create a more meaningful exception message
			throw new CAException("Cannot create Context based on the configuration in jca.properties",e);
		}
		return(context);
	}
	
	/**
	 * @return JCAEContextFactory singleton instance
	 */
	public static JCAContextFactory getInstance(){
		return instance;
	}
}
