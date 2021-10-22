package org.example.services.coordination.deserializers;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import junit.framework.TestCase;
import org.example.models.messages.coordination.AbstractCoordinationMessage;
import org.example.models.messages.coordination.heartbeat.HeartbeatMessage;
import org.example.models.messages.coordination.leader.request.IdentityReleaseRequest;
import org.example.models.messages.coordination.leader.request.IdentityReserveRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class CoordinationRequestDeserializerTest extends TestCase {
    private Gson gson;
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        gsonBuilder.registerTypeAdapter(AbstractCoordinationMessage.class, new CoordinationRequestDeserializer());
        gson = gsonBuilder.create();
    }

    @Test
    public void heartbeatTest() {
        HeartbeatMessage msg = (HeartbeatMessage) gson.fromJson("{\"serverName\":\"s1\",\"type\":\"heartbeat\"}", AbstractCoordinationMessage.class);
        assertEquals(msg.getServerName(), "s1");
        assertEquals(msg.getType(), "heartbeat");
    }

    @Test
    public void identityReserveRequestTest() {
        IdentityReserveRequest msg = (IdentityReserveRequest) gson.fromJson("{\"serverName\" : \"s1\", \"type\"" +
                ": \"identity_reserve_request\", \"identity\": \"test\", \"identityType\": \"room\"}",
                AbstractCoordinationMessage.class
        );

        assertEquals(msg.getServerName(), "s1");
        assertEquals(msg.getType(), "identity_reserve_request");
        assertEquals(msg.getIdentity(), "test");
        assertEquals(msg.getIdentityType(), "room");
    }

    @Test
    public void identityReleaseRequestTest() {
        IdentityReleaseRequest msg = (IdentityReleaseRequest) gson.fromJson("{\"serverName\" : \"s1\", \"type\"" +
                        ": \"identity_release_request\", \"identity\": \"test\", \"identityType\": \"room\"}",
                AbstractCoordinationMessage.class
        );

        assertEquals(msg.getServerName(), "s1");
        assertEquals(msg.getType(), "identity_release_request");
        assertEquals(msg.getIdentity(), "test");
        assertEquals(msg.getIdentityType(), "room");
    }
}