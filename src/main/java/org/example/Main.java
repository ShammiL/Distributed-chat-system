package org.example;

import org.example.services.client.ChatClientServer;
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
            ChatClientServer.getInstance().run();

        } catch (CmdLineException e) {
            e.getLocalizedMessage();
        }
    }
}