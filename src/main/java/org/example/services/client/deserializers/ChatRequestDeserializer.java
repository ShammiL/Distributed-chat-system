package org.example.services.client.deserializers;

import com.google.gson.*;
import org.example.models.messages.chat.AbstractChatRequest;
import org.example.models.messages.chat.requests.*;
import org.example.models.messages.chat.requests.chat.*;

import java.lang.reflect.Type;

public class ChatRequestDeserializer implements JsonDeserializer<AbstractChatRequest> {

    @Override
    public AbstractChatRequest deserialize(JsonElement jsonElement, Type type,
                                           JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {

        final JsonObject requestJson = jsonElement.getAsJsonObject();

        AbstractChatRequest request;

        switch (requestJson.get("type").getAsString()) {
            case RequestConstants.CREATE_ROOM:
                request = new CreateRoomRequest(requestJson.get("roomid").getAsString());
                break;
            case RequestConstants.DELETE_ROOM:
                request = new DeleteRoomRequest(requestJson.get("roomid").getAsString());
                break;
            case RequestConstants.JOIN_ROOM:
                request = new JoinRoomRequest(requestJson.get("roomid").getAsString());
                break;
            case RequestConstants.MOVE_JOIN:
                request = new MoveJoinRequest(
                        requestJson.get("former").getAsString(),
                        requestJson.get("identity").getAsString(),
                        requestJson.get("roomid").getAsString()
                );
                break;
            case RequestConstants.LIST:
                request = new ListRequest();
                break;
            case RequestConstants.MESSAGE:
                request = new MessageRequest(requestJson.get("content").getAsString());
                break;
            case RequestConstants.NEW_IDENTITY:
                request = new NewIdentityRequest(requestJson.get("identity").getAsString());
                break;
            case RequestConstants.QUIT:
                request = new QuitRequest();
                break;
            case RequestConstants.WHO:
                request = new WhoRequest();
                break;
            default:
                throw new JsonParseException("Unexpected request type");
        }
        return request;
    }
}
