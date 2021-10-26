package org.example.services.coordination;

import org.example.models.messages.coordination.election.CoordinatorInformationMessage;
import org.example.models.messages.coordination.election.ElectionAnswerMessage;
import org.example.models.messages.coordination.election.ElectionCoordinatorMessage;
import org.example.models.messages.coordination.election.ElectionStartMessage;
import org.example.models.messages.coordination.heartbeat.HeartbeatMessage;
import org.example.models.messages.coordination.leader.request.IdentityReleaseRequest;
import org.example.models.messages.coordination.leader.request.IdentityReserveRequest;
import org.example.models.server.ServerInfo;
import org.example.models.server.ServerState;
import org.example.services.coordination.client.CoordinationClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.concurrent.atomic.AtomicBoolean;

public final class MessageSender {

    private MessageSender(){}

    public static void sendElectionAnswerMessage(ServerInfo server) throws InterruptedException {
        CoordinationClient client = new CoordinationClient(server.getServerAddress(), server.getCoordinationPort());
        client.sendMessageAndGetStatus(new ElectionAnswerMessage(ServerState.getInstance().getServerInfo().getServerId()));
    }

    public static void sendElectionCoordinatorMessage(ServerInfo server) throws InterruptedException {
        CoordinationClient client = new CoordinationClient(server.getServerAddress(), server.getCoordinationPort());
        client.sendMessageAndGetStatus(new ElectionCoordinatorMessage(ServerState.getInstance().getServerInfo().getServerId()));

    }

    public static void sendElectionStartMessage(ServerInfo server) throws InterruptedException {
        CoordinationClient client = new CoordinationClient(server.getServerAddress(), server.getCoordinationPort());
        client.sendMessageAndGetStatus(new ElectionStartMessage(ServerState.getInstance().getServerInfo().getServerId()));

    }

    public static void sendCoordinatorInformationMessage(ServerInfo server) throws InterruptedException {
        CoordinationClient client = new CoordinationClient(server.getServerAddress(), server.getCoordinationPort());
        client.sendMessageAndGetStatus(new CoordinatorInformationMessage(ServerState.getInstance().getServerInfo().getServerId()));

    }

    public static void sendHeartBeatMessage(ServerInfo server) throws InterruptedException {
        CoordinationClient client = new CoordinationClient(server.getServerAddress(), server.getCoordinationPort());
        client.sendMessageAndGetStatus(new HeartbeatMessage(ServerState.getInstance().getServerInfo().getServerId()));

    }

    public static JSONObject reserveIdentity(ServerInfo server, String identity, String identityType) throws InterruptedException {
        CoordinationClient client = new CoordinationClient(server.getServerAddress(), server.getCoordinationPort());
        JSONObject response = client.sendMessageAndGetStatus(new IdentityReserveRequest(
                identity,
                identityType,ServerState.getInstance().getServerInfo().getServerId()));
        return response;
    }

    public static JSONObject releaseIdentity(ServerInfo server, String identity, String identityType) throws InterruptedException {
        CoordinationClient client = new CoordinationClient(server.getServerAddress(), server.getCoordinationPort());
        JSONObject response = client.sendMessageAndGetStatus(new IdentityReleaseRequest(
                identity,
                identityType,ServerState.getInstance().getServerInfo().getServerId()));
        return response;
    }

    public static JSONObject requestRoomList(ServerInfo server) throws InterruptedException {
        return null;
    }

}
