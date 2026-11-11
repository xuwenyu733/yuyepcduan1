@echo off
set JAVA_HOME=C:\Program Files\Java\jdk-21
set PATH=%JAVA_HOME%\bin;%PATH%
cd /d "%~dp0"
java -Dloader.path=%cd%\target\libs -Dspring.profiles.active=local -jar %cd%\target\yudao-server.jar
