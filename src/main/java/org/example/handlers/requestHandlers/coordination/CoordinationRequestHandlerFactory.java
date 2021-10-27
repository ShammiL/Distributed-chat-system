package org.example.handlers.requestHandlers.coordination;

import org.example.models.messages.AbstractMessage;
import org.example.models.messages.coordination.election.CoordinatorInformationMessage;
import org.example.models.messages.coordination.election.ElectionAnswerMessage;
import org.example.models.messages.coordination.election.ElectionCoordinatorMessage;
import org.example.models.messages.coordination.election.ElectionStartMessage;
import org.example.models.messages.coordination.heartbeat.HeartbeatMessage;
import org.example.models.messages.coordination.leader.request.GlobalRoomListRequest;
import org.example.models.messages.coordination.leader.request.IdentityReleaseRequest;
import org.example.models.messages.coordination.leader.request.IdentityReserveRequest;
import org.example.models.messages.coordination.leader.request.RoomInfoRequest;
import org.example.models.server.ServerInfo;

public class CoordinationRequestHandlerFactory {

    public static AbstractCoordinationRequestHandler requestHandler(AbstractMessage request, ServerInfo server){
        if (request instanceof ElectionAnswerMessage) {
            return new ElectionAnswerMessageHandler(server);
        }
        if (request instanceof ElectionCoordinatorMessage){
            return new ElectionCoordinatorMessageHandler(server);
        }
        if (request instanceof ElectionStartMessage){
            return new ElectionStartMessageHandler(server);
        }
        if (request instanceof HeartbeatMessage){
            return new HeartbeatMessageHandler(server);
        }
        if (request instanceof IdentityReleaseRequest){
            return new IdentityReleaseRequestHandler(server, (IdentityReleaseRequest) request);
        }
        if (request instanceof IdentityReserveRequest){
            return new IdentityReserveRequestHandler(server, (IdentityReserveRequest) request);
        }
        if (request instanceof CoordinatorInformationMessage) {
            return new CoordinatorInformationMessageHandler(server, (CoordinatorInformationMessage) request);
        }
        if (request instanceof GlobalRoomListRequest){
            return new GlobalRoomListHandler(server, (GlobalRoomListRequest) request);
        }
        if (request instanceof RoomInfoRequest){
            return new RoomInfoHandler(server, (RoomInfoRequest) request);
        }
        return null;
    }
}
