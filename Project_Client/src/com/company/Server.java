package com.company;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static com.company.Main.*;

public class Server {

    public static void sendToServer(Socket socket, int selection, Message message, User user) throws IOException {
        socket = new Socket(HOST, PORT);
        OutputStream outputStream = socket.getOutputStream();
        switch (selection) {
            case MESSAGE_FOLLOWS:
                outputStream.write(selection);
                message.write(outputStream);
                getServerResponse(socket.getInputStream());
                break;
            case REFRESH_MESSAGES:
            case GET_LAST_MESSAGES:
                outputStream.write(selection);
                getServerResponse(socket.getInputStream());
                break;
            case LOGIN:
            case SIGNUP:
                outputStream.write(selection);
                user.write(outputStream);
                getServerLoginOrSignupResponse(socket.getInputStream(), user);


        }
    }


    public static void getServerResponse(InputStream inputStream) throws IOException {
        int serverResponse = inputStream.read();
        switch (serverResponse) {
            case NO_ACTION:
                break;
            case MESSAGE_FOLLOWS:
                getMessage(inputStream);
                break;
            case MESSAGE_ARRAY_FOLLOWS:
                getMessageArray(inputStream);
                break;
            default:
                break;
        }
    }

    private static void getMessage(InputStream inputStream) throws IOException {
        Message message = new Message(inputStream);
        Ui.printFromServer(message.getMessage());
    }

    private static void getMessageArray(InputStream inputStream) throws IOException {
        int size = inputStream.read();
        List<Message> messages = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            messages.add(new Message(inputStream));
        }
        for (Message message2 : messages) {
            Ui.printFromServer(message2.getMessage());
            RefreshMessages.timeStamp = message2.getTimeStamp();
        }
    }

    private static void getServerLoginOrSignupResponse(InputStream inputStream, User user) throws IOException {
        int serverResponse = inputStream.read();
        switch (serverResponse) {
            case NO_ACTION:
                currentUser = user;
                break;
            case MESSAGE_FOLLOWS:
                getMessage(inputStream);
                break;
        }
    }
}
