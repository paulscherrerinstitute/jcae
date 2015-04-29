package ch.psi.jcae;

import static org.junit.Assert.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import gov.aps.jca.CAException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import ch.psi.jcae.impl.DefaultChannelService;
import ch.psi.jcae.impl.type.ByteArrayString;
import ch.psi.jcae.impl.type.DoubleArrayTimestamp;
import ch.psi.jcae.impl.type.DoubleTimestamp;
import ch.psi.jcae.util.ComparatorDouble;

/**
 * JUnit test case for testing the functionality of a <code>Channel</code>
 * 
 * IMPORTANT NOTE:
 * The JCAE library is capable in setting channels faster that the "camon" command can handle. Therefor don't be confused
 * that camon is not showing a value change. To have camon catching up you have to insert <code>Thread.sleep()</code> statements
 */
public class ChannelTest {
	
	private static Logger logger = Logger.getLogger(ChannelTest.class.getName());
	
	private ChannelService cservice;
	private static TestChannels testChannels;
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		testChannels = new TestChannels();
		testChannels.start();
	}
	
	@AfterClass
	public static void tearDownClass() throws Exception {
		testChannels.stop();
	}
	
	@Before
	public void setUp() throws Exception {
		cservice = new DefaultChannelService();
	}
	
	@After
	public void tearDown(){
		cservice.destroy();
	}

	/**
	 * Test to continuously set and get a string waveform
	 * @throws Exception 
	 */
	@Test
	public void testSetGetValueStringWaveform() throws Exception {
		try{
		Channel<String[]> bean = cservice.createChannel(new ChannelDescriptor<String[]>(String[].class, TestChannels.STRING_WAVEFORM));
		
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
		Channel<byte[]> bean = cservice.createChannel(new ChannelDescriptor<byte[]>(byte[].class, TestChannels.CHARACTER_WAVEFORM));

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
			fail("getValue() does not return all the waveform elements");
		}

		// Check whether converted String value is the same than the one that was set 
		String svalue = new String(value);
		svalue = svalue.trim();
		if(! setvalue.equals(svalue)){
			fail("The returned channel value does not match to the one set");
		}
		logger.fine("String returned: "+svalue+" Size: "+svalue.length());
		
		
		// Test whether only a substring can be retrieved from the waveform channel
		value = Arrays.copyOf(bean.getValue(),4);// only get the 4 first characters
		svalue = new String(value);
		svalue = svalue.trim();
		if(! "some".equals(svalue)){
			fail("The returned sub channel value does not match to the one set");
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
	@Ignore
	@Test
	public void testGetHostname() throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {
		Channel<String> bean = cservice.createChannel(new ChannelDescriptor<String>(String.class, TestChannels.BINARY_IN));
		logger.fine("Size of the Channel: "+bean.getSource());
		
		// TODO determine localhosts hostname, e.g. apple.psi.ch
		assertEquals("localhost", bean.getSource());
//		if(! .equals("localhost")){
//			fail("Ioc name returned does not match the expected ioc name");
//		}
	}
	
	
	@Test
	public void testGetValueComplexType() throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {
		// Test if array and getValue(int size) is called
		Channel<DoubleTimestamp> bean = cservice.createChannel(new ChannelDescriptor<DoubleTimestamp>(DoubleTimestamp.class, TestChannels.BINARY_IN, true));
		DoubleTimestamp v = bean.getValue();
		System.out.printf("%f %s offset: %d\n",v.getValue(), v.getTimestamp(), v.getNanosecondOffset());
		bean.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if(evt.getPropertyName().equals("value")){
					DoubleTimestamp t = ((DoubleTimestamp) evt.getNewValue());
					System.out.printf("event: %f %s %d\n", t.getValue(), t.getTimestamp(), t.getNanosecondOffset());
				}
			}
		});

		Channel<Double> beand = cservice.createChannel(new ChannelDescriptor<Double>(Double.class, TestChannels.BINARY_IN));
		beand.setValue(12d);
		Thread.sleep(1000);
		beand.setValue(1d);
		Thread.sleep(100);
		beand.setValue(200d);
		Thread.sleep(1000);
	}

	@Test
	public void testGetValueWAVE() throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {
//		// Test if array and getValue(int size) is called
//		Channel<double[]> bean = cservice.createChannel(new ChannelDescriptor<double[]>(double[].class, "TRFCB-RLLE-RIOC:MASTER-MSICNT"));
//		System.out.println("Value: " + bean.getValue()); // Get first value of the array
//		System.out.println("Value: " + bean.getValue().length); // Get first value of the array
//		System.out.println("Value: " + bean.getValue()[0]); // Get first value of the array
		
		// Test if array and getValue(int size) is called
//		Channel<Integer> bean = cservice.createChannel(new ChannelDescriptor<Integer>(Integer.class, "TRFCB-RLLE-RIOC:MASTER-MSICNT"));
//		System.out.println("Value: " + bean.getValue()); // Get first value of the array
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
		Channel<double[]> bean = cservice.createChannel(new ChannelDescriptor<double[]>(double[].class, TestChannels.CHARACTER_WAVEFORM));
		bean.getValue(); // Get first value of the array

		// Test if scalar and getValue(int size) is called
		Channel<Double> beand = cservice.createChannel(new ChannelDescriptor<Double>(Double.class, TestChannels.BINARY_IN));
		beand.getValue();

		// Test how ChannelBean does behave is Scaler attached to waveform
		Channel<Double> beandd = cservice.createChannel(new ChannelDescriptor<Double>(Double.class, TestChannels.DOUBLE_WAVEFORM));
		beandd.getValue();
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
		Channel<Double> beand = cservice.createChannel(new ChannelDescriptor<Double>(Double.class, TestChannels.BINARY_IN));
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
		
		Channel<Double> channel1 = cservice.createChannel(new ChannelDescriptor<Double>(Double.class, TestChannels.ANALOG_OUT));
		// Use of a second channel to ensure that the value is not somehow cached in the Channel object itself
		Channel<Double> channel2 = cservice.createChannel(new ChannelDescriptor<Double>(Double.class, TestChannels.ANALOG_OUT));
		
		Double ovalue = channel1.getValue();
		for(Double value=1.1;value<10;value++){
			channel1.setValue(value); // Wait forever
			Double v = channel2.getValue();
			
			if(!v.equals(value)){
				fail(String.format("Set value [%s] does not equal retrieved value [%s]", value, v));
			}
		}
		
		// Reset value to old value
		channel1.setValue((ovalue.intValue()+1.0)%100); // Have this to ensure that if someone is doing a camon that things change (see note header)
	}
	
	
	@Test
	public void testSetValueAsync() throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {
		
		Channel<Double> c = cservice.createChannel(new ChannelDescriptor<Double>(Double.class, TestChannels.ANALOG_OUT));
		c.setValue(1.0);
		System.out.println("done");
		c.setValueAsync(5.0);
		System.out.println("done");
	}
	
//	@Test
//	public void testT() throws ChannelException, InterruptedException, TimeoutException, ExecutionException{
//		Channel<Double> ch = cservice.createChannel(new ChannelDescriptor<Double>(Double.class, "MTEST-HW3:MOT1", false));
//		Channel<Double> ch2 = cservice.createChannel(new ChannelDescriptor<Double>(Double.class, "MTEST-HW3:MOT1.RBV", false));
//		for(double i=0;i<=5.0; i=i+1){
//			System.out.println("SET "+i);
//			ch.setValueNoWait(i);
//			Future<Double> f = ch2.waitForValueAsync(i, new ComparatorDouble(0.01));
//			System.out.println("VALUE "+ch2.getValue());
//			System.out.println("NEW "+f.get());
////			System.out.println("done");
//		}
//	}
	
//	@Test
//	public void testT2() throws ChannelException, InterruptedException, TimeoutException, ExecutionException{
//		Channel<Double> ch1 = cservice.createChannel(new ChannelDescriptor<Double>(Double.class, "MTEST-HW3:MOT1", false));
//		Channel<Double> ch2 = cservice.createChannel(new ChannelDescriptor<Double>(Double.class, "MTEST-HW3:MOT2", false));
//		Channel<Double> ch1r = cservice.createChannel(new ChannelDescriptor<Double>(Double.class, "MTEST-HW3:MOT1.RBV", false));
//		for(double i=0;i<=5.0; i=i+1){
//			System.out.println("SET[1] "+i);
//			Future<Double> f = ch1.setValueAsync(i);
//			System.out.println("SET[2] "+i);
//			ch2.setValueAsync(i);
////			ch.setValueAsync(i);
////			Future<Double> f = ch2.waitForValueAsync(i, new ComparatorDouble(0.01));
//			System.out.println("VALUE "+ch1r.getValue());
////			System.out.println("NEW "+f.get());
//			f.get();
////			System.out.println("done");
//		}
//	}
	
	
	@Test
	public void testSetValueString() throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {
		Channel<String> channel1 = cservice.createChannel(new ChannelDescriptor<String>(String.class, TestChannels.STRING_OUT1));
		// Use of a second channel to ensure that the value is not somehow cached in the Channel object itself
		Channel<String> channel2 = cservice.createChannel(new ChannelDescriptor<String>(String.class, TestChannels.STRING_OUT1));
		
		for(Double value=1.1;value<10;value++){
			channel1.setValue(value+""); // Wait forever
			String v = channel2.getValue();
			
			if(!v.equals(value+"")){
				fail(String.format("Set value [%s] does not equal retrieved value [%s]", value, v));
			}
		}
		
		// Reset value to old value
		channel1.setValue(System.currentTimeMillis()+""); // Have this to ensure that if someone is doing a camon that things change (see note header)
	}
	
	
	boolean testflag = false;
	@Test
	public void testCompositeChannel() throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {
		// Use of a second channel to ensure that the value is not somehow cached in the Channel object itself
		Channel<String> setChannel = cservice.createChannel(new ChannelDescriptor<String>(String.class, TestChannels.STRING_OUT1));
		Channel<String> readbackChannel = cservice.createChannel(new ChannelDescriptor<String>(String.class, TestChannels.STRING_OUT2));
		
		
		Channel<String> channel = cservice.createChannel(new CompositeChannelDescriptor<String>(String.class, TestChannels.STRING_OUT1, TestChannels.STRING_OUT2));
		
		String valSet = "";
		String valReadback = "result";
		
		setChannel.setValue(valSet);
		readbackChannel.setValue(valReadback);
		
		Thread.sleep(100);
		String val = "testvalue";
		channel.setValue(val);
		
		assertEquals(setChannel.getValue(), val);
		assertEquals(readbackChannel.getValue(), valReadback); // Readback must not have been changed

		Thread.sleep(100);
		readbackChannel.setValue(val);
		
		assertEquals(val, readbackChannel.getValue());
		assertEquals(val, setChannel.getValue());
		
		
		// Test whether monitor is working
		testflag = false;
		PropertyChangeListener l = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				testflag = true;
			}
		};
		
		channel.addPropertyChangeListener(l);
		
		readbackChannel.setValue("result");
		Thread.sleep(10); // sleep 10 milliseconds to ensure that listener was fired
		assertTrue(testflag);
		
	}
	
	
	@Test
	public void testSetValueMonitored() throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {
		
		Channel<Double> channel1 = cservice.createChannel(new ChannelDescriptor<Double>(Double.class, TestChannels.ANALOG_OUT, true));
		// Use of a second channel to ensure that the value is not somehow cached in the Channel object itself
		Channel<Double> channel2 = cservice.createChannel(new ChannelDescriptor<Double>(Double.class, TestChannels.ANALOG_OUT, true));
		
		Double ovalue = channel1.getValue();
		for(Double value=1.1;value<10;value++){
			channel1.setValue(value); // Wait forever
			// Sometimes it needs some time to have the monitor catch up. Therefore we introduce a safty wait of 5 millisecond
			Thread.sleep(5);
			Double v = channel2.getValue();
			
			assertEquals(value, v);
		}
		
		// Reset value to old value
		channel1.setValue((ovalue.intValue()+1.0)%100); // Have this to ensure that if someone is doing a camon that things change (see note header)
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
		Channel<Integer> beand = cservice.createChannel(new ChannelDescriptor<Integer>(Integer.class, TestChannels.BINARY_IN));
		beand.setValue(1);
		
		final Channel<Integer> beanset = cservice.createChannel(new ChannelDescriptor<Integer>(Integer.class, TestChannels.BINARY_IN));
		
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
		
		beand.waitForValueAsync(0).get(2000L, TimeUnit.MILLISECONDS);
		
		
		// TODO Test if channel is already on the given value (measure time)
	}
	
	
	@Test
	public void testWaitForValueRetry() throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {
		final Integer testvalue = 0;
		
		// Test if scalar and getValue(int size) is called
		Channel<Integer> beand = cservice.createChannel(new ChannelDescriptor<Integer>(Integer.class, TestChannels.BINARY_IN));
		beand.setValue(testvalue+1);
		
		final Channel<Integer> beanset = cservice.createChannel(new ChannelDescriptor<Integer>(Integer.class, TestChannels.BINARY_IN));
		
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
		Integer v = beand.waitForValueAsync(0, 1000L).get(6000L, TimeUnit.MILLISECONDS);

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
		Channel<Integer> beand = cservice.createChannel(new ChannelDescriptor<Integer>(Integer.class, TestChannels.BINARY_IN));
		beand.setValue(1);
		
		final Channel<Integer> beanset = cservice.createChannel(new ChannelDescriptor<Integer>(Integer.class, TestChannels.BINARY_IN));
		
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(1500);
					beanset.setValue(0);
				} catch (InterruptedException e) {
					// Ignore this because 
				} catch( ExecutionException e){
					e.printStackTrace();
				} catch( ChannelException e){
					e.printStackTrace();
				}
				
			}
		});
		t.start();
		
		try{
		// Wait for the channel to get to 0 using the default wait timeout
		beand.waitForValueAsync(0).get(1, TimeUnit.MILLISECONDS); // Need to throw an TimeoutException
		}
		finally{
			t.interrupt(); // terminate thread
		}
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
		Channel<Integer> beand = cservice.createChannel(new ChannelDescriptor<Integer>(Integer.class, TestChannels.BINARY_IN));
		beand.setValue(1);
		
		final Channel<Integer> beanset = cservice.createChannel(new ChannelDescriptor<Integer>(Integer.class, TestChannels.BINARY_IN));
		
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
		beand.waitForValueAsync(0, c).get(2000L, TimeUnit.MILLISECONDS);
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
		Channel<Integer> beand = cservice.createChannel(new ChannelDescriptor<Integer>(Integer.class, TestChannels.BINARY_IN));
		beand.setValue(1);
		
		final Channel<Integer> beanset = cservice.createChannel(new ChannelDescriptor<Integer>(Integer.class, TestChannels.BINARY_IN));
		
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
		beand.waitForValueAsync(1, c).get(2000L, TimeUnit.MILLISECONDS); // Wait until channel is not 1
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
		Channel<Integer> beand = cservice.createChannel(new ChannelDescriptor<Integer>(Integer.class, TestChannels.BINARY_IN));
		beand.setValue(1);
		
		final Channel<Integer> beanset = cservice.createChannel(new ChannelDescriptor<Integer>(Integer.class, TestChannels.BINARY_IN));
		
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
					beanset.setValue(0);
				} catch (InterruptedException e) {
					// Ignore thread is terminated
				} catch (ExecutionException e) {
					e.printStackTrace();
				} catch (ChannelException e) {
					e.printStackTrace();
				}
				
			}
		});
		t.start();
		
		try{
			// Wait forever
			beand.waitForValue(0);
		}
		finally{
			t.interrupt();
		}
		
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

		Channel<Double> bean = cservice.createChannel(new ChannelDescriptor<Double>(Double.class, TestChannels.BINARY_IN));

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
		Channel<double[]> bean = cservice.createChannel(new ChannelDescriptor<double[]>(double[].class, TestChannels.DOUBLE_WAVEFORM, true));
		
		valueFromListener = null;
		bean.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				// logger.info("Property changed");
				if(evt.getPropertyName().equals("value")){
					valueFromListener = (double[]) evt.getNewValue();
				}
			}
		});

		bean.setValue(new double[]{5,6,7,8,9});
		
		// Give the Listener some time to react on the monitor event
		Thread.sleep(1000);

		if(valueFromListener==null || valueFromListener[0]!= 5d){
			fail("The PropertyChangeListener has not return the correct value");
		}
	}
	
	@Test
	public void testArrayTimestampSize() throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {
		Channel<DoubleArrayTimestamp> bean1 = cservice.createChannel(new ChannelDescriptor<DoubleArrayTimestamp>(DoubleArrayTimestamp.class, TestChannels.DOUBLE_WAVEFORM, true));
		assertTrue(bean1.getSize() > 1);
		bean1.destroy();
		
		Channel<ByteArrayString> bean2 = cservice.createChannel(new ChannelDescriptor<ByteArrayString>(ByteArrayString.class, TestChannels.CHARACTER_WAVEFORM));
		assertTrue(bean2.getSize() > 1);
		bean2.destroy();
	}

	private int mcount = 0;
	@Test
	public void testTimestampMonitor() throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {
		// Test if array and getValue(int size) is called
		Channel<Double> c = cservice.createChannel(new ChannelDescriptor<Double>(Double.class, TestChannels.BINARY_IN));
		Channel<DoubleTimestamp> bean = cservice.createChannel(new ChannelDescriptor<DoubleTimestamp>(DoubleTimestamp.class, TestChannels.BINARY_IN, true));
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
		
		if(!(mcount == 4 | mcount == 5)){ // 5 because while connecting the listener gets fired for the actual value
			fail("Not all monitors fired correctly");
		}
//		assertEquals("Not all monitors fired correctly", 4, mcount);
	}
	
	
	@Test(expected=IllegalStateException.class)
	public void testDestruction() throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {
		final Channel<Double> b = cservice.createChannel(new ChannelDescriptor<Double>(Double.class, TestChannels.BINARY_IN));
		cservice.destroy();
		b.destroy(); // Expect an illegal state exception here as the channel is already closed!
	}
	
	@Test
	public void testSetChannel() throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {
		Channel<Double> bean = cservice.createChannel(new ChannelDescriptor<Double>(Double.class, TestChannels.BINARY_OUT, true));
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
	public void testWaitForValueAbort() throws InterruptedException, TimeoutException, ChannelException, CAException, ExecutionException  {
		// Test if scalar and getValue(int size) is called
		final Channel<Double> beand = cservice.createChannel(new ChannelDescriptor<Double>(Double.class, TestChannels.ANALOG_OUT, true));
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
				} catch (InterruptedException e) {
					// Ignore thread terminated early
				} catch (ExecutionException e) {
					e.printStackTrace();
				} catch (ChannelException e) {
					e.printStackTrace();
				}
				
			}
		});
		t.start();
		
		try{
			beand.waitForValueAsync(1.0, new Comparator<Double>() {
				@Override
				public int compare(Double setvalue, Double value) {
					if(value>(setvalue-0.1) && value<=(setvalue+0.1)){
						return 0;
					}
					return 1;
				}
			}).get(20000L, TimeUnit.MILLISECONDS);
		}
		finally{
			t.interrupt();
		}
		
		// TODO Test if channel is already on the given value (measure time)
	}
}
