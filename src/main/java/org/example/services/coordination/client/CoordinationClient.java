package org.example.services.coordination.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.example.models.messages.coordination.AbstractCoordinationMessage;
import org.example.models.messages.coordination.leader.reply.ReplyObjects;
import org.example.services.coordination.decoders.CoordinationMessageDecoder;
import org.example.services.coordination.encoders.CoordinationMessageEncoder;
import org.json.simple.JSONObject;

import java.util.concurrent.atomic.AtomicBoolean;

public class CoordinationClient {

    private final String host;
    private final int port;
    public  CoordinationClient(String host, int port) {
        this.host = host;
        this.port = port;
    }
    public JSONObject sendMessageAndGetStatus(AbstractCoordinationMessage message) throws InterruptedException {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
//        AtomicBoolean status = new AtomicBoolean(false);
        JSONObject responseObj = ReplyObjects.leaderResponse();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel channel) throws Exception {
                    channel.pipeline().addLast(
                            new JsonObjectDecoder(),
                            new CoordinationMessageDecoder(),
                            new CoordinationMessageEncoder(),
                            new StringEncoder(),
//                            new CoordinationClientHandler(message, status));
                            new CoordinationClientHandler(message, responseObj));

                }
            });

            // Start the client.
            ChannelFuture f = bootstrap.connect(host, port).sync();

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
//        return status;
        return responseObj;
    }

}
