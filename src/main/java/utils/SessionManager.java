package utils;

import Entities.User;

public class SessionManager {
    private static User currentUser;
    
    public static void setCurrentUser(User user) {
        currentUser = user;
    }
    
    public static User getCurrentUser() {
        return currentUser;
    }
    
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public static boolean isAdmin() {
        return currentUser != null && currentUser.hasRole("ROLE_ADMIN");
    }
    
    public static void logout() {
        currentUser = null;
    }
} 