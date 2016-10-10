#!/bin/bash
PRG="${0}"
PRGDIR=`dirname ${PRG}`
CURRENT_HOME=`cd "${PRGDIR}/.." > /dev/null;pwd `
statuechange=${CURRENT_HOME}/statuechange.aleiye

rm -rf ${statuechange}

sh ${CURRENT_HOME}/bin/lassock-daemon.sh -p start

while [ 1 -eq 1 ]
do
   if [ -f ${statuechange} ]; then
      rm -rf ${statuechange}
      sh ${CURRENT_HOME}/bin/lassock-daemon.sh -p stop
      sh ${CURRENT_HOME}/bin/lassock-daemon.sh -p start
   fi
   sleep 5
done