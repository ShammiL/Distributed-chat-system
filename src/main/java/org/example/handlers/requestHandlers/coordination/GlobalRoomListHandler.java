package org.example.handlers.requestHandlers.coordination;

import com.google.gson.Gson;
import org.example.models.messages.coordination.leader.reply.GlobalRoomResponse;
import org.example.models.messages.coordination.leader.request.GlobalRoomListRequest;
import org.example.models.server.LeaderState;
import org.example.models.server.ServerInfo;
import org.example.models.server.ServerState;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class GlobalRoomListHandler extends AbstractCoordinationRequestHandler{
    private GlobalRoomListRequest request;
    public GlobalRoomListHandler(ServerInfo server, GlobalRoomListRequest request) {
        super(server);
    }

    @Override
    public JSONObject handleRequest() {
        System.out.println("global room list handler in coordination");
        GlobalRoomResponse response = new GlobalRoomResponse(
                ServerState.getInstance().getServerInfo().getServerId()
        );
        response.setGlobalRoomList(
                LeaderState.getInstance().getGlobalRoomList()
        );
        String jsonString = new Gson().toJson(response);
        JSONParser parser = new JSONParser();
        try {
            System.out.println(jsonString);
            return (JSONObject) parser.parse(jsonString);
        } catch (ParseException e) {
            System.out.println("error in parsing");
            return null;
        }

    }
}
