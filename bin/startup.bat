@ECHO off
@ECHO start lassock...

setlocal
SET msg=lassock started.

CD ..\

SET CURRENT_HOME=%cd%
SET class_path=%CLASS_PATH%


SET host_name = %COMPUTERNAME%

::classpth is not exsit.

IF class_path == "" msg="plese install jdk." goto end


goto start

:start
start "Lassock" java.exe -Xms512m -Xmx512m -Djava.awt.headless=true -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=75 -XX:+UseCMSInitiatingOccupancyOnly -XX:+HeapDumpOnOutOfMemoryError -XX:+DisableExplicitGC -Dfile.encoding=UTF-8 -cp %CURRENT_HOME%\conf\*;%CURRENT_HOME%\lassock.jar com.aleiye.lassock.AleiyeLassock %host_name% >> %CURRENT_HOME%/logs/run.log 2>&1 &
:end

@ECHO %msg%