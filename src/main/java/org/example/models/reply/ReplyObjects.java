package org.example.models.reply;

import org.json.simple.JSONObject;

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
    public static JSONObject newIdRoomChange(String identity) {
        JSONObject idRoomChange = new JSONObject();
        idRoomChange.put("type", "roomchange");
        idRoomChange.put("identity", identity);
        idRoomChange.put("former", "");
        idRoomChange.put("roomid", "MainHall-s1");
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
}
