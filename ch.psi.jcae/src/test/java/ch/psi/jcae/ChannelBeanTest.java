/**
 * 
 * Copyright 2010 Paul Scherrer Institute. All rights reserved.
 * 
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This code is distributed in the hope that it will be useful,
 * but without any warranty; without even the implied warranty of
 * merchantability or fitness for a particular purpose. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this code. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package ch.psi.jcae;

import static org.junit.Assert.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import junit.framework.Assert;

import gov.aps.jca.CAException;

import org.junit.Before;
import org.junit.Test;

import ch.psi.jcae.impl.ChannelBean;
import ch.psi.jcae.impl.ChannelBeanFactory;
import ch.psi.jcae.impl.type.DoubleTimestamp;

/**
 * JUnit test case for testing the functionality of a <code>ChannelBean</code>
 * @author ebner
 *
 */
public class ChannelBeanTest {
	
	private static Logger logger = Logger.getLogger(ChannelBeanTest.class.getName());
	
//	private static String channelPrefix = "MTEST-PC-JCAE:";
	private static String iocname = "psi-softioc.psi.ch";
	private ChannelBeanFactory factory;
	
	@Before
	public void setUp() throws Exception {
		factory = ChannelBeanFactory.getFactory();
	}

	/**
	 * Test to continuously set and get a string waveform
	 * @throws Exception 
	 */
	@Test
	public void testSetGetValueStringWaveform() throws Exception {
		try{
		ChannelBean<String[]> bean = factory.createChannelBean(String[].class, TestChannels.STRING_WAVEFORM, false);
		
//		List<String> list = new ArrayList<String>();
		
		// Set test string to waveform
		String[] value = {"one....................", "two....................", "three....................", "four....................", "five....................", "six....................", "seven....................", "eight....................", "nine....................", "ten...................."};
//		for(String v : value){
//			list.add(v);
//		}
		
		String[] value2 = {"", "", "", "", "", "", "", ""};
		for(int i=0;i<5000;i++){
			// Alternate values to set
			if(i%2==0){
				bean.setValue(value);
			}
			else{
				bean.setValue(value2);
			}
			
//			list.add("");
//			bean.setValue(list.toArray(new String[list.size()]));
			
			System.out.print(i+" ");
			if(i%100==0){
				System.out.println();
			}
			Thread.sleep(10);
			bean.getValue();
			
		}
		}
		catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		
	}
	
	
	/**
	 * Test to verify whether a char waveform can be easily accessed and
	 * be converted to a String
	 * @throws CAException 
	 * @throws InterruptedException 
	 * @throws ChannelException 
	 * @throws TimeoutException 
	 * @throws ExecutionException 
	 */
	@Test
	public void testGetValueWaveform() throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {
		ChannelBean<byte[]> bean = factory.createChannelBean(byte[].class, TestChannels.CHARACTER_WAVEFORM, false);

		// Set test string to waveform
		String setvalue = "some value";
		bean.setValue(setvalue.getBytes());
		
		// Get total size of the waveform
		int size = bean.getSize(); // Will return the maximum number of waveform elements. There is no way to only return the number of used elements.
		logger.fine("Maximum number of waveform elements: " + size);

		// Get value from waveform
		byte[] value = bean.getValue();
		int valueSize = value.length;
		
		// Check whether the size of the value matches the maximum size of elements ()
		if(size != valueSize){
			Assert.fail("getValue() does not return all the waveform elements");
		}

		// Check whether converted String value is the same than the one that was set 
		String svalue = new String(value);
		svalue = svalue.trim();
		if(! setvalue.equals(svalue)){
			Assert.fail("The returned channel value does not match to the one set");
		}
		logger.fine("String returned: "+svalue+" Size: "+svalue.length());
		
		
		// Test whether only a substring can be retrieved from the waveform channel
		value = Arrays.copyOf(bean.getValue(),4);// only get the 4 first characters
		svalue = new String(value);
		svalue = svalue.trim();
		if(! "some".equals(svalue)){
			Assert.fail("The returned sub channel value does not match to the one set");
		}
		logger.fine("String returned: "+svalue+" Size: "+svalue.length());
	}
	
	
	/**
	 * Test get IOC name of the channel
	 * @throws CAException 
	 * @throws InterruptedException 
	 * @throws ChannelException 
	 * @throws TimeoutException 
	 * @throws ExecutionException 
	 */
	@Test
	public void testGetHostname() throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {
		ChannelBean<String> bean = factory.createChannelBean(String.class, TestChannels.BINARY_IN, false);
		logger.fine("Size of the Channel: "+bean.getHostname());
		if(! bean.getHostname().equals(iocname)){
			Assert.fail("Ioc name returned does not match the expected ioc name");
		}
	}
	
	
	@Test
	public void testGetValueComplexType() throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {
		// Test if array and getValue(int size) is called
		ChannelBean<DoubleTimestamp> bean = factory.createChannelBean(DoubleTimestamp.class, TestChannels.BINARY_IN, true);
		DoubleTimestamp v = bean.getValue();
		System.out.printf("%f %s offset: %d\n",v.getValue(), v.getTimestamp(), v.getNanosecondOffset());
		bean.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				DoubleTimestamp t = ((DoubleTimestamp) evt.getNewValue());
				System.out.printf("event: %f %s %d\n", t.getValue(), t.getTimestamp(), t.getNanosecondOffset());
			}
		});

		ChannelBean<Double> beand = factory.createChannelBean(Double.class, TestChannels.BINARY_IN, false);
		beand.setValue(12d);
		Thread.sleep(1000);
		beand.setValue(1d);
		Thread.sleep(100);
		beand.setValue(200d);
		Thread.sleep(1000);
	}

	
	/**
	 * Test the various versions of the getValue function
	 * @throws CAException
	 * @throws InterruptedException 
	 * @throws ChannelException 
	 * @throws TimeoutException 
	 * @throws ExecutionException 
	 */
	@Test
	public void testGetValue() throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {
		// Test if array and getValue(int size) is called
		ChannelBean<double[]> bean = factory.createChannelBean(double[].class, TestChannels.CHARACTER_WAVEFORM, false);
		bean.getValue(); // Get first value of the array

		// Test if scalar and getValue(int size) is called
		ChannelBean<Double> beand = factory.createChannelBean(Double.class, TestChannels.BINARY_IN, false);
		beand.getValue();

		// Test how ChannelBean does behave is Scaler attached to waveform
		ChannelBean<Double> beandd = factory.createChannelBean(Double.class, TestChannels.DOUBLE_WAVEFORM, false);
		beandd.getValue();

		// Test get on a MBBI record
		ChannelBean<String> beans = factory.createChannelBean(String.class, TestChannels.MBBI, true);
		beans.getValue();
	}
	
	/**
	 * Test the various versions of the getValue function
	 * @throws CAException
	 * @throws InterruptedException 
	 * @throws ChannelException 
	 * @throws TimeoutException 
	 * @throws ExecutionException 
	 */
	@Test
	public void testGetValueRetries() throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {
		

		// Test if scalar and getValue(int size) is called
		ChannelBean<Double> beand = factory.createChannelBean(Double.class, TestChannels.BINARY_IN, false);
		Thread.sleep(5000);
		
		// TODO NEED TO MANUALLY REBOOT IOC
		
		logger.info("Value: "+beand.getValue());

	}
	
	
	/**
	 * Test the various versions of the setValue function
	 * @throws CAException
	 * @throws InterruptedException 
	 * @throws ChannelException 
	 * @throws TimeoutException 
	 * @throws ExecutionException 
	 */
	@Test
	public void testSetValue() throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {
		Double value = 1d;
		// Test if scalar and getValue(int size) is called
		ChannelBean<Double> beand = factory.createChannelBean(Double.class, TestChannels.BINARY_IN, false);
		beand.setValue(value); // Wait forever

		// Test how ChannelBean does behave is Scaler attached to waveform
		ChannelBean<Double> beandd = factory.createChannelBean(Double.class, TestChannels.BINARY_IN, false);
		Double v = beandd.getValue();
		
		if(!v.equals(value)){
			fail("Set value does not equal retrieved value");
		}
		
		// TODO need to add a test to test timeout, ... (therefore we need other record that is blocking some time)
	}
	
	
	/**
	 * Test waitForValue function
	 * @throws CAException
	 * @throws InterruptedException 
	 * @throws ChannelException 
	 * @throws TimeoutException 
	 * @throws ExecutionException 
	 */
	@Test
	public void testWaitForValue() throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {
		// Test if scalar and getValue(int size) is called
		ChannelBean<Integer> beand = factory.createChannelBean(Integer.class, TestChannels.BINARY_IN, false);
		beand.setValue(1);
		
		final ChannelBean<Integer> beanset = factory.createChannelBean(Integer.class, TestChannels.BINARY_IN, false);
		
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(500);
					beanset.setValue(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
		t.start();
		
		beand.waitForValue(0).get(2000L, TimeUnit.MILLISECONDS);
		
		
		// TODO Test if channel is already on the given value (measure time)
	}
	
	
	@Test
	public void testWaitForValueRetry() throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {
		final Integer testvalue = 0;
		
		// Test if scalar and getValue(int size) is called
		ChannelBean<Integer> beand = factory.createChannelBean(Integer.class, TestChannels.BINARY_IN, false);
		beand.setValue(testvalue+1);
		
		final ChannelBean<Integer> beanset = factory.createChannelBean(Integer.class, TestChannels.BINARY_IN, false);
		
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(5000);
					beanset.setValue(testvalue);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
		t.start();
		beand.setWaitRetryPeriod(1000L);
		Integer v = beand.waitForValueRetry(0).get(6000L, TimeUnit.MILLISECONDS);

		assertTrue("Channel not reached value "+testvalue, v==testvalue);
		
		
		// TODO Test if channel is already on the given value (measure time)
	}
	
	
	
	/**
	 * Test waitForValue function
	 * @throws CAException
	 * @throws InterruptedException 
	 * @throws ChannelException 
	 * @throws TimeoutException 
	 * @throws ExecutionException 
	 */
	@Test( expected=TimeoutException.class )
	public void testWaitForValueTimeout() throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {
		// Test if scalar and getValue(int size) is called
		ChannelBean<Integer> beand = factory.createChannelBean(Integer.class, TestChannels.BINARY_IN, false);
		beand.setValue(1);
		
		final ChannelBean<Integer> beanset = factory.createChannelBean(Integer.class, TestChannels.BINARY_IN, false);
		
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(1500);
					beanset.setValue(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
		t.start();
		
		// Wait for the channel to get to 0 using the default wait timeout
		beand.waitForValue(0).get(1, TimeUnit.MILLISECONDS); // Need to throw an TimeoutException
	}
	
	/**
	 * Test waitForValue function passing a comparator. The comparator will never return 0 so 
	 * an CAException indicating a wait timeout is expected.
	 * 
	 * @throws CAException
	 * @throws InterruptedException 
	 * @throws ChannelException 
	 * @throws TimeoutException 
	 * @throws ExecutionException 
	 */
	@Test(expected=TimeoutException.class)
	public void testWaitForValueComparator() throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {
		// Test if scalar and getValue(int size) is called
		ChannelBean<Integer> beand = factory.createChannelBean(Integer.class, TestChannels.BINARY_IN, false);
		beand.setValue(1);
		
		final ChannelBean<Integer> beanset = factory.createChannelBean(Integer.class, TestChannels.BINARY_IN, false);
		
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
					beanset.setValue(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
		t.start();
		
		Comparator<Integer> c = new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				if(o1<o2){
					return 0;
				}
				else{
					return -1;
				}
			}
		};
		beand.waitForValue(0, c).get(2000L, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * Test waitForValue function passing a comparator.
	 * 
	 * @throws CAException
	 * @throws InterruptedException 
	 * @throws ChannelException 
	 * @throws TimeoutException 
	 * @throws ExecutionException 
	 */
	@Test
	public void testWaitForValueComparatorTwo() throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {
		// Test if scalar and getValue(int size) is called
		ChannelBean<Integer> beand = factory.createChannelBean(Integer.class, TestChannels.BINARY_IN, false);
		beand.setValue(1);
		
		final ChannelBean<Integer> beanset = factory.createChannelBean(Integer.class, TestChannels.BINARY_IN, false);
		
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
					beanset.setValue(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
		t.start();
		
		Comparator<Integer> c = new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				if(o1!=o2){
					return 0;
				}
				else{
					return -1;
				}
			}
		};
		long start = System.currentTimeMillis();
		beand.waitForValue(1, c).get(2000L, TimeUnit.MILLISECONDS); // Wait until channel is not 1
		long end = System.currentTimeMillis();
		
		logger.info("Elapsed time: "+(end-start));
	}
	
	
	/**
	 * Test waitForValue function
	 * @throws CAException
	 * @throws InterruptedException 
	 * @throws ChannelException 
	 * @throws TimeoutException 
	 * @throws ExecutionException 
	 */
	@Test
	public void testWaitForValueNoTimeout() throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {
		// Test if scalar and getValue(int size) is called
		ChannelBean<Integer> beand = factory.createChannelBean(Integer.class, TestChannels.BINARY_IN, false);
		beand.setValue(1);
		
		final ChannelBean<Integer> beanset = factory.createChannelBean(Integer.class, TestChannels.BINARY_IN, false);
		
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
					beanset.setValue(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
		t.start();
		
		// Wait forever
		beand.waitForValue(0,null);
		
		
		// TODO Test if channel is already on the given value (measure time)
	}
	
	
	/**
	 * Test property change support if connection status is modified
	 * @throws InterruptedException
	 * @throws CAException
	 * @throws ChannelException 
	 * @throws TimeoutException 
	 * @throws ExecutionException 
	 */
	@Test
	public void testConnectionListener() throws InterruptedException, CAException, TimeoutException, ChannelException, ExecutionException {

		ChannelBean<Double> bean = factory.createChannelBean(Double.class, TestChannels.BINARY_IN, false);

		logger.info("Bean connected: " + bean.isConnected());
		
		bean.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("connected")) {
					logger.fine("Connection status changed: " + evt.getNewValue());
				}
			}
		});
		logger.info("Bean connected: " + bean.isConnected());
	}
	
	/**
	 * Test property change support for arrays
	 * @throws CAException
	 * @throws InterruptedException
	 */
	private double[] valueFromListener = null;
	@Test
	public void testPropertyChangeSupportArray() throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {
		// Test if array and getValue(int size) is called
		ChannelBean<double[]> bean = factory.createChannelBean(double[].class, TestChannels.DOUBLE_WAVEFORM, true);
		
		valueFromListener = null;
		bean.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				// logger.info("Property changed");
				valueFromListener = (double[]) evt.getNewValue();
			}
		});

		bean.setValue(new double[]{5,6,7,8,9});
		
		// Give the Listener some time to react on the monitor event
		Thread.sleep(1000);

		if(valueFromListener==null || valueFromListener[0]!= 5d){
			Assert.fail("The PropertyChangeListener has not return the correct value");
		}
	}


	private int mcount = 0;
	@Test
	public void testTimestampMonitor() throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {
		// Test if array and getValue(int size) is called
		ChannelBean<Double> c = factory.createChannelBean(Double.class, TestChannels.BINARY_IN, false);
		ChannelBean<DoubleTimestamp> bean = factory.createChannelBean(DoubleTimestamp.class, TestChannels.BINARY_IN, true);
		bean.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				DoubleTimestamp v = (DoubleTimestamp) evt.getNewValue();
				System.out.printf("%f %s %d\n", v.getValue(), v.getTimestamp(), v.getNanosecondOffset());
				mcount++;
			}
		});
		
		c.setValue(10d);
//		Thread.sleep(100); // monitor should still fire!
		c.setValue(3.69);
		Thread.sleep(500);
		c.setValue(6.1);
		Thread.sleep(500);
		c.setValue(45.9);
		
		Thread.sleep(500);
		
		c.destroy();
		bean.destroy();
		
		if(mcount != 4){ // 5 because while connecting the listener gets fired for the actual value
			Assert.fail("Not all monitors fired correctly");
		}
	}
	
	
	@Test(expected=IllegalStateException.class)
	public void testDestruction() throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {
		final ChannelBean<Double> b = factory.createChannelBean(Double.class, TestChannels.BINARY_IN, false);
		factory.getChannelFactory().destroyContext();
		b.destroy(); // Expect an illegal state exception here as the channel is already closed!
	}
	
	@Test
	public void testSetChannel() throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {
		ChannelBean<Double> bean = factory.createChannelBean(Double.class, TestChannels.BINARY_OUT, true);
		for(int i=0;i<100;i++){
			logger.info("Set value [iteration: "+i+"]");
			bean.setValue(2.0);
			Thread.sleep(100);
		}
	}
	
	/**
	 * Test to check how a wait can be aborted on request.
	 * 
	 * @throws CAException
	 * @throws InterruptedException
	 * @throws ChannelException 
	 * @throws TimeoutException 
	 * @throws ExecutionException 
	 */
	@Test
	public void testWaitForValueAbort() throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {
		// Test if scalar and getValue(int size) is called
		final ChannelBean<Double> beand = factory.createChannelBean(Double.class, TestChannels.ANALOG_OUT, true);
		beand.setValue(0.0);

		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					for(double i=0;i<=1.1;i=i+0.1){
						logger.info("Set value: "+i);
						beand.setValue(i);
						Thread.sleep(500);
					}
					
				
					// Abort wait ...

					// TODO implement
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
		t.start();
		
		beand.waitForValue(1.0, new Comparator<Double>() {
			@Override
			public int compare(Double setvalue, Double value) {
				if(value>(setvalue-0.1) && value<=(setvalue+0.1)){
					return 0;
				}
				return 1;
			}
		}).get(20000L, TimeUnit.MILLISECONDS);
		
		
		// TODO Test if channel is already on the given value (measure time)
	}
}
