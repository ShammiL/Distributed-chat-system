package org.example;

import org.apache.log4j.Logger;
import org.example.services.client.ChatClientServer;
import org.example.services.coordination.server.CoordinationServer;
import org.example.services.server.ConfigReaderService;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;


public class Main {
    private static final Logger logger = Logger.getLogger(Main.class);
    public static void main(String[] args) throws Exception {

        String serverId;
        String serverConfig;
        CmdValues values = new CmdValues();
        CmdLineParser parser = new CmdLineParser(values);

        try {
            parser.parseArgument(args);
            serverId = values.getServerId();
            serverConfig = values.getServerConfig();
            logger.info("server config set");

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

        } catch (CmdLineException e) {
            e.getLocalizedMessage();
        }
    }
}