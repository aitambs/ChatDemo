package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Main {

    private static final int PORT=3000;

    public static void main(String[] args) {
        List<Message> messages = new ArrayList<>();
        Map<String, String> users = new HashMap<>();
        messages.add(new Message("This is the first message"));
        ServerSocket serverSocket=null;
        try {
            serverSocket = new ServerSocket(PORT);
            while (true) {
                Socket socket=serverSocket.accept();
                new HandleClient(socket,messages,users).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
