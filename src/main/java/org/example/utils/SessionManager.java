package org.example.utils;

import org.example.Entities.G_user;

/**
 * Singleton session manager to hold the currently logged-in user
 * across all screens without needing to pass it manually.
 *
 * Usage:
 *   // After login:
 *   SessionManager.getInstance().setCurrentUser(loggedInUser);
 *
 *   // In any controller:
 *   user u = SessionManager.getInstance().getCurrentUser();
 *
 *   // On logout:
 *   SessionManager.getInstance().logout();
 */
public class SessionManager {

    private static SessionManager instance;
    private G_user currentUser;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public G_user getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(G_user u) {
        this.currentUser = u;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isAdmin() {
        if (currentUser == null) return false;
        String roles = currentUser.getRoles();
        return roles != null && roles.contains("ROLE_ADMIN");
    }

    public boolean isInstructor() {
        if (currentUser == null) return false;
        String roles = currentUser.getRoles();
        return roles != null && roles.contains("ROLE_INSTRUCTOR");
    }

    public boolean isStudent() {
        if (currentUser == null) return false;
        String roles = currentUser.getRoles();
        return roles != null && roles.contains("ROLE_STUDENT");
    }

    public void logout() {
        this.currentUser = null;
    }
}
