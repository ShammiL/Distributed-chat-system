package org.example.services.client;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;
import org.example.models.client.IClient;
import org.example.models.room.GlobalRoom;
import org.example.models.room.Room;
import org.example.models.server.LeaderState;
import org.example.models.server.ServerInfo;
import org.example.models.server.ServerState;
import org.example.services.client.decoders.ChatRequestObjectDecoder;
//import org.example.services.client.validators.ChatRequestValidator;
import org.example.services.coordination.MessageSender;
import org.json.simple.JSONObject;


import java.io.IOException;
import java.net.ConnectException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatClientServer {
    private static ChatClientServer instance;
    private final int port;
    private static String id;
    public static Map<ChannelId, IClient> channelIdClient = new ConcurrentHashMap<>();
    public static Map<String, Room> localRoomIdLocalRoom = new ConcurrentHashMap<>();

    private final ServerInfo serverInfo;
    private Logger logger = Logger.getLogger(ChatClientServer.class);

    private ChatClientServer() {
        this.serverInfo = ServerState.getInstance().getServerInfo();
        id = serverInfo.getServerId();
        this.port = serverInfo.getClientPort();
        localRoomIdLocalRoom.put("MainHall-" + id, new Room("MainHall-" + id));

        // if leader add to global list
        if(ServerState.getInstance().isCoordinator()){
            GlobalRoom gRoom = new GlobalRoom("MainHall-" + id, ServerState.getInstance().getServerInfo().getServerId());
            LeaderState.getInstance().checkAndAddRoom(gRoom);
        } else{
            JSONObject response = null;
            try {
                response = MessageSender.reserveIdentity(
                        ServerState.getInstance().getCoordinator(),
                        "MainHall-" + id,
                        "room"
                );
            } catch (Exception e) {
                if(e instanceof ConnectException) {
                    System.out.println("connect exception");
                }
                else {
                    System.out.println(e.getMessage());
                }
            }

        }
        System.out.println("mainHall added");

        logger.info("ChatClientServer instance created");

    }


    public static ChatClientServer getInstance() {
        if (instance == null) {
            instance = new ChatClientServer();
        }
        return instance;
    }


    public static Room getMainHal() {
        return localRoomIdLocalRoom.get("MainHall-" + id);
    }

    public static String getId() {
        return id;
    }

    public void run() throws Exception {
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        EventLoopGroup childGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(parentGroup, childGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws IOException {
                            socketChannel.pipeline().addLast(
                                    new JsonObjectDecoder(),
                                    new ChatRequestObjectDecoder(),
                                    new StringEncoder(CharsetUtil.UTF_8),
//                                    new ChatRequestValidator(),
                                    new ChatServerHandler()
                            );
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.SO_BACKLOG, 128);
            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } finally {
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        }
    }
}
