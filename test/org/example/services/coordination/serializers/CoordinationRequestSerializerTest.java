package org.example.services.coordination.serializers;


import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import junit.framework.TestCase;
import org.example.models.messages.coordination.AbstractCoordinationMessage;
import org.example.models.messages.coordination.heartbeat.HeartbeatMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class CoordinationRequestSerializerTest extends TestCase {
    private Gson gson;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        gsonBuilder.registerTypeAdapter(AbstractCoordinationMessage.class, new CoordinationRequestSerializer());
        gson = gsonBuilder.create();
    }

    @Test
    public void heartbeatTest() {
        String json = gson.toJson(new HeartbeatMessage("s1"));
        assertEquals(json,"{\"serverName\":\"s1\",\"type\":\"heartbeat\"}");
    }
}