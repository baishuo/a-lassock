#!/bin/bash
PRG="${0}"
PRGDIR=`dirname ${PRG}`
CURRENT_HOME=`cd "${PRGDIR}/.." > /dev/null;pwd `
pidfile=${CURRENT_HOME}"/check.pid"


TestPid () {
    if [ -f ${pidfile} ] ; then
        if ps ux | grep `cat $pidfile` |grep "aleiye-lassock-check.sh" | grep -v "grep" > /dev/null ; then
            echo "`date +"%F %T"`: `basename $0` is running!  main process id = $(cat ${pidfile})"
            exit 1
        else
            rm -f $pidfile
        fi
    fi
}
# Trap
trap "rm -f ${pidfile} ; exit 1 " 1 2 3 15
TestPid
nohup sh ${CURRENT_HOME}/bin/aleiye-lassock-check.sh >> /dev/null 2>&1 &
echo $! > ${pidfile}
