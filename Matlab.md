# Overview
Jcae can be used to easily interface Epics via ChannelAccess within Matlab. This document describes how to do so within Matlab. On the exact jcae API details please refer to the general [Readme.md](Readme.md).

The latest stable package can be downloaded [here](http://slsyoke4.psi.ch:8081/artifactory/releases/jcae_all-2.7.0.jar).

The *prerequisites* for this package is *Matlab2015a* or later.

To be able to use the package, include the full qualified path of the jar in the *javaclasspath.txt* within the Matlab home folder (ideally also copy the jar into this directory). For example:

```
/Users/ebner/Documents/MATLAB/jcae_all-2.7.0.jar
```

If you need to provide special channel access settings, i.e. providing a jcae.properties file, create the file in the Matlab home folder, add the properties you need and add following line into `java.opts` (also located in the Matlab home folder - create if it doesn't exist):

```
-Dch.psi.jcae.config.file=/Users/ebner/Documents/MATLAB/jcae.properties
```

Note that similar to the jar it has to be the full qualified path of the file!

After altering the file(s) you need to restart Matlab.

There are absolutely no other dependencies that need to be met except including the Jar!

# Usage

After loading the required jar (see Overview above) channels can be created and read/written as follows:

```
import ch.psi.jcae.*
import ch.psi.jcae.impl.*
context = DefaultChannelService()
channel = context.createChannel(ChannelDescriptor('double', 'ARIDI-PCT:CURRENT'))
channel.getValue()
channel.destroy()
context.destroy()
```

Before being able to create channels there need to be a context / channel service instance. For normal setups, ideally there should be only one context per Matlab application. The context can be configured via the above mentioned jcae.properties file (in there you can specify for example the epics address list) that is passed via the _java.opts_ configuration line. To create a context use:

```Matlab
import ch.psi.jcae.*
import ch.psi.jcae.impl.*
context = DefaultChannelService()
```

The context need to be destroyed at the end of the application via

```Matlab
context.destroy()
```


To create a channel use the context createChannel function. The functions argument is a so called ChannelDescriptor which describes the desired channel, i.e. name, type, monitored (whether the channel object should be constantly monitoring the channel) as well as size (in case of array). 

Here are some examples on how to create channels:

```Matlab
% Create double channel
channel = context.createChannel(ChannelDescriptor('double', 'ARIDI-PCT:CURRENT'))
% Create monitored double channel
channel = context.createChannel(ChannelDescriptor('double', 'ARIDI-PCT:CURRENT', true))
% Create a channel for a double waveform/array of size 10
channel = context.createChannel(ChannelDescriptor('double', 'ARIDI-PCT:CURRENT', true, 10))
```

After creating a channel you are able to get and set values via the `getValue()` and `setValue(value)` methods. _Note_, if you created a channel with the monitored flag set true `getValue()` will not reach for the network to get the latest value of the channel but returns the latest update by a channel monitor.
If you require to explicitly fetch the value over the network use `getValue(true)` (this should only be rare cases as most of the time its enough to get the cached value)

`Note` a polling loop within your Matlab application on a channel created with the monitored flag set *true* is perfectly fine and does not induce any load on the network.
 
After you are done working with a channel close the channel via

```Matlab
channel.destroy()
```

There are various other ways to interact with a channel. However for more details on them have a look at the general [Readme.md](Readme.md).