#!/bin/bash

BASEDIR=$(cd $(dirname $0);pwd)

EPICS_DISPLAY_PATH=$BASEDIR/../config/medm:/work/sls/config/medm
export EPICS_DISPLAY_PATH

# motorx_more.adl taken from /work/sls/config/medm
medm -x -macro "P=MTEST-PC-JCAE:" jcaeTest.adl &
