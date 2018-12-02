package com.company;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

public class HandleClient extends Thread {

    public static final int NO_ACTION = 0;
    public static final int MESSAGE_FOLLOWS = 1;
    public static final int MESSAGE_ARRAY_FOLLOWS = 2;
    public static final int GET_LAST_MESSAGES = 100;
    public static final int REFRESH_MESSEGES = 101;
    public static final int MAX_LAST_MESSAGES = 10; // Maximum 255
    public static final int LOGIN = 103;
    public static final int SIGNUP = 104;
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private List<Message> messages;
    private Map<String,String> users;

    public HandleClient(Socket socket, List<Message> messages, Map<String,String> users){
        this.socket=socket;
        this.messages= messages;
        this.users=users;
    }

    @Override
    public void run() {
        try {
            inputStream=socket.getInputStream();
            outputStream=socket.getOutputStream();
            int command = inputStream.read();
            int messagesSize=messages.size();
            int size;

            switch (command){
                case MESSAGE_FOLLOWS:
                    Message message=new Message(inputStream);
                    if (Message.verify(message)) {
                        messages.add(message);
                        System.out.println(message.getMessage());
                        outputStream.write(NO_ACTION);
                        Log.log_event(socket,message.getMessage());
                    } else {
                        Message returnMessage = new Message("Error verifying Message");
                        outputStream.write(MESSAGE_FOLLOWS);
                        returnMessage.write(outputStream);
                        Log.log_event(socket,"Bad Message received");
                    }
                    break;
                case GET_LAST_MESSAGES:
                    Log.log_event(socket,"Requested last messages");
                    int toSend = ((messagesSize- MAX_LAST_MESSAGES)<0)?0:(messagesSize-MAX_LAST_MESSAGES);
                    size= ((messagesSize-MAX_LAST_MESSAGES)<0)?messagesSize:MAX_LAST_MESSAGES;
                    outputStream.write(MESSAGE_ARRAY_FOLLOWS);
                    outputStream.write(size);

                    for (int i = toSend; i < messagesSize ; i++) {
                        messages.get(i).write(outputStream);
                    }

                    break;
                case REFRESH_MESSEGES:
                    byte[] buffer=new byte[8];
                    inputStream.read(buffer);
                    long since = ByteBuffer.wrap(buffer).getLong();

                    int i=0;
                    for (; i < messagesSize; i++) {
                        if (messages.get(i).getTimeStamp()>since) break;
                    }
                    if (i==messagesSize){
                        outputStream.write(NO_ACTION);
                    } else {
                        size= messagesSize-i;
                        outputStream.write(MESSAGE_ARRAY_FOLLOWS);
                        outputStream.write(size);

                        for (int j = i; j < messagesSize ; j++) {
                            messages.get(j).write(outputStream);
                        }
                    }
                    break;
                case LOGIN:
                    User user=new User(inputStream);
                    if (users.containsKey(user.getUserName())){
                        if (users.get(user.getUserName()).equals(user.getPassword())){
                            outputStream.write(NO_ACTION);
                            return;
                        }
                    }
                    message = new Message("Invalid username or password");
                    outputStream.write(MESSAGE_FOLLOWS);
                    message.write(outputStream);
                    break;
                case SIGNUP:
                    user = new User(inputStream);
                    synchronized (users){
                        if (users.containsKey(user.getUserName())){
                            message = new Message("User Already exists");
                            outputStream.write(MESSAGE_FOLLOWS);
                            message.write(outputStream);
                        } else {
                            users.put(user.getUserName(),user.getPassword());
                            outputStream.write(NO_ACTION);
                        }
                    }
                    break;
                default:
                    Log.log_event(socket,"Invalid request");
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
