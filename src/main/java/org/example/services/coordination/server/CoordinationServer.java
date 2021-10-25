package org.example.services.coordination.server;

//import com.sun.security.ntlm.Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import org.example.models.server.LeaderState;
import org.apache.log4j.Logger;
import org.example.models.server.ServerInfo;
import org.example.models.server.ServerState;
import org.example.services.coordination.decoders.CoordinationRequestDecoder;
import org.example.services.coordination.election.BullyElection;

import java.io.IOException;

public class CoordinationServer {
    private static CoordinationServer instance;
    private final int port;

    private final ServerInfo serverInfo;
    Logger logger = Logger.getLogger(CoordinationServer.class);

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

    public void SelectCoordinator() {
        System.out.println("select coordinator");

        if(ServerState.getInstance().getHigherServerInfo().isEmpty()){
//                if there are no higher priority servers
            System.out.println("there are no higher priority servers");
            ServerState.getInstance().setCoordinator(ServerState.getInstance().getServerInfo()); // set self as the coordinator
            LeaderState.getInstance().assignOwnLists(); // set self ss the leader
            new BullyElection().informAndSetNewCoordinator(
                    ServerState.getInstance().getLowerServerInfo()
            );
        } else{
            new BullyElection().startElection(ServerState.getInstance().getHigherServerInfo());
        }


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
            logger.info("Coordination server started");
        } finally {
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        }
    }
}
