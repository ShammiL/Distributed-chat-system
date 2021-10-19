package org.example.services.client;

public class UtilService {
    private UtilService(){}

    public static boolean isAlphaNumeric(String s){
        return s.matches("[a-zA-Z0-9]+");
    }
}
