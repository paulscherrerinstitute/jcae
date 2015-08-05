package ch.psi.jcae;

import java.util.Properties;

import ch.psi.jcae.impl.DefaultChannelService;

public class Context extends DefaultChannelService{
	
	public enum Configuration { EPICS_CA_ADDR_LIST, EPICS_CA_AUTO_ADDR_LIST, EPICS_CA_CONN_TMO,
		EPICS_CA_BEACON_PERIOD, EPICS_CA_REPEATER_PORT, EPICS_CA_SERVER_PORT,
		EPICS_CA_MAX_ARRAY_BYTES };
		
	public Context(){
		super();
	}
	
	public Context(Properties properties){
		super(properties);
	}
}
