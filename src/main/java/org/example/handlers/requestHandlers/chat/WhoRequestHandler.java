package org.example.handlers.requestHandlers.chat;

import io.netty.channel.ChannelId;
import org.example.models.client.Client;
import org.example.models.client.IClient;
import org.example.models.messages.chat.reply.ReplyObjects;
import org.example.models.room.LocalRoom;
import org.example.models.room.Room;
import org.example.services.client.ChatClientServer;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class WhoRequestHandler extends AbstractRequestHandler {

    public WhoRequestHandler(IClient client) {
        super(((Client) client));
    }

    @Override
    public JSONObject processRequest() {
        Room room = getClient().getRoom();
        String roomId = room.getRoomId();
        String owner;
        if (room instanceof LocalRoom){
            owner = ((LocalRoom) room).getOwner();
        }
        else owner = "";
        ArrayList<String> roomMembers = getRoomMembers(room);
        return ReplyObjects.roomContents(roomId, roomMembers, owner);
    }

    @Override
    public void handleRequest() {
        JSONObject reply = processRequest();
        sendResponse(reply);
    }

    public ArrayList<String> getRoomMembers(Room room) {
        ArrayList<String> roomMembers = new ArrayList<>();
        for(Map.Entry<ChannelId, IClient> entry : ChatClientServer.channelIdClient.entrySet()){
            IClient client = entry.getValue();
            if(((Client) client).getRoom().equals(room)){
                roomMembers.add(((Client) client).getIdentity());
            }
        }
        return roomMembers;
    }
}