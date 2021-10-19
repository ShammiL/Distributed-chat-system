package org.example.handlers.requestHandlers;

import org.example.models.client.Client;
import org.example.models.client.IClient;
import org.example.models.reply.ReplyObjects;
import org.example.models.requests.AbstractRequest;
import org.example.models.requests.MessageRequest;
import org.example.models.requests.NewIdentityRequest;
import org.json.simple.JSONObject;

public class MessageRequestHandler extends AbstractRequestHandler{
    private MessageRequest request;

    public MessageRequestHandler(AbstractRequest request, IClient client) {
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
