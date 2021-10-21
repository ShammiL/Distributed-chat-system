package org.example.services.client.deserializers;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import junit.framework.TestCase;
import org.example.models.messages.chat.AbstractChatRequest;
import org.example.models.messages.chat.requests.RequestConstants;
import org.example.models.messages.chat.requests.chat.*;
import org.junit.Test;

public class RequestDeserializerTest extends TestCase {

    private static final String NEW_IDENTITY_JSON = "{\"type\" : \"newidentity\", \"identity\" : \"Adel\"}";
    private static final String LIST_JSON = "{\"type\" : \"list\"}";
    private static final String WHO_JSON = "{\"type\" : \"who\"}";
    private static final String CREATE_ROOM_JSON = "{\"type\" : \"createroom\", \"roomid\" : \"jokes\"}";
    private static final String JOIN_ROOM_JSON = "{\"type\" : \"joinroom\", \"roomid\" : \"jokes\"}";
    private static final String MOVE_JOIN_JSON = "{\"type\" : \"movejoin\", \"former\" : \"MainHall-s1\", \"roomid\"" +
            " : \"jokes\", \"identity\" : \"Maria\"}";
    private static final String DELETE_ROOM_JSON = "{\"type\" : \"deleteroom\", \"roomid\" : \"jokes\"}";
    private static final String MESSAGE_JSON = "{\"type\" : \"message\", \"content\" : \"Hi there!\"}";
    private static final String QUIT_JSON = "{\"type\":\"quit\"}";

    private Gson gson;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        gsonBuilder.registerTypeAdapter(AbstractChatRequest.class, new ChatRequestDeserializer());
        gson = gsonBuilder.create();
    }

    @Test
    public void testNewIdentifier() {
        NewIdentityRequest request = (NewIdentityRequest) gson.fromJson(NEW_IDENTITY_JSON, AbstractChatRequest.class);
        assertEquals("Adel", request.getIdentity());
        assertEquals(RequestConstants.NEW_IDENTITY, request.getType());
    }

    @Test
    public void testList() {
        ListRequest request = (ListRequest) gson.fromJson(LIST_JSON, AbstractChatRequest.class);
        assertEquals(RequestConstants.LIST, request.getType());
    }

    @Test
    public void testWho() {
        WhoRequest request = (WhoRequest) gson.fromJson(WHO_JSON, AbstractChatRequest.class);
        assertEquals(RequestConstants.WHO, request.getType());
    }

    public void testCreateRoom() {
        CreateRoomRequest request = (CreateRoomRequest) gson.fromJson(CREATE_ROOM_JSON, AbstractChatRequest.class);
        assertEquals(RequestConstants.CREATE_ROOM, request.getType());
        assertEquals("jokes", request.getRoomId());
    }

    @Test
    public void testJoinRoom() {
        JoinRoomRequest request = (JoinRoomRequest) gson.fromJson(JOIN_ROOM_JSON, AbstractChatRequest.class);
        assertEquals(RequestConstants.JOIN_ROOM, request.getType());
        assertEquals(request.getRoomId(), "jokes");
    }

    @Test
    public void testMoveJoin() {
        MoveJoinRequest request = (MoveJoinRequest) gson.fromJson(MOVE_JOIN_JSON, AbstractChatRequest.class);
        assertEquals(RequestConstants.MOVE_JOIN, request.getType());
        assertEquals("MainHall-s1", request.getFormer());
        assertEquals("jokes", request.getRoomId());
        assertEquals("Maria", request.getIdentity());
    }

    @Test
    public void testDeleteRoom() {
        DeleteRoomRequest request = (DeleteRoomRequest) gson.fromJson(DELETE_ROOM_JSON, AbstractChatRequest.class);
        assertEquals(request.getType(), RequestConstants.DELETE_ROOM);
        assertEquals("jokes", request.getRoomId());
    }

    @Test
    public void testMessage() {
        MessageRequest request = (MessageRequest) gson.fromJson(MESSAGE_JSON, AbstractChatRequest.class);
        assertEquals(RequestConstants.MESSAGE, request.getType());
        assertEquals("Hi there!", request.getContent());
    }

    @Test
    public void testQuit() {
        QuitRequest request = (QuitRequest) gson.fromJson(QUIT_JSON, AbstractChatRequest.class);
        assertEquals(RequestConstants.QUIT, request.getType());
    }
}

