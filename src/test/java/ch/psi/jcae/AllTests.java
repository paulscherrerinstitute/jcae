package ch.psi.jcae;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses( { 	ChannelServiceAnnotatedObjectTest.class, 
					ChannelServiceTest.class, 
					ChannelTest.class, 
					JCAChannelFactoryTest.class,
					JcaePropertiesTest.class })

public class AllTests {
}
