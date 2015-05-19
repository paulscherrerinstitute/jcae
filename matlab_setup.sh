#!/bin/bash

BASE_DIR=`pwd`

VERSION=2.7.0

CLASSPATH_FILE=javaclasspath.txt
CONFIGURATION_FILE=jcae.properties
OPTION_FILE=java.opts
JAR_FILE=jcae_all-${VERSION}.jar


echo 'Downloading latest Matlab Channel Access library ...'
curl -O http://slsyoke4.psi.ch:8081/artifactory/releases/${JAR_FILE}

echo 'Creating configuration files ...'
# Create classpath file
touch ${BASE_DIR}/${CLASSPATH_FILE}
echo ${BASE_DIR}/${JAR_FILE} >> ${BASE_DIR}/${CLASSPATH_FILE}

# Create configuration files
touch ${BASE_DIR}/${CONFIGURATION_FILE}

touch ${BASE_DIR}/${OPTION_FILE}
echo -Dch.psi.jcae.config.file=${BASE_DIR}/${CONFIGURATION_FILE} >> ${BASE_DIR}/${OPTION_FILE}

echo 'Done'