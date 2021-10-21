package org.example.models.messages.coordination.leader.request;

public class IdentityReleaseRequest extends AbstractIdentityRequest {

    public IdentityReleaseRequest(String identity, String identityType, String serverName) {
        super("identity_release_request", identity, identityType, serverName);
    }
}
