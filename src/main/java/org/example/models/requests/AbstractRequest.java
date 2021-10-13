package org.example.models.requests;

public abstract class AbstractRequest {

    private String type;

    public AbstractRequest(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
