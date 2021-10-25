package org.example.handlers.requestHandlers.chat;

import org.example.models.client.IClient;
import org.example.models.messages.chat.AbstractChatRequest;
import org.example.models.messages.chat.requests.chat.*;

public class RequestHandlerFactory {

    public static AbstractRequestHandler requestHandler(AbstractChatRequest request, IClient client){
        if (request instanceof NewIdentityRequest) {
            return new NewIdentityRequestHandler(request, client);
        }
        if (request instanceof CreateRoomRequest){
            return new CreateRoomRequestHandler(request,client);
        }
        if (request instanceof DeleteRoomRequest){
            return new DeleteRoomRequestHandler(request, client);
        }
        if (request instanceof JoinRoomRequest){
            return new JoinRoomRequestHandler(request, client);
        }
        if (request instanceof MoveJoinRequest){
            return null;
        }
        if (request instanceof ListRequest){
            return null;
        }
        if (request instanceof WhoRequest) {
            return null;
        }
        if (request instanceof MessageRequest){
            return new MessageRequestHandler(request, client);
        }
        if (request instanceof QuitRequest) {
            return new QuitRequestHandler(request, client);
        }
        return null;
    }
}
