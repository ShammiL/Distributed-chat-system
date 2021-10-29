package org.example.handlers.requestHandlers.chat;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.example.models.client.Client;
import org.example.models.client.IClient;
import org.example.models.messages.chat.AbstractChatRequest;
import org.example.models.messages.chat.reply.ReplyObjects;
import org.example.models.messages.chat.requests.chat.ListRequest;
import org.example.models.messages.coordination.leader.reply.GlobalRoomResponse;
import org.example.models.room.GlobalRoom;
import org.example.models.server.LeaderState;
import org.example.models.server.ServerState;
import org.example.services.coordination.MessageSender;
import org.json.simple.JSONObject;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Map;

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
        logger.info("room list request from :" + getClient().getIdentity());
        try {
            if (ServerState.getInstance().isCoordinator()) {
                JSONObject response = ReplyObjects.roomList(getRooms());
                sendResponse(response);
            } else {
                sendRequestToLeaderAndSendResponseTOClient();
            }
        } catch (InterruptedException | ConnectException e) {
            logger.error("Error when getting lists from the coordinator " + e.getMessage());
            request.incrementTries();
            ServerState.getInstance().addRetryRequest(request);
        }
    }

    private void sendRequestToLeaderAndSendResponseTOClient() throws InterruptedException, ConnectException {
        JSONObject response = null;

        response = MessageSender.requestRoomList(
                ServerState.getInstance().getCoordinator()
        );

        Gson gson = new Gson();
        GlobalRoomResponse globalRoomResponse = gson.fromJson(response.get("room_list").toString(),
                GlobalRoomResponse.class);
        ArrayList<String> rooms = new ArrayList<>();
        for (Map.Entry<String, GlobalRoom> entry : globalRoomResponse.getGlobalRoomList().entrySet()) {
            rooms.add(entry.getKey());
        }

        JSONObject clientResponse = ReplyObjects.roomList(rooms);
        sendResponse(clientResponse);


    }

    public ArrayList<String> getRooms() {
        ArrayList<String> rooms = new ArrayList<>();
        for (Map.Entry<String, GlobalRoom> entry : LeaderState.getInstance().getGlobalRoomList().entrySet()) {
            GlobalRoom gRoom = entry.getValue();
            rooms.add((gRoom).getRoomId());

        }
        return rooms;
    }
}
