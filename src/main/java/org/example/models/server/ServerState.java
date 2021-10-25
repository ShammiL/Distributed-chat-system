package org.example.models.server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ServerState {
    private static ServerState instance;
    private ServerInfo serverInfo;
    private ServerInfo coordinator;
    private final ConcurrentMap<String, ServerInfo> serversList = new ConcurrentHashMap<>(); // (serverId, serverinfp)
    private final ConcurrentMap<String, ServerInfo> higherServerInfo = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, ServerInfo> lowerServerInfo = new ConcurrentHashMap<>();

    private ServerState() {
    }

    public static synchronized ServerState getInstance() {
        if (instance == null) {
            instance = new ServerState();
        }
        return instance;
    }


    public synchronized void initServerState(String serverId) {
        serverInfo = serversList.get(serverId);
    }

    public synchronized ServerInfo getServerInfoById(String serverId) {
        return serversList.get(serverId);
    }

    public synchronized ServerInfo getServerInfo() {
        return serverInfo;
    }

    public synchronized List<ServerInfo> getServersListAsArrayList() {
        return new ArrayList<>(serversList.values());
    }

    public ServerInfo getCoordinator() {
        return coordinator;
    }

    public void setCoordinator(ServerInfo coordinator) {
        this.coordinator = coordinator;
        System.out.println("new coordinator is set");
    }

    public synchronized List<ServerInfo> getHigherServerInfo() {
        return new ArrayList<>(higherServerInfo.values());
    }

    public synchronized List<ServerInfo> getLowerServerInfo() { return new ArrayList<>(lowerServerInfo.values());
    }

    public synchronized void setServersList(List<ServerInfo> serversList, String myServerId) {
        for (ServerInfo server : serversList) {
            if (!server.getServerId().equals(myServerId)) {
                System.out.println("setServersList: " + server.getServerId());
                addServer(server, myServerId);
            } else {
                this.serverInfo = server;
            }
        }
    }


    public synchronized void addServer(ServerInfo externalServer, String myServerId) {
        if (compare(myServerId, externalServer.getServerId()) < 0) {
            higherServerInfo.put(externalServer.getServerId(), externalServer);
        } else {
//            lowerServerInfo.put(myServerId, externalServer);
            lowerServerInfo.put(externalServer.getServerId(), externalServer);
        }
        serversList.put(externalServer.getServerId(), externalServer);

    }


    private int compare(String myServerId, String externalServerId) {
        if (null != myServerId && null != externalServerId) {
            Integer server1Id = Integer.parseInt(myServerId.substring(1));
            Integer server2Id = Integer.parseInt(externalServerId.substring(1));
            return server1Id - server2Id;
        }
        return 0;
    }
}