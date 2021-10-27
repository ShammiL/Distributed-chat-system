package org.example.handlers.requestHandlers.chat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import io.netty.channel.ChannelId;
import org.apache.log4j.Logger;
import org.example.models.client.Client;
import org.example.models.client.IClient;
import org.example.models.messages.chat.AbstractChatRequest;
import org.example.models.messages.chat.reply.ReplyObjects;
import org.example.models.messages.chat.requests.chat.ListRequest;
import org.example.models.messages.coordination.leader.reply.GlobalRoomResponse;
import org.example.models.room.GlobalRoom;
import org.example.models.room.Room;
import org.example.models.server.LeaderState;
import org.example.models.server.ServerState;
import org.example.services.client.ChatClientServer;
import org.example.services.coordination.MessageSender;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ListRequestHandler extends AbstractRequestHandler {
    private final ListRequest request;
    private final Logger logger = Logger.getLogger(ListRequestHandler.class);

    public ListRequestHandler(AbstractChatRequest request, IClient client) {
        super((Client) client);
        this.request = (ListRequest) request;
    }

    @Override
    public JSONObject processRequest() {
        return null;
    }

    @Override
    public void handleRequest() {
        System.out.println("Client list request handler");
        try {
            if (ServerState.getInstance().isCoordinator()) {
                System.out.println("if coordiantor send list to client");
                JSONObject response = ReplyObjects.roomList(getRooms());
                System.out.println(response);
                sendResponse(response);
            } else {
                System.out.println("herrr");
                sendRequestToLeader();

            }
        } catch (InterruptedException | ConnectException e) {
            // todo: start election and resend request
            logger.error("Error when getting lists from the coordinator " + e.getMessage());
        }
    }

    private void sendRequestToLeader() throws InterruptedException, ConnectException {
        JSONObject response = null;

        response = MessageSender.requestRoomList(
                ServerState.getInstance().getCoordinator()
        );
        System.out.println(response.get("room_list"));

//        Gson gson = new Gson();
//        Map<String , GlobalRoom> rooms = gson.fromJson((JsonElement) response.get("room_list"), ConcurrentHashMap.class);
//        for(Map.Entry<String, GlobalRoom> entry : rooms.entrySet()){
//            GlobalRoom gRoom = entry.getValue();
//            System.out.println(gRoom.getRoomId());
//
//        }



    }

    public ArrayList<String> getRooms() {
        ArrayList<String> rooms = new ArrayList<>();
        for(Map.Entry<String, GlobalRoom> entry : LeaderState.getInstance().getGlobalRoomList().entrySet()){
            GlobalRoom gRoom = entry.getValue();
            rooms.add((gRoom).getRoomId());

        }
        System.out.println(rooms);
        return rooms;
    }
}
