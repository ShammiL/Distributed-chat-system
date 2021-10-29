package org.example.services.coordination;

import org.example.config.Config;
import org.example.models.messages.coordination.AbstractCoordinationMessage;
import org.example.models.messages.coordination.election.*;
import org.example.models.messages.coordination.heartbeat.HeartbeatMessage;
import org.example.models.messages.coordination.leader.request.GlobalRoomListRequest;
import org.example.models.messages.coordination.leader.request.IdentityReleaseRequest;
import org.example.models.messages.coordination.leader.request.IdentityReserveRequest;
import org.example.models.messages.coordination.leader.request.RoomInfoRequest;
import org.example.models.server.ServerInfo;
import org.example.models.server.ServerState;
import org.example.services.coordination.client.CoordinationClient;
import org.example.services.coordination.election.BullyElection;
import org.json.simple.JSONObject;

import java.net.ConnectException;

public final class MessageSender {

    private MessageSender() {
    }

    private static Runnable getRetryRunnable(ServerInfo server, AbstractCoordinationMessage message, Runnable success,
                                             int tries) {
        return () -> {
            try {
                Thread.sleep(Config.COORDINATION_MESSAGE_RETRY_INTERVAL);
            } catch (InterruptedException ignored) {
            }

            if (tries <= Config.COORDINATION_MESSAGE_RETRIES) {
                try {
                    if (message instanceof ElectionAnswerMessage) {
                        if (BullyElection.getInstance().getWaitingForElectionAnswerMsg()) {
                            sendElectionAnswerMessage(server, tries + 1);
                        }
                    } else if(message instanceof ElectionCoordinatorMessage) {
                        if (BullyElection.getInstance().getWaitingForElectionCoordinatorMsg()) {
                            sendElectionCoordinatorMessage(server, tries + 1);
                        }
                    } else if (message instanceof CoordinatorInformationMessage) {
                        sendCoordinatorInformationMessage(server, tries+1);
                    } else if (message instanceof ElectionStartMessage) {
                        if (BullyElection.getInstance().getContinueElection()) {
                            sendElectionStartMessage(server, success, tries + 1);
                        }
                    }
                } catch (InterruptedException | ConnectException ignored) {
                }
            }
        };
    }

    public static void sendElectionAnswerMessage(ServerInfo server) throws InterruptedException, ConnectException {
        sendElectionAnswerMessage(server, 1);
    }

    public static void sendElectionAnswerMessage(ServerInfo server, int tries) throws InterruptedException, ConnectException {
        CoordinationClient client = new CoordinationClient(server.getServerAddress(), server.getCoordinationPort(), true);
        AbstractCoordinationMessage message = new ElectionAnswerMessage(ServerState.getInstance().getServerInfo()
                .getServerId());
        client.sendMessageAndGetStatus(message, null, getRetryRunnable(server, message, null, tries));
    }

    public static void sendElectionCoordinatorMessage(ServerInfo server) throws InterruptedException, ConnectException {
        sendElectionCoordinatorMessage(server, 1);
    }

    public static void sendElectionCoordinatorMessage(ServerInfo server, int tries) throws InterruptedException, ConnectException {
        CoordinationClient client = new CoordinationClient(server.getServerAddress(), server.getCoordinationPort(), true);
        AbstractCoordinationMessage message = new ElectionCoordinatorMessage(ServerState.getInstance().getServerInfo()
                .getServerId());
        client.sendMessageAndGetStatus(message, null, getRetryRunnable(server, message, null, tries));
    }

    public static void sendElectionStartMessage(ServerInfo server, Runnable success) throws InterruptedException, ConnectException {
        sendElectionStartMessage(server, success, 1);
    }
    public static void sendElectionStartMessage(ServerInfo server, Runnable success, int tries) throws InterruptedException, ConnectException {
        CoordinationClient client = new CoordinationClient(server.getServerAddress(), server.getCoordinationPort(), true);
        AbstractCoordinationMessage message = new ElectionStartMessage(ServerState.getInstance().getServerInfo()
                .getServerId());
        client.sendMessageAndGetStatus( message, success, getRetryRunnable(server, message, success, tries));
    }

    public static void sendCoordinatorInformationMessage(ServerInfo server) throws InterruptedException, ConnectException {
        sendCoordinatorInformationMessage(server, 1);
    }
    public static void sendCoordinatorInformationMessage(ServerInfo server, int tries) throws InterruptedException, ConnectException {
        CoordinationClient client = new CoordinationClient(server.getServerAddress(), server.getCoordinationPort());
        AbstractCoordinationMessage message = new CoordinatorInformationMessage(ServerState.getInstance().getServerInfo()
                .getServerId());
        client.sendMessageAndGetStatus(message, null, getRetryRunnable(server, message, null, tries));
    }

    public static void sendHeartBeatMessage(ServerInfo server, Runnable success, Runnable failure) throws InterruptedException, ConnectException {
        CoordinationClient client = new CoordinationClient(server.getServerAddress(), server.getCoordinationPort(), true);
        client.sendMessageAndGetStatus(new HeartbeatMessage(ServerState.getInstance().getServerInfo().getServerId()), success, failure);
    }

    public static JSONObject reserveIdentity(ServerInfo server, String identity, String identityType) throws InterruptedException, ConnectException {
        CoordinationClient client = new CoordinationClient(server.getServerAddress(), server.getCoordinationPort());
        JSONObject response = client.sendMessageAndGetStatus(new IdentityReserveRequest(
                identity,
                identityType, ServerState.getInstance().getServerInfo().getServerId()));
        return response;
    }

    public static JSONObject releaseIdentity(ServerInfo server, String identity, String identityType) throws InterruptedException, ConnectException {
        CoordinationClient client = new CoordinationClient(server.getServerAddress(), server.getCoordinationPort());
        JSONObject response = client.sendMessageAndGetStatus(new IdentityReleaseRequest(
                identity,
                identityType, ServerState.getInstance().getServerInfo().getServerId()));
        return response;
    }

    public static JSONObject requestRoomList(ServerInfo server) throws InterruptedException, ConnectException {
        CoordinationClient client = new CoordinationClient(server.getServerAddress(), server.getCoordinationPort());
        JSONObject response = client.sendMessageAndGetStatus(new GlobalRoomListRequest(ServerState.getInstance().getServerInfo().getServerId()));
        System.out.println(response.toString());
        System.out.println("Request room list");
        return response;
    }

    public static JSONObject requestRoomInfo(ServerInfo server, String roomId) throws InterruptedException, ConnectException {
        CoordinationClient client = new CoordinationClient(server.getServerAddress(), server.getCoordinationPort());
        JSONObject response = client.sendMessageAndGetStatus(new RoomInfoRequest(
                ServerState.getInstance().getServerInfo().getServerId(),
                roomId));
        System.out.println("Inside MessageSender : requestRoomInfo");
        return response;
    }

    public static void sendCurrentCoordinatorMessage(ServerInfo server) throws InterruptedException, ConnectException {
        CoordinationClient client = new CoordinationClient(server.getServerAddress(), server.getCoordinationPort(), true);
        client.sendMessageAndGetStatus(new CurrentCoordinatorMessage(
                ServerState.getInstance().getCoordinator().getServerId()
        ));
    }

}
