# 参考资料
https://programming.vip/docs/four-ways-of-integrating-websocket-with-spring-boot.html  
https://www.cnblogs.com/hhhshct/p/9507446.html

# 启动多个客户端的步骤
1. 生成jar包：在./WebSocketClient目录下运行 mvn package。jar包在./WebSocketClient/target目录下，它的依赖在/WebSocketClient/target/lib目录下。
2. 开多个terminal，在./WebSocketClient/target目录下，运行 java -jar WebSocketClient-1.0-SNAPSHOT.jar