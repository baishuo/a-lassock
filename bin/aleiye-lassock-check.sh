#!/bin/bash
PRG="${0}"
PRGDIR=`dirname ${PRG}`
CURRENT_HOME=`cd "${PRGDIR}/.." > /dev/null;pwd `
ipChangePath=${CURRENT_HOME}"/ipChange.aleiye"

rm -rf ${ipChangePath}

sh ${CURRENT_HOME}/bin/lassock-daemon.sh

while [ 1 -eq 1 ]
do
   if [ -d $ipChangePath ]; then
      rm -rf ${ipChangePath}
      sh ${CURRENT_HOME}/bin/shutdown.sh
      sh ${CURRENT_HOME}/bin/lassock-daemon.sh
   fi
   sleep 10
done