package org.example.handlers.requestHandlers.chat;

import org.example.models.client.Client;
import org.example.models.client.IClient;
import org.example.models.messages.chat.AbstractChatRequest;
import org.example.models.messages.chat.requests.chat.ListRequest;
import org.example.models.server.ServerState;
import org.example.services.coordination.MessageSender;
import org.json.simple.JSONObject;

public class ListRequestHandler extends AbstractRequestHandler {
    private final ListRequest request;

    public ListRequestHandler(AbstractChatRequest request, IClient client) {
        super((Client) client);
        this.request = (ListRequest) request;
    }

    @Override
    public JSONObject processRequest() {
        return null;
    }

    @Override
    public void handleRequest() {
        System.out.println("Client list request handler");
        JSONObject response = null;
        try {
            response = MessageSender.requestRoomList(
                    ServerState.getInstance().getCoordinator()
            );
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("error in handle request");
        }
        System.out.println(response.toString());
    }
}
