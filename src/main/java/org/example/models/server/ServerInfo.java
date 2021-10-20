package org.example.models.server;

import com.opencsv.bean.CsvBindByPosition;

public class ServerInfo {

    @CsvBindByPosition(position = 0)
    private String serverId;

    @CsvBindByPosition(position = 1)
    private String serverAddress;

    @CsvBindByPosition(position = 2)
    private int clientPort;

    @CsvBindByPosition(position = 3)
    private int coordinationPort;

    public ServerInfo(String serverId, String serverAddress, int clientPort, int coordinationPort) {
        this.serverId = serverId;
        this.serverAddress = serverAddress;
        this.clientPort = clientPort;
        this.coordinationPort = coordinationPort;
    }

    public ServerInfo() {
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public int getClientPort() {
        return clientPort;
    }

    public void setClientPort(int clientPort) {
        this.clientPort = clientPort;
    }

    public int getCoordinationPort() {
        return coordinationPort;
    }

    public void setCoordinationPort(int coordinationPort) {
        this.coordinationPort = coordinationPort;
    }
}
