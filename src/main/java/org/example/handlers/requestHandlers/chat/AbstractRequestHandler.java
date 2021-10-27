package org.example.handlers.requestHandlers.chat;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import org.apache.log4j.Logger;
import org.example.models.client.Client;
import org.example.models.client.IClient;
import org.example.models.room.Room;
import org.example.services.client.ChatClientServer;
import org.json.simple.JSONObject;

import java.util.Map;

public abstract class AbstractRequestHandler {
    private final Client client;
    private final Logger logger = Logger.getLogger(AbstractRequestHandler.class);

    public AbstractRequestHandler(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    public abstract JSONObject processRequest();

    public abstract void handleRequest();

    public void sendResponse(JSONObject reply) {
        final ChannelFuture f = client.getCtx().writeAndFlush(reply.toJSONString() + "\n");
        f.addListener((ChannelFutureListener) future -> {
            assert f == future;
            logger.info("Listener in the abstract request handler");
        });
    }

    public void broadcast(JSONObject reply, Room room) {
        for (Map.Entry<ChannelId, IClient> entry : ChatClientServer.channelIdClient.entrySet()) {
            Client clientFromMap = (Client) entry.getValue();
            logger.info("clientFromMap: " + clientFromMap.getIdentity() + " | client: " + client.getIdentity());
            if (!clientFromMap.getIdentity().equals(client.getIdentity()) && clientFromMap.getRoom().equals(room)) {
                ChannelHandlerContext ctxOfClientFromMap = clientFromMap.getCtx();
                final ChannelFuture f = ctxOfClientFromMap.writeAndFlush(reply.toJSONString() + "\n");
                f.addListener((ChannelFutureListener) future -> {
                    assert f == future;
                });

            }
        }

    }
}
