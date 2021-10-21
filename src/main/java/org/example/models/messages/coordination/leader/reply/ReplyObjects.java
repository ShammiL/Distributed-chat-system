package org.example.models.messages.coordination.leader.reply;

import org.json.simple.JSONObject;

public final class ReplyObjects {

    private ReplyObjects() {}

    public static JSONObject identityReleaseReply(boolean success, String identity, String identityType) {
        JSONObject releaseReply = new JSONObject();
        String status = (success) ? "success" : "failure";
        releaseReply.put("type", "identity_release_response");
        releaseReply.put("identity", identity);
        releaseReply.put("identityType", identityType);
        releaseReply.put("status", status);
        return releaseReply;
    }

    public static JSONObject identityReserveReply(boolean success, String identity, String identityType) {
        JSONObject reserveReply = new JSONObject();
        String status = (success) ? "success" : "failure";
        reserveReply.put("type", "identity_reserve_response");
        reserveReply.put("identity", identity);
        reserveReply.put("identityType", identityType);
        reserveReply.put("status", status);
        return reserveReply;
    }
}
