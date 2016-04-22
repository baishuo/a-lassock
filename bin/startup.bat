@ECHO off

@ECHO start lassock...

setlocal
SET msg=lassock started.

SET CURRENT_HOME=%cd%/../

SET class_path=%CLASS_PATH%

SET host_name = %COMPUTERNAME%

::classpth is not exsit.

IF class_path == "" msg="plese install jdk." goto end


goto start

:start
start "Lassock" java.exe -cp %CURRENT_HOME%/conf/*;%CURRENT_HOME%/lassock.jar com.aleiye.lassock.Lassock %host_name% >> %CURRENT_HOME%/logs/run.log 2>&1 &
:end

@ECHO %msg%