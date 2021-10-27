package org.example.handlers.requestHandlers.chat;

import org.apache.log4j.Logger;
import org.example.models.client.Client;
import org.example.models.client.IClient;
import org.example.models.messages.chat.AbstractChatRequest;
import org.example.models.messages.chat.requests.chat.ListRequest;
import org.example.models.server.ServerState;
import org.example.services.coordination.MessageSender;
import org.json.simple.JSONObject;

import java.net.ConnectException;

public class ListRequestHandler extends AbstractRequestHandler {
    private final ListRequest request;
    private final Logger logger = Logger.getLogger(ListRequestHandler.class);

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
        try {
            sendRequestToLeader();
        } catch (InterruptedException | ConnectException e) {
            // todo: start election and resend request
            logger.error("Error when getting lists from the coordinator " + e.getMessage());
        }
    }

    private void sendRequestToLeader() throws InterruptedException, ConnectException {
        JSONObject response = null;

        response = MessageSender.requestRoomList(
                ServerState.getInstance().getCoordinator()
        );
        System.out.println(response.toString());
    }
}
