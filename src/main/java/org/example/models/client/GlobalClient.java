package org.example.models.client;

public class GlobalClient implements IClient {
    private String identity = "";
    private String serverId;
    private boolean isAccepted = false;

    public GlobalClient(String identity, String serverId) {
        this.identity = identity;
        this.serverId = serverId;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }
}
