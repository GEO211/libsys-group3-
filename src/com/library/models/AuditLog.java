package com.library.models;

import java.sql.Timestamp;

public class AuditLog {
    private int logId;
    private String action;
    private String details;
    private Timestamp timestamp;
    private int adminId;
    private String adminName;

    public AuditLog(int logId, String action, String details, Timestamp timestamp, int adminId, String adminName) {
        this.logId = logId;
        this.action = action;
        this.details = details;
        this.timestamp = timestamp;
        this.adminId = adminId;
        this.adminName = adminName;
    }

    // Getters
    public int getLogId() { return logId; }
    public String getAction() { return action; }
    public String getDetails() { return details; }
    public Timestamp getTimestamp() { return timestamp; }
    public int getAdminId() { return adminId; }
    public String getAdminName() { return adminName; }

    @Override
    public String toString() {
        return String.format("[%s] %s by %s: %s", 
            timestamp.toString(), action, adminName, details);
    }
} 