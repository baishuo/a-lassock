#!/bin/bash
PRG="${0}"
PRGDIR=`dirname ${PRG}`
CURRENT_HOME=`cd "${PRGDIR}/.." > /dev/null;pwd `
statuechange=${CURRENT_HOME}"/statuechange.aleiye"

rm -rf ${statuechange}

sh ${CURRENT_HOME}/bin/lassock-daemon.sh

while [ 1 -eq 1 ]
do
   if [ -f $ipChangePath ]; then
      rm -rf ${ipChangePath}
      sh ${CURRENT_HOME}/bin/shutdown.sh
      sh ${CURRENT_HOME}/bin/lassock-daemon.sh
   fi
   sleep 5
done