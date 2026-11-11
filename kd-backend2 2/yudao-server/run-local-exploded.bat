@echo off
setlocal
cd /d "%~dp0target"
set JAVA_EXE=C:\Program Files\Java\jdk-21\bin\java.exe
set LOG=%~dp0..\yudao-server-run-now.out
set ERR=%~dp0..\yudao-server-run-now.err
"%JAVA_EXE%" -cp "BOOT-INF\classes;BOOT-INF\lib\*;libs\*" cn.iocoder.yudao.server.YudaoServerApplication --spring.profiles.active=local --server.port=48181 >"%LOG%" 2>"%ERR%"
endlocal
