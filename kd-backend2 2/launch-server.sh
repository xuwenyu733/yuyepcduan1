#!/bin/bash
# 应用名称
APP_NAME=yudao-server.jar
# 环境配置
profiles_active=local
# 远程调试端口（生产环境建议注释掉）
remote_vm_port=5005
# JVM 参数
JVM_OPTS="-Xms1024m -Xmx2048m"
JVM_OPTS="$JVM_OPTS -XX:MetaspaceSize=512m -XX:MaxMetaspaceSize=1024m"
JVM_OPTS="$JVM_OPTS -XX:+HeapDumpOnOutOfMemoryError"
JVM_OPTS="$JVM_OPTS -XX:HeapDumpPath=./logs/heap_dump.hprof"
JVM_OPTS="$JVM_OPTS -Dfile.encoding=UTF-8"
JVM_OPTS="$JVM_OPTS -Dloader.path=./libs"
JVM_OPTS="$JVM_OPTS --add-opens java.desktop/java.awt.font=ALL-UNNAMED"
# 远程调试（可选，生产环境建议注释）
# JVM_OPTS="$JVM_OPTS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:${remote_vm_port}"
# 应用参数
APP_OPTS="--spring.profiles.active=${profiles_active}"
# 启动命令
nohup java $JVM_OPTS -jar $APP_NAME $APP_OPTS > nohup.out 2>&1 &
# 获取进程ID
PID=$!
echo "应用启动成功，进程ID: $PID"
echo $PID > app.pid
# 等待启动
sleep 3
# 检查进程是否存在
if ps -p $PID > /dev/null; then
   echo "应用正在运行..."
   tail -f nohup.out
else
   echo "应用启动失败，请检查 nohup.out 日志"
   exit 1
fi
