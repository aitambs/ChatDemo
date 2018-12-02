package com.company;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class Message {
    private final String message;
    private final int length, hash;
    private final long timeStamp;

    /**
     * Constructs a message with the given content
     * @param message the content of the message
     */
    public Message(String message){
        timeStamp=System.currentTimeMillis();
        this.message=message;
        length=message.getBytes().length;
        hash=message.hashCode();
    }

    /**
     * Constructs a message from a stream of bytes from given InputStream
     * @param inputStream source of byte stream
     * @throws IOException
     */
    public Message(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[16];
        inputStream.read(buffer);
        timeStamp=ByteBuffer.wrap(buffer).getLong(0);
        length=ByteBuffer.wrap(buffer).getInt(8);
        hash=ByteBuffer.wrap(buffer).getInt(12);
        byte[] messagebuffer=new byte[length];
        int actuallyRead=inputStream.read(messagebuffer);
        if(actuallyRead<length)
            throw new IOException("Invalid message size: "+actuallyRead+" expecting "+length);
        message=new String(messagebuffer);
    }

    /**
     * Get the Message's content
     * @return the Message's content
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get the Message's content length in bytes
     * @return the Message's content lengh in bytes
     */
    public int getLength() {
        return length;
    }

    /**
     * Get the Message's content hash code
     * @return the Message's content hash code
     */
    public int getHash() {
        return hash;
    }

    /**
     * Get the Message's timestamp
     * @return the Message's timestamp
     */
    public long getTimeStamp() {
        return timeStamp;
    }

    /**
     * Writes the Message as a stream of bytes into given OutputStream
     * @param outputStream where the Message should be written
     * @throws IOException
     */
    public void write(OutputStream outputStream) throws IOException {
        byte[] buffer=new byte[16];
        ByteBuffer.wrap(buffer).putLong(0,timeStamp);
        ByteBuffer.wrap(buffer).putInt(8,length);
        ByteBuffer.wrap(buffer).putInt(12,hash);
        outputStream.write(buffer);
        outputStream.write(message.getBytes());
    }

    /**
     * Verifys a Message's integrity via it's length and hash code.
     * @param message the message to verify
     * @return
     */
    public static boolean verify(Message message){
        return message.message.getBytes().length==message.length && message.message.hashCode()==message.hash;
    }
}
