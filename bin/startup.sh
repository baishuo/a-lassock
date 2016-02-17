#!/bin/bash
PRG="${0}"
PRGDIR=`dirname ${PRG}`
CURRENT_HOME=`cd "${PRGDIR}/.." > /dev/null;pwd `
pidfile=${CURRENT_HOME}"/raker.pid"

TestPid () {
    if [ -f ${pidfile} ] ; then
        if ps ux | grep `cat $pidfile` |grep "lassock.jar" | grep -v "grep" > /dev/null ; then
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

devName=`hostname`

nohup java -cp ${CURRENT_HOME}/lib/*:${CURRENT_HOME}/conf/*:${CURRENT_HOME}/raker.jar com.aleiye.lassock.Lassock ${devName} >> ${CURRENT_HOME}/logs/run.log 2>&1 &

echo $! > ${pidfile}


