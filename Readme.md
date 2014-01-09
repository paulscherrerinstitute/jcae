# Overview

The package ch.psi.jcae.cas contains an utility library to easily create an channel access server.

# Usage

## Prerequisites
### Dependencies
The easiest way to get all the jcae dependencies is to use the Maven. Add following dependency to your dependencies section:

```xml
<dependency>
        <groupId>ch.psi</groupId>
        <artifactId>jcae</artifactId>
        <version>version.to.use</version>
</dependency>
```

## Configuration
The JCA Extension library/classes obtain its configuration from the central `jcae.properties` file. 
Note: In most cases no `jca.properties` file is needed! The `jcae.properties` file either need to reside inside 
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
| ch.psi.jcae.ContextFactory.serverPort | | Channel Access server port |

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
The channel factory uses a JCA Context for creating, managing and destructing channels. This context need to be destroyed at the end of every program. If the context is not destroyed, the Java Virtual Machine will not exit. The destruction of the Context is done as follows:

```java
ChannelBeanFactory.getFactory().getChannelFactory().destroyContext();
```

## ChannelBean
ChannelBean is the major abstraction provided by the jcae Library. It introduces an object oriented abstraction 
of an Epics channel and hides the complexity of the creation/usage/destruction of the channel. 
The value of the channel can be easily accessed and modified via get and set methods. A ChannelBean can be 
created in two different modes. normal and monitor mode. In normal mode the value of the channel gets 
collected (over the network) each time the get method is called. In monitor mode a monitor is established 
for the channel that recognizes value changes and caches the value in a local variable. When the value of 
the channel is accessed via the get method the cached value is returned instead of getting it explicitly 
(over the network).

### Usage
The following section gives a short overview of the functionality of ChannelBean and its usage. 
Examples are shown for the creation, usage and destruction.

#### Creation
A ChannelBean is created via the ChannelBeanFactory. The factory is a Singleton Object an can be 
retrieved via the static method `getFactory()`:

```java
// Retrieve channel bean factory
ChannelBeanFactory factory = ChannelBeanFactory.getFactory();
// Create channel bean
ChannelBean<String> bean = factory.createChannelBean(String.class, "MYCHANNEL:XYZ", true);
```

#### Get/Set Value

```java
//Get value
String value = bean.getValue();
// Get value explicitly over the network (If ChannelBean is created in monitor mode)
value = bean.getValue(true);
// Set value
bean.setValue("hello");
```





# Development
The package is build via Maven.

Use `mvn clean install deploy` to create a new version of the package and to upload the artifact to artifactory.

# Notes

## Channel Access Specification

The specification can be found at: http://epics.cosylab.com/cosyjava/JCA-Common/Documentation/CAproto.html

DBR - data request buffer types
DBF - database field types

Values can be set in different ways:
  * normal - wait until channel returns acknowledgement
  * async - get a handle (i.e. Future) to wait for set operation to finish
  * noWait - issues a set command while requesting no acknowledgement (fire and forget)
  
Ways to Get and Set Values
  * Set/Put
    * Fire and forget (different type of CA request send out)
    * Get acknowledgement (both cases are handled the same)
      * Wait until acknowledgement is here (synchronous)
      * Do something and then check if acknowledgement was received (asynchronous)
  * Get
    * Wait until value is received
    * Do something and then check back if value was received