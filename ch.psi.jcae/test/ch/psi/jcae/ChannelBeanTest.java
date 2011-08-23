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
import java.util.Comparator;
import java.util.Date;
import java.util.logging.Logger;

import junit.framework.Assert;

import gov.aps.jca.CAException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import ch.psi.jcae.ChannelBean;
import ch.psi.jcae.ChannelBeanFactory;

/**
 * JUnit test case for testing the functionality of a <code>ChannelBean</code>
 * @author ebner
 *
 */
public class ChannelBeanTest {
	
	private static Logger logger = Logger.getLogger(ChannelBeanTest.class.getName());
	
	private static String channelPrefix = "MTEST-PC-JCAE:";
	private static String iocname = "psi-softioc.psi.ch";
	private ChannelBeanFactory factory;
	
	@Before
	public void setUp() throws Exception {
		factory = ChannelBeanFactory.getFactory();
	}

	/**
	 * Test to continuously set and get a string waveform
	 * @throws CAException
	 * @throws InterruptedException
	 */
	@Test
	public void testSetGetValueStringWaveform() throws CAException, InterruptedException {
		
		ChannelBean<String[]> bean = factory.createChannelBean(String[].class, channelPrefix+"SWAVE", false);

		// Set test string to waveform
		String[] value = {"one....................", "two....................", "three....................", "four....................", "five....................", "six....................", "seven....................", "eight....................", "nine....................", "ten...................."};
		String[] value2 = {"", "", "", "", "", "", "", ""};
		for(int i=0;i<1000;i++){
			// Alternate values to set
			if(i%2==0){
				bean.setValue(value);
			}
			else{
				bean.setValue(value2);
			}
			Thread.sleep(10);
			bean.getValue();
			
		}
		
	}
	
	
	/**
	 * Test to verify whether a char waveform can be easily accessed and
	 * be converted to a String
	 * @throws CAException 
	 * @throws InterruptedException 
	 */
	@Test
	public void testGetValueWaveform() throws CAException, InterruptedException {
		ChannelBean<byte[]> bean = factory.createChannelBean(byte[].class, channelPrefix+"CWAVE", false);

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
		value = bean.getValue(4);// only get the 4 first characters
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
	 */
	@Test
	public void testGetHostname() throws CAException, InterruptedException {
		ChannelBean<String> bean = factory.createChannelBean(String.class, channelPrefix+"BI", false);
		logger.fine("Size of the Channel: "+bean.getHostname());
		if(! bean.getHostname().equals(iocname)){
			Assert.fail("Ioc name returned does not match the expected ioc name");
		}
	}

	
	/**
	 * Test the various versions of the getValue function
	 * @throws CAException
	 * @throws InterruptedException 
	 */
	@Test
	public void testGetValue() throws CAException, InterruptedException {
		// Test if array and getValue(int size) is called
		ChannelBean<double[]> bean = factory.createChannelBean(double[].class, channelPrefix+"CWAVE", false);
		bean.getValue(10); // Get a subarray
		bean.getValue(); // Get first value of the array

		// Test if scalar and getValue(int size) is called
		ChannelBean<Double> beand = factory.createChannelBean(Double.class, channelPrefix+"BI", false);
		beand.getValue(1);

		// Test how ChannelBean does behave is Scaler attached to waveform
		ChannelBean<Double> beandd = factory.createChannelBean(Double.class, channelPrefix+"DWAVE", false);
		beandd.getValue();

		// Test get on a MBBI record
		ChannelBean<String> beans = factory.createChannelBean(String.class, channelPrefix+"MBBI", true);
		beans.getValue();
	}
	
	/**
	 * Test the various versions of the getValue function
	 * @throws CAException
	 * @throws InterruptedException 
	 */
	@Test
	public void testGetValueRetries() throws CAException, InterruptedException {
		

		// Test if scalar and getValue(int size) is called
		ChannelBean<Double> beand = factory.createChannelBean(Double.class, channelPrefix+"BI", false);
		Thread.sleep(5000);
		
		// TODO NEED TO MANUALLY REBOOT IOC
		
		logger.info("Value: "+beand.getValue());

	}
	
	
	/**
	 * Test the various versions of the setValue function
	 * @throws CAException
	 * @throws InterruptedException 
	 */
	@Test
	public void testSetValue() throws CAException, InterruptedException {
		Double value = 1d;
		// Test if scalar and getValue(int size) is called
		ChannelBean<Double> beand = factory.createChannelBean(Double.class, channelPrefix+"BI", false);
		beand.setValue(value, 0); // Wait forever

		// Test how ChannelBean does behave is Scaler attached to waveform
		ChannelBean<Double> beandd = factory.createChannelBean(Double.class, channelPrefix+"BI", false);
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
	 */
	@Test
	public void testWaitForValue() throws CAException, InterruptedException {
		// Test if scalar and getValue(int size) is called
		ChannelBean<Integer> beand = factory.createChannelBean(Integer.class, channelPrefix+"BI", false);
		beand.setValue(1);
		
		final ChannelBean<Integer> beanset = factory.createChannelBean(Integer.class, channelPrefix+"BI", false);
		
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
		
		beand.waitForValue(0, 2000L);
		
		
		// TODO Test if channel is already on the given value (measure time)
	}
	
	
	/**
	 * Test waitForValue function
	 * @throws CAException
	 * @throws InterruptedException 
	 */
	@Test( expected=CAException.class )
	public void testWaitForValueTimeout() throws CAException, InterruptedException {
		// Test if scalar and getValue(int size) is called
		ChannelBean<Integer> beand = factory.createChannelBean(Integer.class, channelPrefix+"BI", false);
		beand.setValue(1);
		beand.setWaitTimeout(1000); // For testing purpose set wait timeout to 1000 milliseconds
		
		final ChannelBean<Integer> beanset = factory.createChannelBean(Integer.class, channelPrefix+"BI", false);
		
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
		beand.waitForValue(0); // Need to throw an Timeout CAException
	}
	
	/**
	 * Test waitForValue function
	 * @throws CAException
	 * @throws InterruptedException 
	 */
	@Test
	@Ignore
	public void testUseCorrectProperties() throws CAException, InterruptedException {
		// Test if scalar and getValue(int size) is called
		ChannelBean<Integer> beand = factory.createChannelBean(Integer.class, channelPrefix+"BI", false);

		if(beand.getWaitTimeout() != 2000){
			fail("The wait timeout for a ChannelBean found is not the value specified in jcae.properties");
		}
	}
	
	/**
	 * Test waitForValue function passing a comparator. The comparator will never return 0 so 
	 * an CAException indicating a wait timeout is expected.
	 * 
	 * @throws CAException
	 * @throws InterruptedException 
	 */
	@Test(expected=CAException.class)
	public void testWaitForValueComparator() throws CAException, InterruptedException {
		// Test if scalar and getValue(int size) is called
		ChannelBean<Integer> beand = factory.createChannelBean(Integer.class, channelPrefix+"BI", false);
		beand.setValue(1);
		
		final ChannelBean<Integer> beanset = factory.createChannelBean(Integer.class, channelPrefix+"BI", false);
		
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
		beand.waitForValue(0, c, 2000L);
	}
	
	/**
	 * Test waitForValue function passing a comparator.
	 * 
	 * @throws CAException
	 * @throws InterruptedException 
	 */
	@Test
	public void testWaitForValueComparatorTwo() throws CAException, InterruptedException {
		// Test if scalar and getValue(int size) is called
		ChannelBean<Integer> beand = factory.createChannelBean(Integer.class, channelPrefix+"BI", false);
		beand.setValue(1);
		
		final ChannelBean<Integer> beanset = factory.createChannelBean(Integer.class, channelPrefix+"BI", false);
		
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
		beand.waitForValue(1, c, 2000L); // Wait until channel is not 1
		long end = System.currentTimeMillis();
		
		logger.info("Elapsed time: "+(end-start));
	}
	
	
	/**
	 * Test waitForValue function
	 * @throws CAException
	 * @throws InterruptedException 
	 */
	@Test
	public void testWaitForValueNoTimeout() throws CAException, InterruptedException {
		// Test if scalar and getValue(int size) is called
		ChannelBean<Integer> beand = factory.createChannelBean(Integer.class, channelPrefix+"BI", false);
		beand.setValue(1);
		
		final ChannelBean<Integer> beanset = factory.createChannelBean(Integer.class, channelPrefix+"BI", false);
		
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
	 */
	@Test
	public void testConnectionListener() throws InterruptedException, CAException {

		ChannelBean<Double> bean = factory.createChannelBean(Double.class, channelPrefix+"BI", false);

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
	public void testPropertyChangeSupportArray() throws CAException, InterruptedException {
		// Test if array and getValue(int size) is called
		ChannelBean<double[]> bean = factory.createChannelBean(double[].class, channelPrefix+"DWAVE", true);
		
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
	public void testTimestampMonitor() throws CAException, InterruptedException {
		// Test if array and getValue(int size) is called
		ChannelBean<Double> c = factory.createChannelBean(Double.class, channelPrefix+"BI", false);
		ChannelBean<Double> bean = factory.createChannelBean(Double.class, channelPrefix+"BI", true);
		MonitorListenerDoubleTimestamp l = new MonitorListenerDoubleTimestamp(){

			@Override
			public void valueChanged(Double value, Date timestamp, long nanosecondsOffset) {
				System.out.println(value+ " - "+timestamp+" ."+nanosecondsOffset);
				mcount++;
			}};
			
		bean.attachMonitor(l );
		
		c.setValue(10d);
//		Thread.sleep(100);
		c.setValue(3.69);
		Thread.sleep(500);
		c.setValue(6.1);
		Thread.sleep(500);
		c.setValue(45.9);
		
		Thread.sleep(500);
		
		c.destroy();
		bean.destroy();
		
		if(mcount != 5){ // 5 because while connecting the listener gets fired for the actual value
			Assert.fail("Not all monitors fired correctly");
		}
	}
	
	
	
	/**
	 * Test whether registering a function as monitor works
	 * @throws CAException
	 * @throws InterruptedException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */
	private boolean methodCalled = false;
	@Test
	public void testFunctionMonitorListener() throws CAException, InterruptedException, SecurityException, NoSuchMethodException {

		ChannelBean<String> bean = factory.createChannelBean(String.class, channelPrefix+"SOUT1", true);
		bean.addMonitorListener(this, this.getClass().getMethod("doit"));

		methodCalled=false;
		bean.setValue("hallihallo");
		
		// Ensure that the monitor has enough time to call the function
		Thread.sleep(1000);
		
		if(!methodCalled){
			Assert.fail("Registered method was not called");
		}
	}
	
	/**
	 * Test function to be called by a monitor. 
	 * @see #testFunctionMonitorListener()
	 */
	public void doit(){
		logger.fine("Method called ...");
		methodCalled=true;
	}
}
