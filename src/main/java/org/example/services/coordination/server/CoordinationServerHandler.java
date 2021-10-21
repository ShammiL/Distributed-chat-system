package org.example.services.coordination.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.example.handlers.requestHandlers.coordination.AbstractCoordinationRequestHandler;
import org.example.handlers.requestHandlers.coordination.CoordinationRequestHandlerFactory;
import org.example.models.messages.coordination.AbstractCoordinationMessage;
import org.example.models.server.ServerInfo;
import org.example.models.server.ServerState;
import org.json.simple.JSONObject;

public class CoordinationServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        AbstractCoordinationMessage message = (AbstractCoordinationMessage) msg;
        try {
            //TODO: Authenticate requesting server identity
            ServerInfo server = ServerState.getInstance().getServerInfoById(message.getServerName());
            if (server != null) {
                AbstractCoordinationRequestHandler handler = CoordinationRequestHandlerFactory
                        .requestHandler(message, server);
                JSONObject response = handler.handleRequest();
                // TODO: Handle requests without responses cleanly
                if (response != null) {
                    ctx.writeAndFlush(response.toJSONString());
                }
            }
            //TODO: If server null
            ctx.close();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }
}
