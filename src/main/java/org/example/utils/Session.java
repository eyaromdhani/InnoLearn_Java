package org.example.utils;

/**
 * Classe utilitaire pour gérer l'ID de l'utilisateur connecté de manière statique.
 */
public class Session {
    private static int currentUserId;

    public static void setUserId(int userId) {
        currentUserId = userId;
    }

    public static int getUserId() {
        return currentUserId;
    }

    public static void clear() {
        currentUserId = 0;
    }
}
