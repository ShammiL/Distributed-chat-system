package org.example.services.coordination.deserializers;

import com.google.gson.*;
import org.example.models.messages.coordination.AbstractCoordinationMessage;
import org.example.models.messages.coordination.election.CoordinatorInformationMessage;
import org.example.models.messages.coordination.election.ElectionAnswerMessage;
import org.example.models.messages.coordination.election.ElectionCoordinatorMessage;
import org.example.models.messages.coordination.election.ElectionStartMessage;
import org.example.models.messages.coordination.heartbeat.HeartbeatMessage;
import org.example.models.messages.coordination.leader.reply.GlobalRoomResponse;
import org.example.models.messages.coordination.leader.reply.IdentityReleaseResponse;
import org.example.models.messages.coordination.leader.reply.IdentityReserveResponse;
import org.example.models.messages.coordination.leader.reply.RoomInfoResponse;
import org.example.models.messages.coordination.leader.request.GlobalRoomListRequest;
import org.example.models.messages.coordination.leader.request.IdentityReleaseRequest;
import org.example.models.messages.coordination.leader.request.IdentityReserveRequest;
import org.example.models.messages.coordination.leader.request.RoomInfoRequest;

import java.lang.reflect.Type;

public class CoordinationMessageDeserializer implements JsonDeserializer<AbstractCoordinationMessage> {

    @Override
    public AbstractCoordinationMessage deserialize(JsonElement jsonElement, Type type,
                                                   JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {

        final JsonObject requestJson = jsonElement.getAsJsonObject();
        AbstractCoordinationMessage request;
        switch (requestJson.get("type").getAsString()) {
            case "electionanswer":
                request = jsonDeserializationContext.deserialize(jsonElement, ElectionAnswerMessage.class);
                break;
            case "electioncoordinator":
                request = jsonDeserializationContext.deserialize(jsonElement, ElectionCoordinatorMessage.class);
                break;
            case "electionstart":
                request = jsonDeserializationContext.deserialize(jsonElement, ElectionStartMessage.class);
                break;
            case "heartbeat":
                request = jsonDeserializationContext.deserialize(jsonElement, HeartbeatMessage.class);
                break;
            case "identity_release_request":
//                request = new IdentityReleaseRequest(
//                        requestJson.get("identity").getAsString(),
//                        requestJson.get("identityType").getAsString(),
//                        requestJson.get("serverName").getAsString()
//                );
                request = jsonDeserializationContext.deserialize(jsonElement, IdentityReleaseRequest.class);
                break;
            case "identity_reserve_request":
//                request = new IdentityReserveRequest(
//                        requestJson.get("identity").getAsString(),
//                        requestJson.get("identityType").getAsString(),
//                        requestJson.get("serverName").getAsString()
//                );
                request = jsonDeserializationContext.deserialize(jsonElement, IdentityReserveRequest.class);
                break;
            case "identity_reserve_response":
                request = new IdentityReserveResponse(
                        requestJson.get("identity").getAsString(),
                        requestJson.get("identityType").getAsString(),
                        requestJson.get("status").getAsString(),
                        requestJson.get("serverName").getAsString()
                );
                break;
            case "identity_release_response":
                request = new IdentityReleaseResponse(
                        requestJson.get("identity").getAsString(),
                        requestJson.get("identityType").getAsString(),
                        requestJson.get("status").getAsString(),
                        requestJson.get("serverName").getAsString()
                );
                break;
            case "coordinatorinformation":
                request = jsonDeserializationContext.deserialize(jsonElement, CoordinatorInformationMessage.class);
                break;
            case "room_list":
                request = jsonDeserializationContext.deserialize(jsonElement, GlobalRoomListRequest.class);
                break;
            case "room_list_response":
                request = jsonDeserializationContext.deserialize(jsonElement, GlobalRoomResponse.class);
                break;
            case "room_info_request":
                request = jsonDeserializationContext.deserialize(jsonElement, RoomInfoRequest.class);
                break;
            case "room_info_response":
                request = jsonDeserializationContext.deserialize(jsonElement, RoomInfoResponse.class);
                break;
            default:
                throw new JsonParseException("Unexpected coordination message type");
        }
        return request;
    }
}