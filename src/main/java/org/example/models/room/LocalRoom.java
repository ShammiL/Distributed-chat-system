package org.example.models.room;

public class LocalRoom extends Room{
    private String owner;

    public LocalRoom(String roomId, String owner) {
        super(roomId);
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
