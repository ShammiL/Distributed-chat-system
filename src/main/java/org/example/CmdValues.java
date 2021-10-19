package org.example;

import org.kohsuke.args4j.Option;

public class CmdValues {
    @Option(required = true, name = "-n", usage = "n=Server ID")
    private String serverId = "1";

    @Option(required = true, name = "-l", usage = "l=Server Configuration File")
    private String serverConfig = "./config/server.tab";

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(String serverConfig) {
        this.serverConfig = serverConfig;
    }
}
