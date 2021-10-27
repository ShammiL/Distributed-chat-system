package org.example.models.messages.chat.reply;

import org.example.services.client.ChatClientServer;
import org.json.simple.JSONObject;

import java.util.List;

public class ReplyObjects {
    private ReplyObjects() {}
    @SuppressWarnings("unchecked")
    public static JSONObject newIdentityReply(String approveVal) {
        JSONObject newIdentity = new JSONObject();
        newIdentity.put("type", "newidentity");
        newIdentity.put("approved", approveVal);
        return newIdentity;
    }

    @SuppressWarnings("unchecked")
    public static JSONObject newIdRoomChange(String identity, String mainhall) {
        JSONObject idRoomChange = new JSONObject();
        idRoomChange.put("type", "roomchange");
        idRoomChange.put("identity", identity);
        idRoomChange.put("former", "");
        idRoomChange.put("roomid", mainhall);
        return idRoomChange;
    }

    @SuppressWarnings("unchecked")
    public static JSONObject message(String identity, String content) {
        JSONObject msg = new JSONObject();
        msg.put("type", "message");
        msg.put("identity", identity);
        msg.put("content", content);
        return msg;
    }

    @SuppressWarnings("unchecked")
    public static JSONObject createNewRoom(String roomId, boolean approved) {
        JSONObject msg = new JSONObject();
        msg.put("type", "createroom");
        msg.put("roomid", roomId);
        msg.put("approved", Boolean.toString(approved));
        return msg;
    }

    @SuppressWarnings("unchecked")
    public static JSONObject roomChange(String clientId, String roomId, String formerRoomId) {
        JSONObject msg = new JSONObject();
        msg.put("type", "roomchange");
        msg.put("identity", clientId);
        msg.put("former", formerRoomId);
        msg.put("roomid", roomId);
        return msg;
    }

    public static JSONObject deleteRoomClientMessage(String roomId, boolean approved){
        JSONObject msg = new JSONObject();
        msg.put("type", "deleteroom");
        msg.put("roomid", roomId);
        msg.put("approved", Boolean.toString(approved));
        return msg;
    }

    public static JSONObject deleteRoomServerMessage(String roomId){
        JSONObject msg = new JSONObject();
        msg.put("type", "deleteroom");
        msg.put("roomid", roomId);
        msg.put("serverid", ChatClientServer.getInstance().getId());
        return msg;
    }

    public static JSONObject roomContents(String roomId, List<String> identities, String owner) {
        JSONObject msg = new JSONObject();
        msg.put("type", "roomcontents");
        msg.put("roomid", roomId);
        msg.put("identities", identities);
        msg.put("owner", owner);
        return msg;
    }

    public static JSONObject routeMessage(String roomId, String host, String port) {
        JSONObject msg = new JSONObject();
        msg.put("type", "route");
        msg.put("roomid", roomId);
        msg.put("host", host);
        msg.put("port", port);
        return msg;
    }
}
