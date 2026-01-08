package com.broadcastsender.model;

/**
 * Enum representing the status of an upload operation.
 */
public enum UploadStatus {
    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    SUCCESS("Success"),
    FAILED("Failed"),
    RETRYING("Retrying");
    
    private final String displayName;
    
    UploadStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
