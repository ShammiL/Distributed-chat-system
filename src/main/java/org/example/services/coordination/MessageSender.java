package org.example.services.coordination;

import org.example.models.messages.coordination.election.CoordinatorInformationMessage;
import org.example.models.messages.coordination.election.ElectionAnswerMessage;
import org.example.models.messages.coordination.election.ElectionCoordinatorMessage;
import org.example.models.messages.coordination.election.ElectionStartMessage;
import org.example.models.messages.coordination.heartbeat.HeartbeatMessage;
import org.example.models.messages.coordination.leader.request.GlobalRoomListRequest;
import org.example.models.messages.coordination.leader.request.IdentityReleaseRequest;
import org.example.models.messages.coordination.leader.request.IdentityReserveRequest;
import org.example.models.messages.coordination.leader.request.RoomInfoRequest;
import org.example.models.server.ServerInfo;
import org.example.models.server.ServerState;
import org.example.services.coordination.client.CoordinationClient;
import org.json.simple.JSONObject;

import java.net.ConnectException;

public final class MessageSender {

    private MessageSender() {
    }

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

    public static void sendHeartBeatMessage(ServerInfo server, Runnable success, Runnable failure) throws InterruptedException, ConnectException {
        CoordinationClient client = new CoordinationClient(server.getServerAddress(), server.getCoordinationPort(), true);
        client.sendMessageAndGetStatus(new HeartbeatMessage(ServerState.getInstance().getServerInfo().getServerId()), success, failure);
    }

    public static JSONObject reserveIdentity(ServerInfo server, String identity, String identityType) throws InterruptedException {
        CoordinationClient client = new CoordinationClient(server.getServerAddress(), server.getCoordinationPort());
        JSONObject response = client.sendMessageAndGetStatus(new IdentityReserveRequest(
                identity,
                identityType, ServerState.getInstance().getServerInfo().getServerId()));
        return response;
    }

    public static JSONObject releaseIdentity(ServerInfo server, String identity, String identityType) throws InterruptedException {
        CoordinationClient client = new CoordinationClient(server.getServerAddress(), server.getCoordinationPort());
        JSONObject response = client.sendMessageAndGetStatus(new IdentityReleaseRequest(
                identity,
                identityType, ServerState.getInstance().getServerInfo().getServerId()));
        return response;
    }

    public static JSONObject requestRoomList(ServerInfo server) throws InterruptedException {
        CoordinationClient client = new CoordinationClient(server.getServerAddress(), server.getCoordinationPort());
        JSONObject response = client.sendMessageAndGetStatus(new GlobalRoomListRequest(ServerState.getInstance().getServerInfo().getServerId()));
        System.out.println(response.toString());
        System.out.println("Request room list");
        return response;
    }

    public static JSONObject requestRoomInfo(ServerInfo server, String roomId) throws InterruptedException {
        CoordinationClient client = new CoordinationClient(server.getServerAddress(), server.getCoordinationPort());
        JSONObject response = client.sendMessageAndGetStatus(new RoomInfoRequest(
                ServerState.getInstance().getServerInfo().getServerId(),
                roomId));
        System.out.println("Inside MessageSender : requestRoomInfo");
        return response;
    }

}
