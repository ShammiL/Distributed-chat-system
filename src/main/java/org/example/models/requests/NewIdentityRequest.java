package org.example.models.requests;

public class NewIdentityRequest extends AbstractRequest {

    private String identity;

    public NewIdentityRequest(String identity) {
        super(RequestConstants.NEW_IDENTITY);
        this.identity = identity;
    }

    public String getIdentity() {
        return identity;
    }
}
