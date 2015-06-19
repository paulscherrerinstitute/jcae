SOME TEST

# Overview

JCAE is an easy to use ChannelAccess library abstracting the complexity of the JCA and CAJ library and bringing ChannelAccess into the Java domain (i.e. use of Java types).

The package provides an easy to use ChannelAccess client as well as Server API (in package `ch.psi.jcae.cas`). 

Jcae can be used to easily interface Epics via ChannelAccess within Matlab. Details about this can be found [here](Matlab.md).

# Usage

## Prerequisites
### Dependencies
The easiest way to get jcae and all its dependencies is to use Gradle, Maven or some other dependeny management system.

For Maven add following dependency to your dependencies section:

```xml
<dependency>
        <groupId>ch.psi</groupId>
        <artifactId>jcae</artifactId>
        <version>version.to.use</version>
</dependency>
```

For Gradle use:

```groovy
compile name: 'ch.psi:jcae:<version to use>'
```

## Configuration
The JCA Extension library/classes obtain its configuration from the central `jcae.properties` file. 
_Note:_ In most cases no `jca.properties` file is needed! The `jcae.properties` file either need to reside inside 
the classpath of the application or need to be specified via the VM argument:

```
-Dch.psi.jcae.config.file=myjcae.properties
```

The `jcae.properties` file holds all configuration parameters needed by the factory classes that are provided within 
the library. To uniquely identify which class is using the property, the property name has fully qualified class 
name as prefix.

### ContextFactory

| Property | Value | Description |
| --- | --- | --- |
| ch.psi.jcae.ContextFactory.addressList | | Space separated list of IP addresses |
| ch.psi.jcae.ContextFactory.autoAddressList | true, false | Auto address list |
| ch.psi.jcae.ContextFactory.useShellVariables | true, false | Use settings set by the EPICS_CA_ADDR_LIST and EPICS_CA_AUTO_ADDR_LIST shell variable|
| ch.psi.jcae.ContextFactory.addLocalBroadcastInterfaces | true, false | Extend the address list with the local broadcast interfaces |
| ch.psi.jcae.ContextFactory.queuedEventDispatcher | true, false | Use queued event dispatcher |
| ch.psi.jcae.ContextFactory.maxArrayBytes | | Number of maximum bytes that are used to transfer an array |
| ch.psi.jcae.ContextFactory.serverPort | | Channel Access server port (if using a gateway this is usually 5062) |

### ChannelFactory

| Property | Value | Description |
| --- | --- | --- |
| ch.psi.jcae.ChannelFactory.timeout | 10000 | Timeout in milliseconds for creating a new channel |
| ch.psi.jcae.ChannelFactory.retries | 0 | Retries for connecting to a channel |

### ChannelBeanFactory

| Property | Value | Description |
| --- | --- | --- |
| ch.psi.jcae.ChannelBeanFactory.timeout | 10000 | Timeout for a request in milliseconds |
| ch.psi.jcae.ChannelBeanFactory.waitTimeout | - | Timeout in milliseconds for a wait operation (not specified = wait forever) |
| ch.psi.jcae.ChannelBeanFactory.waitRetryPeriod | - | While waiting for a value the period to exchange the monitor of the channel. This might avoid hanging if a monitor callback with a new value (we are waiting for) was lost on the network. While periodically restart the monitor we avoid this scenario. Ideally, if specified, this time is big but smaller than the waitTimeout. If this property is NOT specified only one monitor is started for the whole wait period (should be fine if everything in Channel access behaves as it should) |
| ch.psi.jcae.ChannelBeanFactory.retries | 0 | Retries for set/get operation (will not apply to waitForValue operation) |

## Destruction Context
The channel service uses a JCA Context for creating, managing and destructing channels. This context need to be destroyed at the end of every program. If the context is not destroyed, the Java Virtual Machine will not exit. Therefore the ChannelService instance need to be manually destroyed as follows:

```java
ChannelService service;
//...
service.destroy();
```

## Channel
`ch.psi.jcae.Channel` is the major abstraction provided by the jcae Library. It introduces an object oriented abstraction 
of an Epics channel and hides the complexity of the creation/usage/destruction of the channel. 
The value of the channel can be easily accessed and modified via get and set methods. A Channel can be 
created in two different modes. normal and monitor mode. In normal mode the value of the channel gets 
collected (over the network) each time the get method is called. In monitor mode a monitor is established 
for the channel that recognizes value changes and caches the value in a local variable. When the value of 
the channel is accessed via the get method the cached value is returned instead of getting it explicitly 
(over the network).

### Usage
The following section gives a short overview of the functionality of Channel and its usage. 
Examples are shown for the creation, usage and destruction.

#### Creation
A Channel is created via a ChannelService instance. Ideally there is only one instance of a ChannelService per JVM

```java
ChannelService cservice = new DefaultChannelService();
Channel<String> channel = cservice.createChannel(new ChannelDescriptor<String>(String.class, "MYCHANNEL:XYZ", true));
```

#### Get/Set Value

```java
//Get value
String value = channel.getValue();
// Get value explicitly over the network (If ChannelBean is created in monitor mode)
value = channel.getValue(true);
// Set value
channel.setValue("hello");
```

#### Wait for a specific value
Wait for a channel to reach exactly a specific value:

```java
// Wait without timeout
channel.waitForValue("world");

// Wait with timeout
channel.waitForValueAsync("world").get(10000L, TimeUnit.MILLISECONDS);
```

#### Wait for a channel to meet specified condition

```java
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
beand.waitForValue(1, c, 2000);
```

#### Destruction
As the Channel holds a Channel Access connection that need to be closed explicitly, 
the destroy() need to be called explicitly on that object. After calling the destroy() method 
of the channel object, the object must not be used any more!

```java
channel.destroy();
```

#### Property Change Support
One can register for Channel status changes via the standard JavaBean PropertyChangeSupport functionality. 
To do so register/unregister an object that implements 
PropertyChangeListener as follows:

```java
// Register an object as PropertyChangeListener
channel.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent pce) {
                if(pce.getPropertyName().equals(Channel.PROPERTY_VALUE)){
                    Logger.getLogger(Main.class.getName()).log(Level.INFO, "Current: {0}", pce.getNewValue());
                }
            }
        });
```
        
For a Channel you can register for Channel.PROPERTY_VALUE and Channel.PROPERTY_CONNECTION changes.


## Annotations
Jcae provides a way to annotate Channel declarations within Java classes. While annotating the declarations one does not need to explicitly create/connect the Channel any more. To be able to work with classes containing annotations, the annotated  Channels need to be connected via the ChannelService. This is done via the createAnnotatedChannels(...) function. While calling this function the factory establishes all connections and monitors of the annotated 
Channels.

Within annotations macros can be used (see examples section on how to use this). Macros are inserted into the name like this: `${MACRO}`. The replacement values for the macros need to be specified in the second parameter while calling the `createAnnotatedChannels(object, macro)` function.

### Usage
  * Declaration

```java
public class TestClass{
        @CaChannel(name="CHANNEL-ZERO", type=String.class, monitor=true)
        private Channel<String> type;
        @CaChannel(name={"CHANNEL-ONE", "CHANNEL-TWO", "CHANNEL-THREE"}, type=Double.class, monitor=true)
        private <List<Channel<Double>> values;

        // More code ...
}
```

  * Connect Channel / Registration

```java
TestClass cbean = new TestClass();
channelService.createAnnotatedChannels(cbean);
```

  * Disconnect Bean

```java
channelService.destroyAnnotatedChannels(cbean);
```

### @CaChannel
The CaChannel annotation can be used to annotate Channels or list of channels. 
The annotation takes following parameters:

| Data Type | Name | Default Value | Description |
| --- | --- | --- | --- |
| Class<?> | type | | Type of the ChannelBean |
| String | name | | Name(s) of the channel that should be managed by the (list of) ChannelBean(s) |
| boolean | monitor | false | Flag to indicate whether the channel should be monitored |

```java
@CaChannel( name="TEST", type=String.class, monitor=true)
private Channel<String> testvariable;
// Annotation list of Channels
@CaChannel( name={"TEST1", "TEST2", "TEST3"}, type=Double.class, monitor=true)
private <List<Channel<Double>> list;
```

### @CaPreInit
Execute the annotated function(s) before initializing all Channels that are annotated 
with @CaChannel. If multiple functions are annotated with @CaPreInit, the order of execution 
is not guaranteed. The annotated method must NOT take any parameters!

```java
@CaPreInit
public void myPreInit(){
}
```

### @CaPostInit
Execute the annotated function after initializing all Channels that are annotated with @CaChannel. 
If multiple functions are annotated with @CaPostInit, the order of execution is not guaranteed. 
The annotated method must NOT take any parameters!

```java
@CaPostInit
public void postInit(){
}
```

### @CaPreDestroy
Execute the annotated function(s) before destruction of all Channels that are annotated with @CaChannel. 
If multiple functions are annotated with @CaPreDestroy, the order of execution is not guaranteed. 
The annotated method must NOT take any parameters!

```java
@CaPreDestroy
public void myPreDestroy(){
}
```

### @CaPostDestroy
Execute the annotated function after destruction of all Channels that are annotated with @CaChannel.
If multiple functions are annotated with @CaPostDestroy, the order of execution is not guaranteed. 
The annotated method must NOT take any parameters!

```java
@CaPostDestroy
public void postDestroy(){
}
```

## Examples

### Get Example

```java
import ch.psi.jcae.Channel;
import ch.psi.jcae.ChannelDescriptor;
import ch.psi.jcae.ChannelException;
import ch.psi.jcae.impl.DefaultChannelService;
import gov.aps.jca.CAException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GetExample {

    public static void main(String[] args) throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {

        // Get channel factory
        DefaultChannelService factory = new DefaultChannelService();

        // Connect to channel
        Channel<String> bean = factory.createChannel(new ChannelDescriptor<String>(String.class, "ARIDI-PCT:CURRENT", true));

        // Get value
        String value = bean.getValue();
        Logger.getLogger(GetExample.class.getName()).log(Level.INFO, "{0}", value);

        // Disconnect from channel
        bean.destroy();

        // Close all connections
        factory.destroy();
    }
}
```

### Asynchronous Operations

```
import ch.psi.jcae.Channel;
import ch.psi.jcae.ChannelDescriptor;
import ch.psi.jcae.ChannelException;
import ch.psi.jcae.ChannelService;
import ch.psi.jcae.impl.DefaultChannelService;
import gov.aps.jca.CAException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AsynchronousExample {

    public static void main(String[] args) throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {

        // Get channel factory
        ChannelService context = new DefaultChannelService();

        // Connect to channel
        Channel<String> channel = context.createChannel(new ChannelDescriptor<String>(String.class, "ARIDI-PCT:CURRENT"));

        // Get value
        Future<String> futureValue = channel.getValueAsync();
//        Future<String> future = channel.setValueAsync("value");
        
        // ... Do lots of stuff
        System.out.println("... doing heavy work ...");
        
        String value = futureValue.get();
//        String valueset = future.get();
        Logger.getLogger(AsynchronousExample.class.getName()).log(Level.INFO, "{0}", value);

        // Disconnect from channel
        channel.destroy();

        // Close all connections
        context.destroy();
    }
}
```

### Monitor Example

```java
import ch.psi.jcae.Channel;
import ch.psi.jcae.ChannelDescriptor;
import ch.psi.jcae.ChannelException;
import ch.psi.jcae.impl.DefaultChannel;
import ch.psi.jcae.impl.DefaultChannelService;
import gov.aps.jca.CAException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MonitorExample {

    public static void main(String[] args) throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {
        // Get channel factory
        DefaultChannelService service = new DefaultChannelService();

        // Create ChannelBean
        Channel<String> bean = service.createChannel(new ChannelDescriptor<String>(String.class, "ARIDI-PCT:CURRENT", true));

        // Add PropertyChangeListener to ChannelBean to get value updates
        bean.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent pce) {
                if (pce.getPropertyName().equals(DefaultChannel.PROPERTY_VALUE)) {
                    Logger.getLogger(MonitorExample.class.getName()).log(Level.INFO, "Current: {0}", pce.getNewValue());
                }
            }
        });

        // Monitor the Channel for 10 seconds
        Thread.sleep(10000);

        // Destroy ChannelBean
        bean.destroy();

        // Destroy context of the factory
        service.destroy();
    }
}
```

### Annotation Example

```java
package ch.psi.jcae.examples;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.aps.jca.CAException;
import ch.psi.jcae.Channel;
import ch.psi.jcae.ChannelException;
import ch.psi.jcae.ChannelService;
import ch.psi.jcae.annotation.CaChannel;
import ch.psi.jcae.impl.DefaultChannel;
import ch.psi.jcae.impl.DefaultChannelService;

public class AnnotationExample {

    public static void main(String[] args) throws InterruptedException, TimeoutException, ChannelException, CAException, ExecutionException {
        // Get channel factory
        ChannelService service = new DefaultChannelService();

        ChannelBeanContainer container = new ChannelBeanContainer();
        
        // Connect to channel(s) in the container
        Map<String,String> macros = new HashMap<>();
        macros.put("MACRO_1", "ARIDI");
        macros.put("MACRO_2", "PCT");
        service.createAnnotatedChannels(container, macros);
        
        Double value = container.getCurrent().getValue();
        String unit = container.getUnit().getValue();
        Logger.getLogger(AnnotationExample.class.getName()).log(Level.INFO, "Current: {0} [{1}]", new Object[]{value, unit});
        
        // Disconnect channel(s) in the container
        service.destroyAnnotatedChannels(container);
        
        // Destroy context of the factory
        service.destroy();
    }
}

/**
 * Container class
 */
class ChannelBeanContainer {

    @CaChannel(type=Double.class, name="${MACRO_1}-${MACRO_2}:CURRENT", monitor=true)
    private Channel<Double> current;
    
    @CaChannel(type=String.class, name="${MACRO_1}-${MACRO_2}:CURRENT.EGU", monitor=true)
    private Channel<String> unit;

    public Channel<Double> getCurrent() {
        return current;
    }

    public Channel<String> getUnit() {
        return unit;
    }
}

```

### Complete Annotation Example

```java
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.aps.jca.CAException;
import ch.psi.jcae.Channel;
import ch.psi.jcae.ChannelException;
import ch.psi.jcae.ChannelService;
import ch.psi.jcae.annotation.CaChannel;
import ch.psi.jcae.annotation.CaPostDestroy;
import ch.psi.jcae.annotation.CaPostInit;
import ch.psi.jcae.annotation.CaPreDestroy;
import ch.psi.jcae.annotation.CaPreInit;
import ch.psi.jcae.impl.DefaultChannelService;

public class CompleteAnnotationExample {

    public static void main(String[] args) throws CAException, InterruptedException, TimeoutException, ChannelException, ExecutionException {
        // Get channel factory
        ChannelService service = new DefaultChannelService();

        ChannelBeanContainerComplete container = new ChannelBeanContainerComplete();
        
        // Connect to channel(s) in the container
        service.createAnnotatedChannels(container);
        
        Double value = container.getCurrent().getValue();
        String unit = container.getUnit().getValue();
        Logger.getLogger(CompleteAnnotationExample.class.getName()).log(Level.INFO, "Current: {0} [{1}]", new Object[]{value, unit});
        
        // Disconnect channel(s) in the container
        service.destroyAnnotatedChannels(container);
        
        // Destroy context of the factory
        service.destroy();
    }
}

/**
 * Container class
 */
class ChannelBeanContainerComplete {
    
    @CaChannel(type=Double.class, name="ARIDI-PCT:CURRENT", monitor=true)
    private Channel<Double> current;
    
    @CaChannel(type=String.class, name="ARIDI-PCT:CURRENT.EGU", monitor=true)
    private Channel<String> unit;

    @CaPreInit
    public void preInit(){
        // Code executed before connecting the channels
    }
    
    @CaPostInit
    public void postInit(){
        // Code executed after connecting channels
    }
    
    @CaPreDestroy
    public void preDestroy(){
        // Code executed before destroying channels
    }
    
    @CaPostDestroy
    public void postDestroy(){
        // Code executed after destroying channels
    }
    
    public Channel<Double> getCurrent() {
        return current;
    }
    
    public Channel<String> getUnit() {
        return unit;
    }
}
```

# Development
To be able to build the package there are no prerequisites other than Java >= 1.7. The package can be build via gradle.

 * Use `./gradlew build` to create a new version of the package.
 * Use `./gradlew uploadArchives` to upload the jar into the PSI artifact repository
 * Use `./gradlew fatJar` to create the all in one package for Matlab

## Channel Access Specification
The specification can be found at: http://epics.cosylab.com/cosyjava/JCA-Common/Documentation/CAproto.html
