package org.example.services.coordination.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.example.models.messages.coordination.AbstractCoordinationMessage;
import org.example.models.messages.coordination.election.ElectionAnswerMessage;
import org.example.models.messages.coordination.election.ElectionCoordinatorMessage;
import org.example.models.messages.coordination.election.ElectionStartMessage;
import org.example.models.messages.coordination.heartbeat.HeartbeatMessage;
import org.example.models.messages.coordination.leader.request.IdentityReleaseRequest;
import org.example.models.messages.coordination.leader.request.IdentityReserveRequest;

import java.lang.reflect.Type;

public class CoordinationRequestSerializer implements JsonSerializer<AbstractCoordinationMessage> {

    @Override
    public JsonElement serialize(AbstractCoordinationMessage message, Type type, JsonSerializationContext jsonSerializationContext) {

        if (message instanceof ElectionAnswerMessage) {
           return jsonSerializationContext.serialize(message, ElectionStartMessage.class);
        }
        if (message instanceof ElectionCoordinatorMessage){
            return jsonSerializationContext.serialize(message, ElectionCoordinatorMessage.class);
        }
        if (message instanceof ElectionStartMessage){
            return jsonSerializationContext.serialize(message, ElectionStartMessage.class);
        }
        if (message instanceof HeartbeatMessage){
            return jsonSerializationContext.serialize(message, HeartbeatMessage.class);
        }
        if (message instanceof IdentityReleaseRequest){
            return jsonSerializationContext.serialize(message, IdentityReleaseRequest.class);
        }
        if (message instanceof IdentityReserveRequest){
            return jsonSerializationContext.serialize(message, IdentityReserveRequest.class);
        }
        // Todo: Handle unknown request
        return jsonSerializationContext.serialize("", String.class);
    }
}
