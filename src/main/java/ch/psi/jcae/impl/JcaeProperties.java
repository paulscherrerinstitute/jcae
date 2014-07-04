package ch.psi.jcae.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Properties singleton object for the jca configuration.
 * By default this class uses a jca.properties file that resides in the classpath.
 */
public class JcaeProperties {
	
	public final static String JCAE_CONFIG_FILE_ARGUMENT = "ch.psi.jcae.config.file";
	
	private static final Logger logger = Logger.getLogger(JcaeProperties.class.getName());
	
	/**
	 * Singleton instance of this class
	 */
	private static JcaeProperties instance = new JcaeProperties();
	
	// ContextFactory related properties
	private String addressList = "";
	private boolean autoAddressList = true;
	private boolean useShellVariables = false; // default values
	private boolean addLocalBroadcastInterfaces = false;
	private boolean queuedEventDispatcher = true;
	private String maxArrayBytes = null;
	private String serverPort = null;
	/**
	 * While waiting for a channel to get a certain value usually a monitor for the channel 
	 * is created. If the value is not reached within the waitTimeout time the function returns
	 * with an Exception. Sometimes this behavior is not sufficient to get the channel value
	 * changes. In corrupted environments sometimes the monitor notification for a value change
	 * gets lost. Then the wait function will not return and eventually fail.
	 * To be more robust in these situations a wait retry period can be specified.
	 * the waitTimeout is then split up in several pieces of the waitRetryPeriod length.
	 * For each piece a new monitor gets created. To ensure that no event is lost, the destruction of
	 * the monitor of the period before is at a time where the new monitor of the new period is already created.
	 * By this behavior the scenario mentioned before is not possible any more.
	 */
	private Long waitRetryPeriod = null;
	
	// ChannelFactory related properties
	/**
	 * Connection timeout (in milliseconds) for connecting to a channel
	 */
	private long channelTimeout = 10000;
	
	/**
	 * Default request timeout for get/set operations of a ChannelBean
	 */
	private long requestTimeout = 60000; // 1 minute
	
	/**
	 * Default wait timeout for wait operations of a ChannelBean (default is wait forever)
	 */
	private Long waitTimeout = null;
	
	/**
	 * Retries for get/set operations if a channel is not available, etc. ...
	 */
	private int retries = 0;
	
	/**
	 * Retries for opening/creating a channel (if channel was not connected in time (connectionTimeout) )
	 * Retries does only apply while creating one channel. While creating multiple channels at once retries
	 * is not used
	 */
	private int connectionRetries = 0;
	
	
	/**
	 * Overwrite default construtor with private constructor
	 */
	private JcaeProperties(){
		loadProperties();
	}

	public static JcaeProperties getInstance(){
		return(instance);
	}
	
	public void loadProperties(){
		try {
			// Check system property for configuration file
			String property = System.getProperty(JCAE_CONFIG_FILE_ARGUMENT);
			
			if(property == null){
				// When no system property is specified search the classpath for a jcae.properties file
				InputStream s = this.getClass().getResourceAsStream("/jcae.properties");
				loadProperties(s);
			}
			else{
				loadProperties(new File(property));
			}			
		} catch (Exception e) {
			// We will silently ignore the fact that there is no jca.properties file as in some cases this file is not required 
			logger.log(Level.FINE, "Unable to load jcae.properties file - will use defaults", e);
		}
	}
	
	/**
	 * Load properties from specified file
	 * @param file	File to load properties from
	 * @throws FileNotFoundException	File not found
	 * @throws IOException				Unable to read file
	 */
	public void loadProperties(File file) throws FileNotFoundException, IOException{
		loadProperties(new FileInputStream(file));
	}
	
	/**
	 * Load properties from specified InputStream
	 * @param stream		Input stream to read properties from
	 * @throws IOException	Unable to read input stream
	 */
	public void loadProperties(InputStream stream) throws IOException {

		Properties bundle = new Properties();
		bundle.load(stream);

		/**
		 * Get properties for the ContextFactory
		 */
//		String prefix = ContextFactory.class.getCanonicalName() + ".";
		String prefix = "ch.psi.jcae.ContextFactory.";
		String key = "";

		if (bundle.keySet().contains(prefix + "addressList")) {
			this.addressList = bundle.getProperty(prefix + "addressList");
		}

		if (bundle.keySet().contains(prefix + "autoAddressList")) {
			String s = bundle.getProperty(prefix + "autoAddressList");
			this.autoAddressList = true;
			if (s.equalsIgnoreCase("false")) {
				this.autoAddressList = false;
			}
		}

		if (bundle.keySet().contains(prefix + "useShellVariables")) {
			String s = bundle.getProperty(prefix + "useShellVariables");
			this.useShellVariables = false;
			if (s.equalsIgnoreCase("true")) {
				this.useShellVariables = true;
			}
		}
		if (bundle.keySet().contains(prefix + "addLocalBroadcastInterfaces")) {
			String s = bundle.getProperty(prefix + "addLocalBroadcastInterfaces");
			addLocalBroadcastInterfaces = false;
			if (s.equalsIgnoreCase("true")) {
				addLocalBroadcastInterfaces = true;
			}
		}
		if (bundle.keySet().contains(prefix + "queuedEventDispatcher")) {
			String s = bundle.getProperty(prefix + "queuedEventDispatcher");
			queuedEventDispatcher = true;
			if (s.equalsIgnoreCase("false")) {
				queuedEventDispatcher = false;
			}
		}
		if (bundle.keySet().contains(prefix + "maxArrayBytes")) {
			maxArrayBytes = bundle.getProperty(prefix + "maxArrayBytes");
		}
		if (bundle.keySet().contains(prefix + "serverPort")) {
			serverPort = bundle.getProperty(prefix + "serverPort");
		}
		
		/**
		 * Get properties for the ChannelService
		 */
		// TODO rename to ChannelService
		prefix = "ch.psi.jcae.ChannelFactory.";
		if(bundle.keySet().contains(prefix+"timeout")){
			
			String s = bundle.getProperty(prefix+"timeout");
			try{
				channelTimeout = Long.parseLong(s);
			}
			catch(NumberFormatException e){
				logger.log(Level.WARNING, "Property timeout can not be parsed to a long", e);
			}
		}

		if(bundle.keySet().contains(prefix+"retries")){
			
			String s = bundle.getProperty(prefix+"retries");
			try{
				connectionRetries = Integer.parseInt(s);
			}
			catch(NumberFormatException e){
				logger.log(Level.WARNING, "Property retries can not be parsed to an int", e);
			}
		}
		
		
		
		/**
		 * Get properties for the ChannelBeanFactory
		 */
		prefix = DefaultChannelService.class.getCanonicalName()+".";
		key = prefix+"timeout";
		if(bundle.keySet().contains(key)){
			String s = bundle.getProperty(key);
			try{
				requestTimeout = Long.parseLong(s);
			}
			catch(NumberFormatException e){
				logger.log(Level.WARNING,"Property "+key+" can not be parsed to a long", e);
			}
		}
		
		key = prefix+"waitTimeout";
		if(bundle.keySet().contains(key)){
			String s = bundle.getProperty(key);
			try{
				Long wtimeout = Long.parseLong(s);
				if(wtimeout>0){
					waitTimeout = wtimeout;
				}
				else{
					logger.log(Level.WARNING,"Wait timeout must be > 0 - will take default");
				}
			}
			catch(NumberFormatException e){
				logger.log(Level.WARNING,"Property "+key+" can not be parsed to a long - will take default", e);
			}
		}
		
		key = prefix+"waitRetryPeriod";
		if(bundle.keySet().contains(key)){
			String s = bundle.getProperty(key);
			try{
				waitRetryPeriod = Long.parseLong(s);
			}
			catch(NumberFormatException e){
				logger.log(Level.WARNING,"Property "+key+" can not be parsed to a long", e);
			}
		}
		
		key = prefix+"retries";
		if(bundle.keySet().contains(key)){
			String s = bundle.getProperty(key);
			try{
				retries = Integer.parseInt(s);
			}
			catch(NumberFormatException e){
				logger.log(Level.WARNING,"Property "+key+" can not be parsed to a long", e);
			}
		}
		
		
		// If use shell variables is set to true overwrite properties with shell variables if they are set
		if(useShellVariables){
			Boolean bvalue = getShellEpicsCaAutoAddressList(); 
			if(bvalue !=null ){
				 autoAddressList = bvalue;
			}
			else{
				// Default
				autoAddressList = true;
			}
			String value = getShellEpicsCaAddressList();
			if(value != null){
				addressList = value;
			}
			else{
				// Default
				addressList = "";
			}
			value = getShellEpicsServerPort();
			if(value != null){
				serverPort = value;
			}
			else{
				serverPort = null;
			}
		}
		
		// If add local broadcast interfaces is set to true, add these to the address list
		if(addLocalBroadcastInterfaces){
			addressList = addressList+" "+getLocalBroadcastAddresses();
		}
		
	}
	
	public String getAddressList() {
		return addressList;
	}
	public void setAddressList(String addressList) {
		this.addressList = addressList;
	}
	public boolean isAutoAddressList() {
		return autoAddressList;
	}

	public void setAutoAddressList(boolean autoAddressList) {
		this.autoAddressList = autoAddressList;
	}

	public boolean isUseShellVariables() {
		return useShellVariables;
	}

	public void setUseShellVariables(boolean useShellVariables) {
		this.useShellVariables = useShellVariables;
	}

	public boolean isAddLocalBroadcastInterfaces() {
		return addLocalBroadcastInterfaces;
	}

	public void setAddLocalBroadcastInterfaces(boolean addLocalBroadcastInterfaces) {
		this.addLocalBroadcastInterfaces = addLocalBroadcastInterfaces;
	}

	public boolean isQueuedEventDispatcher() {
		return queuedEventDispatcher;
	}

	public void setQueuedEventDispatcher(boolean queuedEventDispatcher) {
		this.queuedEventDispatcher = queuedEventDispatcher;
	}

	public String getMaxArrayBytes() {
		return maxArrayBytes;
	}

	public void setMaxArrayBytes(String maxArrayBytes) {
		this.maxArrayBytes = maxArrayBytes;
	}

	public long getChannelTimeout() {
		return channelTimeout;
	}

	public void setChannelTimeout(long timeout) {
		this.channelTimeout = timeout;
	}

	public long getRequestTimeout() {
		return requestTimeout;
	}

	public void setRequestTimeout(long requestTimeout) {
		this.requestTimeout = requestTimeout;
	}

	public Long getWaitTimeout() {
		return waitTimeout;
	}

	public void setWaitTimeout(Long waitTimeout) {
		this.waitTimeout = waitTimeout;
	}

	public int getRetries() {
		return retries;
	}

	public void setRetries(int retries) {
		this.retries = retries;
	}

	public String getServerPort() {
		return serverPort;
	}

	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}

	public int getConnectionRetries() {
		return connectionRetries;
	}

	public void setConnectionRetries(int connectionRetries) {
		this.connectionRetries = connectionRetries;
	}

	public Long getWaitRetryPeriod() {
		return waitRetryPeriod;
	}

	public void setWaitRetryPeriod(Long waitRetryPeriod) {
		this.waitRetryPeriod = waitRetryPeriod;
	}
	
	
	/**
	 * Get the Epics Address List set in the <code>EPICS_CA_ADDR_LIST</code> shell variable.
	 * @return	-
	 */
	private static String getShellEpicsCaAddressList(){
		// Get EPICS_CA_ADDR_LIST environment variable from shell
        String addressList = System.getenv("EPICS_CA_ADDR_LIST");
        if (addressList != null) {
        	return(addressList.trim());
        }
		return(null);
	}

	/**
	 * Get whether auto address list should be used from the <code>EPICS_CA_AUTO_ADDR_LIST</code> shell variable.
	 * @return	-
	 */
	private static Boolean getShellEpicsCaAutoAddressList(){
		String useList = System.getenv("EPICS_CA_AUTO_ADDR_LIST");
		if (useList != null ) {
			if(useList.equalsIgnoreCase("YES")){
				return(true);
			}
			return(false);
		}
		return(null);
	}
	
	/**
	 * Get Server Port from the shell variable
	 * @return
	 */
	private static String getShellEpicsServerPort(){
		String port = System.getenv("EPICS_CA_SERVER_PORT");
		if (port != null) {
			return(port);
		}
		return null;
	}
	
	/**
	 * Get a space separated list of all broadcast addresses of local interfaces.
	 * The loopback interface (127.0.0.0) and IPv6 interfaces do not have
	 * broadcast addresses therefore they will not be in the list.
	 * @return	String holding a list of local broadcast addresses
	 */
	private static String getLocalBroadcastAddresses(){
		StringBuffer bAddressList = new StringBuffer();

        // Loop host interfaces
        Enumeration<NetworkInterface> networkInterfaces;
		try {
			networkInterfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			// Something went wrong while determining the network interfaces of the current machine.
			// As there is nothing a user can do, turn this exception into a unchecked RuntimeException.
			throw new RuntimeException("Unable to determine network interfaces of the machine", e);
		}
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();

            // Loop IP addresses of interface
            List<InterfaceAddress> interfaceAddresses = networkInterface.getInterfaceAddresses();
            
            for(InterfaceAddress interfaceAddress: interfaceAddresses){
				InetAddress broadcastAddress = interfaceAddress.getBroadcast();
				if (broadcastAddress != null) {
					if(bAddressList.length()>0){
						bAddressList.append(" ");
					}
					// Use of .substring(1) to remove leading "/" character
					bAddressList.append(broadcastAddress.toString().substring(1));
				}
            }
        }
        return(bAddressList.toString().trim());
	}
}
