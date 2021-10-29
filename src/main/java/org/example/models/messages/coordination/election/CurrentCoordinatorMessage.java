package org.example.models.messages.coordination.election;

import com.google.gson.annotations.SerializedName;
import org.example.models.messages.coordination.AbstractCoordinationMessage;

public class CurrentCoordinatorMessage extends AbstractCoordinationMessage {

    @SerializedName("coordinatorId")
    private String coordinatorId;
    public CurrentCoordinatorMessage(String serverName) {
        super("currentcoordinator", serverName);
    }

    public String getCoordinatorId() {
        return coordinatorId;
    }

    public void setCoordinatorId(String coordinatorId) {
        this.coordinatorId = coordinatorId;
    }
}
