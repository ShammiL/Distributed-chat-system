package org.example.services.coordination.deserializers;

import com.google.gson.*;
import org.example.models.messages.coordination.AbstractCoordinationMessage;
import org.example.models.messages.coordination.election.ElectionAnswerMessage;
import org.example.models.messages.coordination.election.ElectionCoordinatorMessage;
import org.example.models.messages.coordination.election.ElectionStartMessage;
import org.example.models.messages.coordination.heartbeat.HeartbeatMessage;
import org.example.models.messages.coordination.leader.request.IdentityReleaseRequest;
import org.example.models.messages.coordination.leader.request.IdentityReserveRequest;

import java.lang.reflect.Type;

public class CoordinationRequestDeserializer implements JsonDeserializer<AbstractCoordinationMessage> {

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
                request = jsonDeserializationContext.deserialize(jsonElement, IdentityReleaseRequest.class);
                break;
            case "identity_reserve_request":
                request = jsonDeserializationContext.deserialize(jsonElement, IdentityReserveRequest.class);
                break;
            default:
                throw new JsonParseException("Unexpected coordination message type");
        }
        return request;
    }
}