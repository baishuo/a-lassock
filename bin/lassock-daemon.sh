#!/bin/bash
PRG="${0}"
PRGDIR=`dirname ${PRG}`
CURRENT_HOME=`cd "${PRGDIR}/.." > /dev/null;pwd `
pidfile=${CURRENT_HOME}/lassock.pid

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

nohup java -Xms512m -Xmx512m -Djava.awt.headless=true -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=75 -XX:+UseCMSInitiatingOccupancyOnly -XX:+HeapDumpOnOutOfMemoryError -XX:+DisableExplicitGC -Dfile.encoding=UTF-8 -cp ${CURRENT_HOME}/conf/*:${CURRENT_HOME}/lassock.jar com.aleiye.lassock.AleiyeLassock ${devName} > /dev/null 2>&1 &
echo "lassock start successful"
echo $! > ${pidfile}