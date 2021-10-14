package org.example;

import org.example.services.client.ChatClientServer;

public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println("print Hello world");
        new ChatClientServer(5000).run();
    }
}