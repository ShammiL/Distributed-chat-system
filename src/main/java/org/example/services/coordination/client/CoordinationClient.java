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
import org.example.models.messages.coordination.AbstractCoordinationMessage;
import org.example.services.coordination.encoders.CoordinationRequestEncoder;

import java.util.concurrent.atomic.AtomicBoolean;

public class CoordinationClient {

    private final String host;
    private final int port;
    public  CoordinationClient(String host, int port) {
        this.host = host;
        this.port = port;
    }
    //TODO: Improve the method to not just return a (atomic) boolean
    public AtomicBoolean sendMessageAndGetStatus(AbstractCoordinationMessage message) throws InterruptedException {
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
                            new CoordinationRequestEncoder(),
                            new CoordinationClientHandler(message, status));
                }
            });

            // Start the client.
            ChannelFuture f = bootstrap.connect(host, port).sync(); // (5)

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
        return status;
}

}
