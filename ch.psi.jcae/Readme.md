# Overview

The package ch.psi.jcae.cas contains an utility library to easily create an channel access server.

# Development
The package is build via Maven.

Use `mvn clean install deploy` to create a new version of the package and to upload the artifact to artifactory.


## Google Code
Use maven goal `gcupload:gcupload` to upload binaries (jar) to google code.

# Notes

## Channel Access Specification

The specification can be found at: http://epics.cosylab.com/cosyjava/JCA-Common/Documentation/CAproto.html

DBR - data request buffer types
DBF - database field types

Values can be set in different ways:
  * normal - wait until channel returns acknowledgement
  * async - get a handle (i.e. Future) to wait for set operation to finish
  * noWait - issues a set command while requesting no acknowledgement (fire and forget)