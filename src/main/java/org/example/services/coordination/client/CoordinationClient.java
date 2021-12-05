package org.example.services.coordination.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import org.example.models.messages.coordination.AbstractCoordinationMessage;
import org.example.models.messages.coordination.leader.reply.ReplyObjects;
import org.example.services.coordination.decoders.CoordinationMessageDecoder;
import org.example.services.coordination.encoders.CoordinationMessageEncoder;
import org.json.simple.JSONObject;

import java.util.concurrent.atomic.AtomicBoolean;

public class CoordinationClient {

    private final String host;
    private final int port;
    private final boolean async;

    public CoordinationClient(String host, int port) {
        this(host, port, false);
    }

    public CoordinationClient(String host, int port, boolean async) {
        this.host = host;
        this.port = port;
        this.async = async;
    }

    public JSONObject sendMessageAndGetStatus(AbstractCoordinationMessage message) throws InterruptedException {
        return sendMessageAndGetStatus(message, null, null);
    }

    public JSONObject sendMessageAndGetStatus(AbstractCoordinationMessage message, Runnable success, Runnable failure) throws InterruptedException {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
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
                            new StringEncoder(CharsetUtil.UTF_8),
                            new CoordinationClientHandler(message, responseObj));
                }
            });

            // Start the client.

            // Wait until the connection is closed.
            if (async) {
                ChannelFuture f = bootstrap.connect(host, port);
                f.addListener((ChannelFutureListener) channelFuture -> {
                    if (f.isSuccess() && success != null) {
                        success.run();
                    } else if (!f.isSuccess() && failure != null) {
                        failure.run();
                    }
                    workerGroup.shutdownGracefully();
                    f.channel().closeFuture();
                });
            } else {
                ChannelFuture f = bootstrap.connect(host, port).sync();
                f.channel().closeFuture().sync();
            }

        } finally {
            if (!async) {
                workerGroup.shutdownGracefully();
            }
        }

        return responseObj;
    }
}
