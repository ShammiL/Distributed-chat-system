package org.example.services.coordination.election;

import org.apache.log4j.Logger;
import org.example.config.Config;
import org.example.handlers.requestHandlers.chat.RequestHandlerFactory;
import org.example.models.messages.chat.AbstractChatRequest;
import org.example.models.server.LeaderState;
import org.example.models.server.ServerInfo;
import org.example.models.server.ServerState;
import org.example.services.coordination.MessageSender;

import java.net.ConnectException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class BullyElection {

    private AtomicBoolean waitingForElectionAnswerMsg = new AtomicBoolean(false);
    private AtomicBoolean waitingForElectionCoordinatorMsg = new AtomicBoolean(false);
    private static AtomicBoolean continueElection = new AtomicBoolean(false);
    private AtomicLong lastHeartBeatTime = new AtomicLong(0);
    private ScheduledExecutorService electionAnswerMsgTimeOutExecutor;
    private ScheduledExecutorService coordinatorMsgTimeOutExecutor;
    private ScheduledExecutorService electionTimeoutExecutor;
    private ScheduledFuture<?> electionTimeoutFuture;
    private static final Map<String, Integer> electionStartTries = new ConcurrentHashMap<>();

    private static Logger logger = Logger.getLogger(BullyElection.class);

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

    public boolean getWaitingForElectionCoordinatorMsg() {
        return waitingForElectionCoordinatorMsg.get();
    }

    public boolean getContinueElection() {
        return continueElection.get();
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
            if (System.currentTimeMillis() - lastHeartBeatTime.get() > Config.HEARTBEAT_THRESHOLD) {
                logger.info("Starting election because leader didn't send heartbeat");
                BullyElection.getInstance().startElection(
                        ServerState.getInstance().getHigherServerInfo()
                );
            }
        }, Config.ELECTION_START_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    public synchronized void endElectionTimeout() {

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
        logger.info("Election Answer timeout started!");
        electionAnswerMsgTimeOutExecutor.schedule(() -> {
            logger.info("No Answer Messages were received within timeout");
            informAndSetNewCoordinator(ServerState.getInstance().getLowerServerInfo());
            endElectionStart();
            endElectionAnswerMsgTimeout();
        }, Config.ELECTION_ANSWER_MESSAGE_TIMEOUT, TimeUnit.MILLISECONDS);
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
        logger.info("Coordinator message timeout ended");
    }

    public synchronized void startCoordinatorWaitTimeout() {
        if (coordinatorMsgTimeOutExecutor == null) {
            coordinatorMsgTimeOutExecutor = Executors.newSingleThreadScheduledExecutor();
        }
        waitingForElectionCoordinatorMsg.set(true);
        coordinatorMsgTimeOutExecutor.schedule(() -> {
            logger.info("No coordinator messages were received within timeout");
            instance.startElection(ServerState.getInstance().getHigherServerInfo());
        }, Config.COORDINATOR_MESSAGE_TIMEOUT, TimeUnit.MILLISECONDS);
        logger.info("Coordinator message timeout started");
    }

    public static void endElectionStart() {
        continueElection.set(false);
    }

    public static Runnable getElectionStartFailureCallback(ServerInfo higherServer) {

        return () -> {
            logger.info("higher server " + higherServer.getServerId() + " not alive [ElectionStart]");
            try {
                Thread.sleep(Config.ELECTION_START_RETRY_SLEEP);
            } catch (InterruptedException ignored) {
            }
            electionStartTries.computeIfPresent(higherServer.getServerId(), (key,value) -> {
                if (continueElection.get() && value <= Config.ELECTION_START_TRIES) {
                    try {
                        sendElectionStartMessage(higherServer, BullyElection::endElectionStart,  getElectionStartFailureCallback(higherServer));
                    } catch (InterruptedException | ConnectException e) {
                        logger.error(e);
                    }
                }
                return value + 1;
            });
        };
    }

    public void startElection(List<ServerInfo> higherServerList) {
        logger.info("startElection");
        waitingForElectionAnswerMsg.set(true);
        continueElection.set(true);
        for (ServerInfo higherServer : higherServerList) {
            electionStartTries.putIfAbsent(higherServer.getServerId(), 0);
            electionStartTries.computeIfPresent(higherServer.getServerId(), (k,v)-> 0);
            logger.info("higher server -> " + higherServer.getServerId());
            try {
                sendElectionStartMessage(higherServer,  BullyElection::endElectionStart,
                        getElectionStartFailureCallback(higherServer));
            } catch (InterruptedException | ConnectException e) {
               logger.error(e);
            }
        }
        startElectionAnswerMsgTimeout();
    }

    private static void sendElectionStartMessage(ServerInfo higherServer, Runnable success, Runnable failure) throws InterruptedException, ConnectException {
        logger.info("Election start sent to: " + higherServer.getServerId());
        MessageSender.sendElectionStartMessage(higherServer, success, failure);
    }

    public void replyAnswerMessage(ServerInfo requestingServer) {
        logger.info("replyAnswerMessage to " + requestingServer.getServerId());
        try {
            MessageSender.sendElectionAnswerMessage(requestingServer);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void informAndSetNewCoordinator(List<ServerInfo> lowerServerList) {
        //inform lower priority servers about the new coordinator and set coordinator
        logger.info("inform lower priority servers about the new coordinator and set coordinator");

        for (ServerInfo lowerServer : lowerServerList) {
            logger.info(" lower servers -> " + lowerServer.getServerId());
            try {
                sendElectionCoordinatorMessage(lowerServer);
            } catch (ConnectException | InterruptedException e) {
                logger.info("lower server " + lowerServer.getServerId() + " not alive");
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
            logger.info(e.getMessage());
        }
    }

    public void setNewCoordinator(ServerInfo newCoordinator) {

        endElectionTimeout();
        if (ServerState.getInstance().getServerInfo().equals(newCoordinator)) { // check if server is the leader

            if (!newCoordinator.equals(ServerState.getInstance().getCoordinator())) { // check previously not the leader
                logger.info("I am the leader");
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
                logger.info("setNewCoordinator method when null");
                ServerState.getInstance().setCoordinator(newCoordinator);

            } else {
                if (!ServerState.getInstance().getCoordinator().equals(newCoordinator)) {
                    logger.info("setNewCoordinator method");
                    ServerState.getInstance().setCoordinator(newCoordinator);
                }
            }
            startElectionTimeout();
        }

        // Try all requests in the retry queue
        while (ServerState.getInstance().getRetryQueue().size() > 0) {
            AbstractChatRequest request = ServerState.getInstance().getRetryQueue().poll();
            if (request != null && request.getClient() != null) {
                RequestHandlerFactory.requestHandler(request, request.getClient()).handleRequest();
            }
        }
    }

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
