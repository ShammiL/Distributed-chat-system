package org.example.models.server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ServerState {
    private static ServerState instance;
    private ServerInfo serverInfo;
    private ConcurrentMap<String, ServerInfo> serversList = new ConcurrentHashMap<>(); // (serverId, serverinfp)
    private ConcurrentMap<String, ServerInfo> higherServerInfo = new ConcurrentHashMap<>();
    private ConcurrentMap<String, ServerInfo> lowerServerInfo = new ConcurrentHashMap<>();

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

    public synchronized List<ServerInfo> getServersListAsArrayLis() {
        return new ArrayList<>(serversList.values());
    }

    public synchronized void setServersList(List<ServerInfo> serversList, String myServerId) {
        for (ServerInfo server : serversList) {
            if (!server.getServerId().equals(myServerId)) {
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
            lowerServerInfo.put(myServerId, externalServer);
        }
        serversList.put(externalServer.getServerId(), externalServer);

    }


    private int compare(String myServerId, String externalServerId) {
        if (null != myServerId && null != externalServerId) {
            Integer server1Id = Integer.parseInt(myServerId.substring(1, myServerId.length()));
            Integer server2Id = Integer.parseInt(externalServerId.substring(1, externalServerId.length()));
            return server1Id - server2Id;
        }
        return 0;
    }
}