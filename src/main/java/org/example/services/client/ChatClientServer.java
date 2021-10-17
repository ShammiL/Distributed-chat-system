package org.example.services.client;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import org.example.models.client.IClient;
import org.example.models.room.Room;
import org.example.services.client.decoders.RequestObjectDecoder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatClientServer {

    private final int port;
    private static String id;
    public static Map<ChannelId, IClient> channelIdClient = new ConcurrentHashMap<>();
    public static Map<String, Room> localRoomIdLocalRoom = new ConcurrentHashMap<>();


    public ChatClientServer(int port, String id) {
        this.id = id;
        this.port = port;
        localRoomIdLocalRoom.put("MainHall-" + id, new Room("MainHall-" + id) );
    }

    public static Room getMainHal(){
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
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(
                                    new JsonObjectDecoder(),
                                    new RequestObjectDecoder(),
                                    new StringEncoder(CharsetUtil.UTF_8),
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
