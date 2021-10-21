package org.example.handlers.requestHandlers;

import io.netty.channel.ChannelHandlerContext;
import org.example.models.client.IClient;
import org.example.models.requests.*;

public class RequestHandlerFactory {

    public static AbstractRequestHandler requestHandler(AbstractRequest request, IClient client){
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
        if (request instanceof MessageRequest){
            return new MessageRequestHandler(request, client);
        }
        if (request instanceof QuitRequest) {
            return new QuitRequestHandler(request, client);
        }
        if(request instanceof WhoRequest) {
            return new WhoRequestHandler(client);
        }
        return null;
    }
}
