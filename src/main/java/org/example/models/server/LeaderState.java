package org.example.models.server;

import org.example.models.client.GlobalClient;
import org.example.models.room.GlobalRoom;
import org.example.services.client.ChatClientServer;
import org.example.services.coordination.ListServices;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LeaderState {
    private static LeaderState instance;
    private Map<String, GlobalClient> globalClientList = new ConcurrentHashMap<>();
    private Map<String, GlobalRoom> globalRoomList = new ConcurrentHashMap<>();

    public static synchronized LeaderState getInstance() {
        if (instance == null && ServerState.getInstance().getServerInfo().equals(ServerState.getInstance().getCoordinator())) {
            instance = new LeaderState();

        }
        return instance;
    }

    public static synchronized void destroyLeaderInstance() {
        System.out.println("Destroy previous leader instance");
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
        addServersLocalListToGlobalClientLists(
                ListServices.convertGlobalClientList(ChatClientServer.channelIdClient));
        addServersLocalListToGlobalRoomLists(
                ListServices.convertGlobalRoomList(ChatClientServer.localRoomIdLocalRoom));
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

    public synchronized boolean checkAndAddClient(GlobalClient client) {
        if (globalClientList.containsKey(client.getIdentity())) {
            client.setAccepted(false);
            System.out.println("user " + client.getIdentity() + " already exist");
            return false;
        } else {
            client.setAccepted(true);
            globalClientList.put(client.getIdentity(), client);
            System.out.println(client.getIdentity() + " user added successfully");
            return true;
        }

    }

    public synchronized boolean checkAndAddRoom(GlobalRoom room) {
        if (globalRoomList.containsKey(room.getRoomId())) {
            room.setAccepted(false);
            System.out.println("room " + room.getRoomId() + " already exist");
            return false;
        } else {
            room.setAccepted(true);
            globalRoomList.put(room.getRoomId(), room);
            System.out.println(room.getRoomId() + " added successfully");
            return true;
        }
    }

    public synchronized void deleteAClient(String clientId) {
        globalClientList.remove(clientId);
    }

    public synchronized void deleteARoom(String roomId) {
        globalRoomList.remove(roomId);
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

    public void printLists(){
        globalClientList.forEach((key, value) -> System.out.println(key + " " + value));
        globalRoomList.forEach((key, value) -> System.out.println(key + " " + value));
    }

}
