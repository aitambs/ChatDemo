package com.company;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static final int NO_ACTION = 0;
    public static final int MESSAGE_FOLLOWS = 1;
    public static final int MESSAGE_ARRAY_FOLLOWS = 2;
    public static final int GET_LAST_MESSAGES = 100;
    public static final int REFRESH_MESSAGES = 101;
    public static final int PORT = 3000;
    public static final String HOST = "127.0.0.1";
    public static final int LOGIN = 103;
    public static final int SIGNUP = 104;
    public static User currentUser;
    private static Socket socket;

    public static void main(String[] args) {

        while (currentUser == null) {
            try {
                int selection = Integer.parseInt(Ui.getUserInput("1. Login\n2. Register\n9. Exit"));
                switch (selection) {
                    case 1:
                        selection = LOGIN;
                        break;
                    case 2:
                        selection = SIGNUP;
                        break;
                    case 9:
                        return;
                    default:
                        throw new NumberFormatException();
                }
                String userName = Ui.getUserInput("Enter Username");
                String password = Ui.getUserInput("Enter Password");
                User user = new User(userName, password);
                Server.sendToServer(socket, selection, null, user);

            } catch (NumberFormatException e) {
                Ui.printFromClient("Please Enter a valid number");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        RefreshMessages refreshMessages = new RefreshMessages();
        refreshMessages.start();
        while (true) {

            try {
                int selection = Integer.parseInt(Ui.getUserInput("1. Add Message\n2. Get Messages\n9. Exit"));
                switch (selection) {
                    case 9:
                        refreshMessages.stopThread();
                        return;
                    case 1:
                        Server.sendToServer(socket, MESSAGE_FOLLOWS, new Message(Ui.getUserInput("Enter one Message Line:")), null);
                        break;
                    case 2:
                        Server.sendToServer(socket, GET_LAST_MESSAGES, null, null);
                        break;
                    default:
                        throw new NumberFormatException();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                Ui.printFromClient("Please Enter a valid number");
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
