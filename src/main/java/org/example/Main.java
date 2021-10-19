package org.example;

import org.example.services.client.ChatClientServer;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

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

            System.out.println(serverConfig);
            System.out.println(serverId);
        } catch (CmdLineException e) {
            System.err.println("Error while parsing cmd line arguments: " + e.getLocalizedMessage());
        }


//        new ChatClientServer(5000, "s1").run();
    }
}