package org.example.models.client;

import io.netty.channel.ChannelHandlerContext;
import org.example.models.room.Room;

public class Client implements IClient{
    private String identity = "";
    private ChannelHandlerContext ctx;
    private Room room;

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }
}
