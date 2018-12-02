package com.company;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class RefreshMessages extends Thread implements StoppableThread {
    private static final int REFRESH_FREQUENCY_MILLIS = 1000;
    public static long timeStamp =System.currentTimeMillis();
    private boolean stop=false;


    @Override
    public void run() {
        Socket socket=null;
        InputStream inputStream =null;
        OutputStream outputStream=null;
        try {
            while (!stop){
                Thread.sleep(REFRESH_FREQUENCY_MILLIS);
                socket=new Socket(Main.HOST,Main.PORT);
                inputStream=socket.getInputStream();
                outputStream=socket.getOutputStream();
                byte[] buffer=new byte[8];
                ByteBuffer.wrap(buffer).putLong(timeStamp);
                outputStream.write(Main.REFRESH_MESSAGES);
                outputStream.write(buffer);
                Server.getServerResponse(inputStream);
                outputStream.close();
                inputStream.close();
                socket.close();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void stopThread() {
        this.stop=true;
    }
}
