package org.example.models.room;

public class GlobalRoom extends Room{
    private String serverId;
    private boolean isAccepted = false;

    public GlobalRoom(String roomId, String serverId) {
        super(roomId);
        this.serverId = serverId;
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
