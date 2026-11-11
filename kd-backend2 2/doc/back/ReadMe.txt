前台本地启动：  npm run local
前台打包：   npm run build:dev
后台：   配置redis、数据库就好了，reidis不要有密码
swagger地址：   http://localhost:48080/swagger-ui/index.html

需要cmd控制台进行启动：
java -Dloader.path=E:\back_2t\Users\Idea_202004\work\0915\code1\kede_jidi3\fish_server\yudao-server\target/libs -jar ^
E:\back_2t\Users\Idea_202004\work\0915\code1\kede_jidi3\fish_server\yudao-server\target/yudao-server.jar --spring.profiles.active=local

线上日志查看：
tail -f /opt/modules/kede/fish_server/nohup.out

启动脚本：
/opt/modules/kede/fish_server/start.sh restart
tail -f /opt/modules/kede/fish_server/nohup.out



