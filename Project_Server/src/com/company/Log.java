package com.company;

import java.io.*;
import java.net.Socket;
import java.util.Date;

public class Log {
    /**
     * Change to existing writable path on your computer
     */
    private static final String LOG_FILE="C:\\Users\\barsa\\Documents\\log.log";

    /**
     * Logs events into Log File
     * @param socket the socket where the communication occured from
     * @param string a message to put into the log file
     */
    static void log_event(Socket socket, String string){
        try {
            StringBuilder stringBuilder=new StringBuilder();
            stringBuilder.append(new Date().toString()).append(" from: ").append(socket.getRemoteSocketAddress().toString());
            stringBuilder.append("; ").append(string).append("\r\n");
            OutputStream outputStream=new FileOutputStream(new File(LOG_FILE),true);
            outputStream.write(stringBuilder.toString().getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
