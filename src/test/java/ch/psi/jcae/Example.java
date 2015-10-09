package ch.psi.jcae;

import java.util.Properties;
import java.util.concurrent.TimeoutException;

import ch.psi.jcae.impl.JcaeProperties;

public class Example {

	public static void main(String[] args) throws ChannelException, InterruptedException, TimeoutException {
		Properties properties = new Properties();
//		properties.setProperty(Context.Configuration.EPICS_CA_ADDR_LIST.toString(), "172.20.3.50");
		Context context = new Context(properties);
		
		System.out.println(JcaeProperties.getInstance().getAddressList());
		
		Channel<Double> channel = context.createChannel(new ChannelDescriptor<Double>(Double.class, "ARIDI-PCT:CURRENT"));
		
		System.out.println(channel.get());
		
		channel.close();
		context.close();
	}

}
