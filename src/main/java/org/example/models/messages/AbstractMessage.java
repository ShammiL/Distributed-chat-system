package org.example.models.messages;

public abstract class AbstractMessage {

    private String type;

    public AbstractMessage(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
