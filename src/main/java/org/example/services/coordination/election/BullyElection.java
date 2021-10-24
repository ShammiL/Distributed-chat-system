package org.example.services.coordination.election;

import org.example.models.server.LeaderState;
import org.example.models.server.ServerInfo;
import org.example.models.server.ServerState;
import org.example.services.coordination.MessageSender;

import java.net.ConnectException;
import java.util.List;

public class BullyElection {

    public void startElection(List<ServerInfo> higherServerList){
        System.out.println("startElection");
        int notAlive = 0;
        for (ServerInfo higherServer : higherServerList) {
            System.out.println("higher server -> " + higherServer.getServerId());
            try {
                sendElectionStartMessage(higherServer);
            } catch (ConnectException | InterruptedException e) {
                System.out.println("higher server " + higherServer.getServerId() +" not alive");
                notAlive++;
//                e.printStackTrace();
            }

        }
        if (notAlive == higherServerList.size() && higherServerList.size() != 0) {
            informAndSetNewCoordinator(ServerState.getInstance().getLowerServerInfo());
        }
    }

    private void sendElectionStartMessage(ServerInfo higherServer) throws InterruptedException, ConnectException {
        MessageSender.sendElectionStartMessage(higherServer);
    }

    public void replyAnswerMessage(ServerInfo requestingServer){
        System.out.println("replyAnswerMessage to " + requestingServer.getServerId());
        try{
            MessageSender.sendElectionAnswerMessage(requestingServer);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void informAndSetNewCoordinator(List<ServerInfo> lowerServerList) {
        // inform lower priority servers about the new coordinator and set coordinator
        System.out.println("inform lower priority servers about the new coordinator and set coordinator");

        for(ServerInfo lowerServer : lowerServerList){
            System.out.println(" lower servers -> "+ lowerServer.getServerId());
            try {
                sendElectionCoordinatorMessage(lowerServer);
            } catch (ConnectException | InterruptedException e) {
                System.out.println("lower server " + lowerServer.getServerId() +" not alive");
//                e.printStackTrace();
            }
        }
        setNewCoordinator(ServerState.getInstance().getServerInfo());

    }

    private void sendElectionCoordinatorMessage(ServerInfo serverInfo) throws InterruptedException, ConnectException {
        MessageSender.sendElectionCoordinatorMessage(serverInfo);
    }

    public void sendCoordinatorInformationMessage(ServerInfo serverInfo) {
        try {
            MessageSender.sendCoordinatorInformationMessage(serverInfo);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    public void setNewCoordinator(ServerInfo newCoordinator){
        if(ServerState.getInstance().getCoordinator() == null){
            System.out.println("setNewCoordinator method when null");
            ServerState.getInstance().setCoordinator(newCoordinator);

        } else{
            if (!ServerState.getInstance().getCoordinator().equals(newCoordinator)){
                System.out.println("setNewCoordinator method");
                ServerState.getInstance().setCoordinator(newCoordinator);
            }
        }
    }

}
