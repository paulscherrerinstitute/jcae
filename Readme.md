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