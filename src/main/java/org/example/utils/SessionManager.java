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
        if (roles == null) return false;
        String r = roles.toUpperCase();
        return r.contains("ADMIN");
    }

    public boolean isInstructor() {
        if (currentUser == null) return false;
        String roles = currentUser.getRoles();
        if (roles == null) return false;
        String r = roles.toUpperCase();
        return r.contains("INSTRUCTOR") || r.contains("ENSEIGNANT") || r.contains("TEACHER");
    }

    public boolean isRecruiter() {
        if (currentUser == null) return false;
        String roles = currentUser.getRoles();
        if (roles == null) return false;
        String r = roles.toUpperCase();
        return r.contains("RECRUITER") || r.contains("PARTNER");
    }

    public boolean isStudent() {
        if (currentUser == null) return false;
        String roles = currentUser.getRoles();
        if (roles == null) return false;
        String r = roles.toUpperCase();
        return r.contains("STUDENT") || (!isAdmin() && !isInstructor() && !isRecruiter());
    }

    public void logout() {
        this.currentUser = null;
        org.example.utils.Session.clear();
    }
}
