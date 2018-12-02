package com.company;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class User {
    private String userName, password;

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public User(InputStream inputStream) throws IOException{
        int actuallyRead,userNameBytesLength,passwordBytesLength;
        userNameBytesLength=inputStream.read();
        byte[] userNameBytes=new byte[userNameBytesLength];
        actuallyRead=inputStream.read(userNameBytes);
        if (actuallyRead!=userNameBytesLength)
            throw new IOException("Invalid size");
        userName=new String(userNameBytes);

        passwordBytesLength=inputStream.read();
        byte[] passwordBytes=new byte[passwordBytesLength];
        actuallyRead=inputStream.read(passwordBytes);
        if (actuallyRead!=passwordBytesLength)
            throw new IOException("Invalid size");
        password=new String(passwordBytes);
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public void write(OutputStream outputStream) throws IOException {
        byte[] userNameBytes=userName.getBytes();
        byte[] passwordBytes=password.getBytes();
        outputStream.write(userNameBytes.length);
        outputStream.write(userNameBytes);
        outputStream.write(passwordBytes.length);
        outputStream.write(passwordBytes);
    }
}
