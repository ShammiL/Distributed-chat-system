package org.example;

import org.example.services.client.ChatClientServer;

public class Main {

    public static void main(String[] args) throws Exception {
        new ChatClientServer(5000, "s1").run();
    }
}