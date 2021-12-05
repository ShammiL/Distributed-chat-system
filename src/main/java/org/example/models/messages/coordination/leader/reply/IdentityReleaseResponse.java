package org.example.models.messages.coordination.leader.reply;

public class IdentityReleaseResponse extends AbstractIdentityResponse {
    public IdentityReleaseResponse(String identity, String identityType, String status, String serverName) {
        super("identity_release_response", identity, identityType, status, serverName);
    }
}
