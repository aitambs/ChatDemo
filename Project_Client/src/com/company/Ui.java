package com.company;

import java.util.Scanner;

public class Ui {

    static void printFromClient(String string){
        System.out.println(string);
    }

    static String getUserInput(String string){
        printFromClient(string);
        return new Scanner(System.in).nextLine();
    }

    static void printFromServer(String string){
        System.err.println(string);
    }
}
