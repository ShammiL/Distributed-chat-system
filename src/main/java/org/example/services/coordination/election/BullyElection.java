package org.example.services.coordination.election;

import org.example.handlers.requestHandlers.chat.RequestHandlerFactory;
import org.example.models.messages.chat.AbstractChatRequest;
import org.example.models.server.LeaderState;
import org.example.models.server.ServerInfo;
import org.example.models.server.ServerState;
import org.example.services.coordination.MessageSender;

import java.net.ConnectException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class BullyElection {

    public static final int ELECTION_START_TRIES = 3;
    public static final int ELECTION_START_RETRY_SLEEP = 200;
    public static final int ELECTION_START_TIMEOUT = 1000;
    public static final int ELECTION_ANSWER_MESSAGE_TIMEOUT = 2000;
    public static final int COORDINATOR_MESSAGE_TIMEOUT = 3000;
    public static final int HEARTBEAT_THRESHOLD = ELECTION_START_TIMEOUT;

    private AtomicBoolean waitingForElectionAnswerMsg = new AtomicBoolean(false);
    private AtomicBoolean waitingForElectionCoordinatorMsg = new AtomicBoolean(false);
    private AtomicBoolean continueElection = new AtomicBoolean(false);
    private AtomicLong lastHeartBeatTime = new AtomicLong(0);
    private ScheduledExecutorService electionAnswerMsgTimeOutExecutor;
    private ScheduledExecutorService coordinatorMsgTimeOutExecutor;
    private ScheduledExecutorService electionTimeoutExecutor;
    private ScheduledFuture<?> electionTimeoutFuture;

    private static BullyElection instance;

    public static BullyElection getInstance() {
        if (instance == null) {
            instance = new BullyElection();
        }
        return instance;
    }

    private BullyElection() {

    }

    public boolean getWaitingForElectionAnswerMsg() {
        return waitingForElectionAnswerMsg.get();
    }

    public synchronized void restartElectionTimeout() {

            endElectionTimeout();
            startElectionTimeout();
    }

    public synchronized void startElectionTimeout() {

            if (electionTimeoutExecutor == null) {
                electionTimeoutExecutor = Executors.newSingleThreadScheduledExecutor();
            }
            lastHeartBeatTime.set(System.currentTimeMillis());
            electionTimeoutFuture = electionTimeoutExecutor.schedule(() -> {
                if (System.currentTimeMillis() - lastHeartBeatTime.get() > HEARTBEAT_THRESHOLD) {
                    System.out.println("Starting election because leader didn't send heartbeat");
                    BullyElection.getInstance().startElection(
                            ServerState.getInstance().getHigherServerInfo()
                    );
                }
            }, ELECTION_START_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    public  synchronized void endElectionTimeout() {

            if (electionTimeoutFuture != null) {
                electionTimeoutFuture.cancel(true);
            }
            if (electionTimeoutExecutor != null) {
                electionTimeoutExecutor.shutdownNow();
                electionTimeoutExecutor = null;
            }
    }

    public synchronized void startElectionAnswerMsgTimeout() {

            if (electionAnswerMsgTimeOutExecutor == null) {
                electionAnswerMsgTimeOutExecutor = Executors.newSingleThreadScheduledExecutor();
            }
            System.out.println("Election Answer timeout started!");
            electionAnswerMsgTimeOutExecutor.schedule(() -> {
                System.out.println("No Answer Messages were received within timeout");
                informAndSetNewCoordinator(ServerState.getInstance().getLowerServerInfo());
                endElectionAnswerMsgTimeout();
            }, ELECTION_ANSWER_MESSAGE_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    public synchronized void endElectionAnswerMsgTimeout() {
            // Stop the scheduled task to set self as coordinator
            if (waitingForElectionAnswerMsg.get()) {
                waitingForElectionAnswerMsg.set(false);
                if (electionAnswerMsgTimeOutExecutor != null) {
                    electionAnswerMsgTimeOutExecutor.shutdownNow();
                }
                electionAnswerMsgTimeOutExecutor = null;
            }
    }

    public synchronized void endCoordinatorMessageTimeout() {
            if (waitingForElectionCoordinatorMsg.get()) {
                waitingForElectionCoordinatorMsg.set(false);
                if (coordinatorMsgTimeOutExecutor != null) {
                    coordinatorMsgTimeOutExecutor.shutdownNow();
                }
                coordinatorMsgTimeOutExecutor = null;
            }
            System.out.println("Coordinator message timeout ended");
    }

    public synchronized void startCoordinatorWaitTimeout() {
            if (coordinatorMsgTimeOutExecutor == null) {
                coordinatorMsgTimeOutExecutor = Executors.newSingleThreadScheduledExecutor();
            }
            waitingForElectionCoordinatorMsg.set(true);
            coordinatorMsgTimeOutExecutor.schedule(() -> {
                System.out.println("No coordinator messages were received within timeout");
                instance.startElection(ServerState.getInstance().getHigherServerInfo());
            }, COORDINATOR_MESSAGE_TIMEOUT, TimeUnit.MILLISECONDS);
            System.out.println("Coordinator message timeout started");
    }

    public void endElectionStart() {
        continueElection.set(false);
    }

    public void startElection(List<ServerInfo> higherServerList) {
        System.out.println("startElection");
        List<ServerInfo> tryList = new LinkedList<>(higherServerList);
        int tryCount = tryList.size();
        int triesLeft = ELECTION_START_TRIES;

        waitingForElectionAnswerMsg.set(true);
        continueElection.set(true);

        // Send election start messages/retry
        while (tryList.size() > 0 && continueElection.get() && triesLeft > 0) {
            ServerInfo higherServer = tryList.remove(0);
            tryCount--;

            System.out.println("higher server -> " + higherServer.getServerId());
            try {
                sendElectionStartMessage(higherServer);
                endElectionStart();
                break;
            } catch (ConnectException | InterruptedException e) {
                System.out.println("higher server " + higherServer.getServerId() + " not alive");
                tryList.add(higherServer);
            }

            if (tryCount == 0) {
                // One round of tries is complete

                if (triesLeft == ELECTION_START_TRIES) {
                    // If first round of tries
                    startElectionAnswerMsgTimeout();
                }

                triesLeft--;
                try {
                    Thread.sleep(ELECTION_START_RETRY_SLEEP);
                } catch (InterruptedException ignored) {
                }
                tryCount = tryList.size();
            }
        }
    }

    private void sendElectionStartMessage(ServerInfo higherServer) throws InterruptedException, ConnectException {
        System.out.println("Election start sent to: " + higherServer.getServerId());
        MessageSender.sendElectionStartMessage(higherServer);
    }

    public void replyAnswerMessage(ServerInfo requestingServer) {
        System.out.println("replyAnswerMessage to " + requestingServer.getServerId());
        try {
            MessageSender.sendElectionAnswerMessage(requestingServer);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void informAndSetNewCoordinator(List<ServerInfo> lowerServerList) {
        //TODO: Handle possible split-brain
        //inform lower priority servers about the new coordinator and set coordinator
        System.out.println("inform lower priority servers about the new coordinator and set coordinator");

        for (ServerInfo lowerServer : lowerServerList) {
            System.out.println(" lower servers -> " + lowerServer.getServerId());
            try {
                sendElectionCoordinatorMessage(lowerServer);
            } catch (ConnectException | InterruptedException e) {
                System.out.println("lower server " + lowerServer.getServerId() + " not alive");
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

    public void setNewCoordinator(ServerInfo newCoordinator) {

        endElectionTimeout();
        if (ServerState.getInstance().getServerInfo().equals(newCoordinator)) { // check if server is the leader

            if (!newCoordinator.equals(ServerState.getInstance().getCoordinator())) { // check previously not the leader
                System.out.println("I am the leader");
                ServerState.getInstance().setCoordinator(newCoordinator);
                LeaderState.getInstance().assignOwnLists(); // assign own list by newly appointed leader
            }
            LeaderState.getInstance().startHearBeatJob();
        } else { // not leader in this election
            if (ServerState.getInstance().getServerInfo().equals(ServerState.getInstance().getCoordinator())) {
                // check the leader in the previous election
                LeaderState.destroyLeaderInstance(); //
            }
            if (ServerState.getInstance().getCoordinator() == null) {
                System.out.println("setNewCoordinator method when null");
                ServerState.getInstance().setCoordinator(newCoordinator);

            } else {
                if (!ServerState.getInstance().getCoordinator().equals(newCoordinator)) {
                    System.out.println("setNewCoordinator method");
                    ServerState.getInstance().setCoordinator(newCoordinator);
                }
            }
            startElectionTimeout();
        }

        // Try all requests in the retry queue
        while(ServerState.getInstance().getRetryQueue().size() > 0) {
            AbstractChatRequest request = ServerState.getInstance().getRetryQueue().poll();
            if (request.getClient()!= null) {
                RequestHandlerFactory.requestHandler(request, request.getClient()).handleRequest();
            }
        }
    }

    // TODO: Call this when the server shuts down to finish jobs.
    public synchronized void shutDownBullyElection() {
        if (electionTimeoutExecutor != null) {
            electionTimeoutExecutor.shutdown();
        }

        if (electionAnswerMsgTimeOutExecutor != null) {
            electionAnswerMsgTimeOutExecutor.shutdown();
        }

        if (coordinatorMsgTimeOutExecutor != null) {
            coordinatorMsgTimeOutExecutor.shutdown();
        }
    }
}
