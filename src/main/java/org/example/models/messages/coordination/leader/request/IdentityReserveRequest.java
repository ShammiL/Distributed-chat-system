package org.example.models.messages.coordination.leader.request;

public class IdentityReserveRequest extends AbstractIdentityRequest {

    public IdentityReserveRequest(String identity, String identityType, String serverName) {
        super("identity_reserve_request", identity, identityType, serverName);
    }
}
