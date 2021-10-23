package org.example.services.coordination.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import org.example.models.server.ServerInfo;
import org.example.models.server.ServerState;
import org.example.services.coordination.decoders.CoordinationRequestDecoder;

import java.io.IOException;

public class CoordinationServer {
    private static CoordinationServer instance;
    private final int port;

    private final ServerInfo serverInfo;

    public void SelectCoordinator() {

    }
    private CoordinationServer() {
        this.serverInfo = ServerState.getInstance().getServerInfo();
        this.port = serverInfo.getCoordinationPort();
    }

    public static CoordinationServer getInstance() {
        if (instance == null) {
            instance = new CoordinationServer();
        }
        return instance;
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
                                    new CoordinationRequestDecoder(),
                                    new StringEncoder(CharsetUtil.UTF_8),
                                    new CoordinationServerHandler()
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
