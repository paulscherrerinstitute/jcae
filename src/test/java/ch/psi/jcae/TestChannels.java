package ch.psi.jcae;

import java.util.ArrayList;
import java.util.List;

import gov.aps.jca.CAException;
import gov.aps.jca.cas.ProcessVariable;
import ch.psi.jcae.cas.CaServer;
import ch.psi.jcae.cas.ProcessVariableGeneric;

public class TestChannels {
	public static final String PREFIX = "MTEST-PC-JCAE:";
	
	public static final String CHARACTER_WAVEFORM = PREFIX+"CWAVE";
	public static final String DOUBLE_WAVEFORM = PREFIX+"DWAVE";
	public static final String STRING_WAVEFORM = PREFIX+"SWAVE";
	
	public static final String BINARY_IN = PREFIX+"BI";
	public static final String BINARY_OUT = PREFIX+"BO";
	public static final String BINARY_OUT_NOT_EXIST = PREFIX+"BOX";
	
	public static final String ANALOG_OUT = PREFIX+"AO";
	
	public static final String STRING_OUT1 = PREFIX+"SOUT1";
	public static final String STRING_OUT2 = PREFIX+"SOUT2";
	public static final String STRING_OUT3 = PREFIX+"SOUT3";
	
	private CaServer server;
	
	public void start(){
		
		List<ProcessVariable> processVariables = new ArrayList<ProcessVariable>();
		
		processVariables.add(new ProcessVariableGeneric<byte[]>(CHARACTER_WAVEFORM, null, byte[].class, 10));
		processVariables.add(new ProcessVariableGeneric<double[]>(DOUBLE_WAVEFORM, null, double[].class, 10));
		processVariables.add(new ProcessVariableGeneric<String[]>(STRING_WAVEFORM, null, String[].class, 10));
		processVariables.add(new ProcessVariableGeneric<Integer>(BINARY_IN, null, Integer.class));
		processVariables.add(new ProcessVariableGeneric<Integer>(BINARY_OUT, null, Integer.class));
		processVariables.add(new ProcessVariableGeneric<Double>(ANALOG_OUT, null, Double.class));
		processVariables.add(new ProcessVariableGeneric<String>(STRING_OUT1, null, String.class));
		processVariables.add(new ProcessVariableGeneric<String>(STRING_OUT2, null, String.class));
		processVariables.add(new ProcessVariableGeneric<String>(STRING_OUT3, null, String.class));
		processVariables.add(new ProcessVariableGeneric<String>(PREFIX+"SOUT5", null, String.class));
		processVariables.add(new ProcessVariableGeneric<String>(PREFIX+"SOUT4", null, String.class));
		
		
		server = new CaServer(processVariables);
		server.startAsDaemon();
	}
	
	public void stop(){
		try {
			server.stop();
		} catch (IllegalStateException | CAException e) {
			throw new RuntimeException("Unable to stop ChannelAccess server", e);
		}
	}
}
