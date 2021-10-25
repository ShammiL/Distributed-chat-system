package org.example;

import org.example.services.client.ChatClientServer;
import org.example.services.coordination.server.CoordinationServer;
import org.example.services.server.ConfigReaderService;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;


public class Main {

    public static void main(String[] args) throws Exception {

        String serverId;
        String serverConfig;
        CmdValues values = new CmdValues();
        CmdLineParser parser = new CmdLineParser(values);

        try {
            parser.parseArgument(args);
            serverId = values.getServerId();
            serverConfig = values.getServerConfig();

            new ConfigReaderService().readFile(serverConfig, serverId);
            Thread coordinatorThread = new Thread(() -> {
                try {
                    CoordinationServer.getInstance().run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            coordinatorThread.start();
            CoordinationServer.getInstance().SelectCoordinator();
            ChatClientServer.getInstance().run();

//            ServerInfo s1 = ServerState.getInstance().getServerInfoById("s1");
//            MessageSender.releaseIdentity(s1, "test", "room");

        } catch (CmdLineException e) {
            e.getLocalizedMessage();
        }
    }
}