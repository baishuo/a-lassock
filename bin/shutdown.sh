#!/bin/bash
PRG="${0}"
PRGDIR=`dirname ${PRG}`
CURRENT_HOME=`cd "${PRGDIR}/.." > /dev/null;pwd `
pidfile=${CURRENT_HOME}/lassock.pid
checkpidfile=${CURRENT_HOME}/check.pid

TestPid(){


    if [ -f ${checkpidfile} ] ; then
       if ps ux|grep `cat ${pidfile}`|grep "aleiye-lassock-check.sh"|grep -v "grep" > /dev/null ; then
          kill -9 `cat ${checkpidfile}`
       fi
    fi

    if [ -f ${pidfile} ] ; then
       if ps ux|grep `cat ${pidfile}`|grep "lassock.jar"|grep -v "grep" > /dev/null ; then
          kill -9 `cat ${pidfile}`
          echo "shutdown success"
       else
         "the lassock is not running"
       fi
    else
      echo "lassock is not running"
   fi
}

TestPid

rm -rf ${pidfile}
