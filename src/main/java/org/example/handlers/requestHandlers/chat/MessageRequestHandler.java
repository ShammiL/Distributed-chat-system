package org.example.handlers.requestHandlers.chat;

import org.example.models.client.Client;
import org.example.models.client.IClient;
import org.example.models.messages.chat.reply.ReplyObjects;
import org.example.models.messages.chat.AbstractChatRequest;
import org.example.models.messages.chat.requests.chat.MessageRequest;
import org.json.simple.JSONObject;

public class MessageRequestHandler extends AbstractRequestHandler{
    private final MessageRequest request;

    public MessageRequestHandler(AbstractChatRequest request, IClient client) {
        super((Client) client);
        this.request = (MessageRequest)  request;
    }


    @Override
    public JSONObject processRequest() {
        String identity = super.getClient().getIdentity();
        String content = request.getContent();
        return ReplyObjects.message(identity, content);
    }

    @Override
    public void handleRequest() {
        JSONObject msg = processRequest();
        broadcast(msg, getClient().getRoom());
    }
}
