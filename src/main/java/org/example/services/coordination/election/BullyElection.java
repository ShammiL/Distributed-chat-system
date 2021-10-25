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
        if (ServerState.getInstance().getServerInfo().equals(newCoordinator)){ // check if server is the leader

            if (!newCoordinator.equals(ServerState.getInstance().getCoordinator())){ // check previously not the leader
                System.out.println("I am the leader");
                ServerState.getInstance().setCoordinator(newCoordinator);
                LeaderState.getInstance().assignOwnLists(); // assign own list by newly appointed leader

            }

        }
        else { // not leader in this election
            if (ServerState.getInstance().getServerInfo().equals(ServerState.getInstance().getCoordinator())){
                // check the leader in the previous election
                LeaderState.destroyLeaderInstance(); //
            }
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

}
