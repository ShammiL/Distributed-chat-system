package org.example.services;


public class UtilService {
    private UtilService() {
    }

    public static boolean isAlphaNumeric(String s) {
        return s.matches("[a-zA-Z0-9]+");
    }

    public static boolean isLikeMainHall(String s) {
        return s.matches("^MainHall");
    }

}
