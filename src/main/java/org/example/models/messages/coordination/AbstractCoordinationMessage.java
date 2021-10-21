package org.example.models.messages.coordination;

import com.google.gson.annotations.SerializedName;
import org.example.models.messages.AbstractMessage;

public abstract class AbstractCoordinationMessage extends AbstractMessage {

    @SerializedName("serverName")
    protected String serverName;
    public AbstractCoordinationMessage(String type, String serverName) {
        super(type);
        this.serverName = serverName;
    }

    public String getServerName() {
        return serverName;
    }
}
