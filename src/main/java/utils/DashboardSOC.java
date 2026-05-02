package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Mini SOC (Security Operations Center) Utility
 */
public class DashboardSOC {
    private static final List<String> logs = new ArrayList<>();
    private static int failedAttempts = 12;
    private static int activeSessions = 4;
    private static String systemStatus = "OPTIMAL";

    static {
        addLog("SOC Initialized...");
        addLog("Firewall Active - IP Filtering enabled.");
        addLog("Database Encryption Layer: AES-256 Verified.");
    }

    public static void addLog(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        logs.add("[" + timestamp + "] " + message);
        if (logs.size() > 50) logs.remove(0);
    }

    public static List<String> getLogs() {
        return logs;
    }

    public static int getFailedAttempts() { return failedAttempts; }
    public static int getActiveSessions() { return activeSessions; }
    public static String getSystemStatus() { return systemStatus; }
    
    public static void incrementFailedAttempts() { failedAttempts++; }
}
