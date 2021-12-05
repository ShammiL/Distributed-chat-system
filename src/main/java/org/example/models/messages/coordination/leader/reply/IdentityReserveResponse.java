package org.example.models.messages.coordination.leader.reply;

public class IdentityReserveResponse extends AbstractIdentityResponse {
    public IdentityReserveResponse(String identity, String identityType, String status, String serverName) {
        super("identity_reserve_response", identity, identityType, status, serverName);
    }
}
