package org.example.models.server;

import org.example.models.client.GlobalClient;
import org.example.models.room.GlobalRoom;
import org.example.services.client.ChatClientServer;
import org.example.services.coordination.ListServices;
import org.example.services.coordination.MessageSender;

import java.net.ConnectException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.min;

public class LeaderState {
    public static final int HEARTBEAT_INTERVAL = 500;
    public static AtomicInteger heartbeatSleep = new AtomicInteger(1000);
    public static final int MAX_POSSIBLY_DOWN_ROUNDS = 5;
    public static final String ALIVE = "ALIVE";
    public static final String POSSIBLY_DOWN = "POSSIBLY_DOWN";
    public static final String DOWN = "DOWN";


    private static LeaderState instance;
    private Map<String, GlobalClient> globalClientList;
    private Map<String, GlobalRoom> globalRoomList;
    private final Map<String, String> followerStatus = new ConcurrentHashMap<>();
    private final Map<String, Integer> serverDownRounds = new ConcurrentHashMap<>();
    private static ScheduledExecutorService heartBeatExecutorService;

    public static synchronized LeaderState getInstance() {
        if (instance == null && ServerState.getInstance().getServerInfo().equals(ServerState.getInstance().getCoordinator())) {
            instance = new LeaderState();
        }
        return instance;
    }

    public static synchronized void destroyLeaderInstance() {
        System.out.println("Destroy previous leader instance");
        if (heartBeatExecutorService != null) {
            heartBeatExecutorService.shutdownNow();
            heartBeatExecutorService = null;
        }
        instance = null;
    }

    public Map<String, GlobalClient> getGlobalClientList() {
        return globalClientList;
    }

    public Map<String, GlobalRoom> getGlobalRoomList() {
        return globalRoomList;
    }

    public void assignOwnLists() {
        System.out.println("assign own lists");
        globalClientList = ListServices.convertGlobalClientList(ChatClientServer.channelIdClient);
        globalRoomList = ListServices.convertGlobalRoomList(ChatClientServer.localRoomIdLocalRoom);
        printLists();
    }

    private synchronized void addServersLocalListToGlobalClientLists(Map<String, GlobalClient> globalClients) {
        for (Map.Entry<String, GlobalClient> e : globalClients.entrySet()) {
            if (!globalClientList.containsKey(e.getKey())) {
                globalClientList.put(e.getKey(), e.getValue());
            }
        }
        System.out.println("global client list updated");
    }

    private synchronized void addServersLocalListToGlobalRoomLists(Map<String, GlobalRoom> globalRooms) {
        for (Map.Entry<String, GlobalRoom> e : globalRooms.entrySet()) {
            if (!globalRoomList.containsKey(e.getKey())) {
                globalRoomList.put(e.getKey(), e.getValue());
            }
        }
        System.out.println("global room list updated");
    }

    public void addListsOfAServer(Map<String, GlobalClient> globalClients, Map<String, GlobalRoom> globalRooms) {
        addServersLocalListToGlobalClientLists(globalClients);
        addServersLocalListToGlobalRoomLists(globalRooms);
        printLists();
    }

    public synchronized GlobalClient checkAndAddClient(GlobalClient client) {
        if (globalClientList.containsKey(client.getIdentity())) {
            client.setAccepted(false);
            System.out.println("user " + client.getIdentity() + " already exist");
        } else {
            client.setAccepted(true);
            globalClientList.put(client.getIdentity(), client);
            System.out.println(client.getIdentity() + " user added successfully");
        }

        return client;

    }

    public synchronized GlobalRoom checkAndAddRoom(GlobalRoom room) {
        if (globalRoomList.containsKey(room.getRoomId())) {
            room.setAccepted(false);
            System.out.println("room " + room.getRoomId() + " already exist");
        } else {
            room.setAccepted(true);
            globalRoomList.put(room.getRoomId(), room);
            System.out.println(room.getRoomId() + " added successfully");
        }
        return room;
    }

    public synchronized void deleteAClient(GlobalClient client) {
        globalClientList.remove(client.getIdentity());
    }

    public synchronized void deleteARoom(GlobalRoom room) {
        globalRoomList.remove(room.getRoomId());
    }

    public void removeListsByServerID(String serverId) {
        removeClientListByServerID(serverId);
        removeRoomListByServerID(serverId);
    }

    private synchronized void removeClientListByServerID(String serverId) {
        globalClientList.values().removeIf(value -> value.getServerId().equals(serverId));
    }

    private synchronized void removeRoomListByServerID(String serverId) {
        globalRoomList.values().removeIf(value -> value.getServerId().equals(serverId));
    }

    private void printLists() {
        globalClientList.forEach((key, value) -> System.out.println(key + " " + value));
        globalRoomList.forEach((key, value) -> System.out.println(key + " " + value));
    }

    private volatile long lastTime = System.currentTimeMillis();

    public synchronized void startHearBeatJob() {
        if (heartBeatExecutorService == null) {
            heartBeatExecutorService = Executors.newSingleThreadScheduledExecutor();
            heartBeatExecutorService.scheduleAtFixedRate(() -> {
                System.out.println("Leader heart beat job is running in this server");

                long aliveServers = followerStatus.entrySet().stream()
                        .filter(s -> "ALIVE".equals(s.getValue())).count();

                if (aliveServers == 0) {
                    try {
                        Thread.sleep(heartbeatSleep.get());
                        heartbeatSleep.updateAndGet((value)-> min(value+1000, 5000));
                    } catch (InterruptedException ignored) {
                    }
                } else {
                    heartbeatSleep = new AtomicInteger(1000);
                }

                for (ServerInfo server : ServerState.getInstance().getServersListAsArrayList()) {
                    try {
                        MessageSender.sendHeartBeatMessage(server, () -> {
                            followerStatus.put(server.getServerId(), ALIVE);
                            System.out.println("Server " + server.getServerId() + " is Alive");
                        }, () -> handleHeartBeatSendFailure(server));
                    } catch (ConnectException | InterruptedException ignored) {
                        //This branch shouldn't execute
                    }
                }
            }, 0, HEARTBEAT_INTERVAL, TimeUnit.MILLISECONDS);
        }
    }

    private void handleHeartBeatSendFailure(ServerInfo server) {

            String currentStatus = followerStatus.get(server.getServerId());
            if (POSSIBLY_DOWN.equals(currentStatus)) {
                if (serverDownRounds.getOrDefault(server.getServerId(), 0) < MAX_POSSIBLY_DOWN_ROUNDS) {
                    serverDownRounds.put(server.getServerId(),
                            serverDownRounds.getOrDefault(server.getServerId(), 0) + 1);
                } else if (serverDownRounds.get(server.getServerId()) == MAX_POSSIBLY_DOWN_ROUNDS) {
                    removeClientListByServerID(server.getServerId());
                    removeRoomListByServerID(server.getServerId());
                    followerStatus.put(server.getServerId(), DOWN);
                }
                System.out.println("Server " + server.getServerId() + " is Possibly Down");
            } else if (DOWN.equals(currentStatus)) {
                System.out.println("Server " + server.getServerId() + " is Down");
            } else if (ALIVE.equals(currentStatus)) {
                followerStatus.put(server.getServerId(), POSSIBLY_DOWN);
                System.out.println("Server " + server.getServerId() + " is Possibly Down");
            }
    }
}
