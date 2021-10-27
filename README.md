# Distributed-chat-system

## Executing the Chat Server
1. The executable chatServer.jar is provided.

2. A chat server can be executed as follows:

```java -jar chatServer.jar -l <path-to-server_conf.tab> -n <server-id>```


3. ```<path-to-server_conf.tab>``` indicates the path to the server_conf.tab file. This path goes as ```path-to-the-source-code/src/main/java/org/example/config/server_conf.tab```

eg: if the source code is at ```C:/Distributed-chat-system```, then the path to be included is ```C:/Distributed-chat-system/src/main/java/org/example/config/server_conf.tab```

4. ```<server-id>``` indicates the id of the server to be started. This server id should be one that is included in the server_conf.tab file.
