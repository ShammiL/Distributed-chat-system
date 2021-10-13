package org.example.models.requests;

public class MessageRequest extends AbstractRequest {

    private String content;

    public MessageRequest(String content) {
        super(RequestConstants.MESSAGE);
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
