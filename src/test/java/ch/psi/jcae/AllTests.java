package ch.psi.jcae;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.psi.jcae.cas.ProcessVariableTest;

@RunWith(Suite.class)
@SuiteClasses( { 	ChannelServiceAnnotatedObjectTest.class, 
					ChannelServiceTest.class, 
					ChannelTest.class, 
					JCAChannelFactoryTest.class,
					JcaePropertiesTest.class,
					ProcessVariableTest.class })

public class AllTests {
}
