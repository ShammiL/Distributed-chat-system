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
import org.example.services.coordination.decoders.CoordinationMessageDecoder;
import org.example.services.coordination.encoders.CoordinationMessageEncoder;

import java.util.concurrent.atomic.AtomicBoolean;

public class CoordinationClient {

    private final String host;
    private final int port;
    private final boolean async;

    public CoordinationClient(String host, int port){
        this(host, port, false);
    }
    public  CoordinationClient(String host, int port, boolean async) {
        this.host = host;
        this.port = port;
        this.async = async;
    }


    public AtomicBoolean sendMessageAndGetStatus(AbstractCoordinationMessage message) throws InterruptedException {
        return sendMessageAndGetStatus(message, null, null);
    }

    //TODO: Improve the method to not just return a (atomic) boolean
    public AtomicBoolean sendMessageAndGetStatus(AbstractCoordinationMessage message, Runnable success, Runnable failure) throws InterruptedException {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        AtomicBoolean status = new AtomicBoolean(false);
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
                            new CoordinationClientHandler(message, status));
                }
            });

            // Start the client.

            // Wait until the connection is closed.
            if (async) {
                ChannelFuture f = bootstrap.connect(host, port);
                f.addListener((ChannelFutureListener) channelFuture -> {
                    if (f.isSuccess() && success != null){
                        success.run();
                    } else if( !f.isSuccess() && failure != null) {
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
            if(!async) {
                workerGroup.shutdownGracefully();
            }
        }
        // Status will always return false for async requests
        return status;
}

}
