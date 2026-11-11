@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

:: 应用名称
set APP\_NAME=yudao-server.jar
:: 环境配置
set profiles\_active=local
:: 远程调试端口
set remote\_vm\_port=5005

:: JVM 参数
set JVM\_OPTS=-Xms1024m -Xmx2048m
set JVM\_OPTS=!JVM\_OPTS! -XX:MetaspaceSize=512m -XX:MaxMetaspaceSize=1024m
set JVM\_OPTS=!JVM\_OPTS! -XX:+HeapDumpOnOutOfMemoryError
set JVM\_OPTS=!JVM\_OPTS! -XX:HeapDumpPath=./logs/heap\_dump.hprof
set JVM\_OPTS=!JVM\_OPTS! -Dfile.encoding=UTF-8
set JVM\_OPTS=!JVM\_OPTS! -Dloader.path=./libs
set JVM\_OPTS=!JVM\_OPTS! --add-opens java.desktop/java.awt.font=ALL-UNNAMED

:: 如需开启远程调试，取消下一行注释
:: set JVM\_OPTS=!JVM\_OPTS! -agentlib:jdwp=transport=dt\_socket,server=y,suspend=n,address=\*:!remote\_vm\_port!

:: 应用启动参数
set APP\_OPTS=--spring.profiles.active=!profiles\_active!

:: 自动创建logs目录
if not exist logs mkdir logs

:: 清理旧pid
if exist app.pid del /f /q app.pid

echo ==============================================
echo 正在启动 %APP\_NAME%
echo Profile: !profiles\_active!
echo ==============================================

:: 核心：直接启动，日志实时输出到CMD窗口
java !JVM\_OPTS! -jar !APP\_NAME! !APP\_OPTS!

echo.
echo 应用进程已终止！
pause