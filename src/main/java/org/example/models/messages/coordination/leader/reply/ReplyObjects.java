package org.example.models.messages.coordination.leader.reply;

import org.json.simple.JSONObject;

public final class ReplyObjects {

    private ReplyObjects() {
    }

    public static JSONObject leaderResponse() {
        JSONObject responseObj = new JSONObject();
        responseObj.put("reserved", "false");
        responseObj.put("released", "false");
        responseObj.put("room_list", null);
        return responseObj;
    }

    public static JSONObject identityReleaseReply(boolean success, String identity, String identityType, String serverId) {
        JSONObject releaseReply = new JSONObject();
        String status = (success) ? "success" : "failure";
        releaseReply.put("type", "identity_release_response");
        releaseReply.put("identity", identity);
        releaseReply.put("identityType", identityType);
        releaseReply.put("status", status);
        releaseReply.put("serverName", serverId);
        return releaseReply;
    }

    public static JSONObject identityReserveReply(boolean success, String identity, String identityType, String serverId) {
        JSONObject reserveReply = new JSONObject();
        String status = (success) ? "success" : "failure";
        reserveReply.put("type", "identity_reserve_response");
        reserveReply.put("identity", identity);
        reserveReply.put("identityType", identityType);
        reserveReply.put("status", status);
        reserveReply.put("serverName", serverId);
        return reserveReply;
    }

    public static JSONObject roomIdInfoReply(String serverId, String roomOwnedServer) {
        JSONObject roomInfoReply = new JSONObject();
        roomInfoReply.put("type", "room_info_response");
        roomInfoReply.put("serverName", serverId);
        roomInfoReply.put("roomOwnedServerName", roomOwnedServer);
        return roomInfoReply;
    }
}
