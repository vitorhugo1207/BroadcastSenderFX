package com.broadcastsender.model;

/**
 * Enum representing different authentication types supported by the application.
 */
public enum AuthType {
    NONE("No Authentication"),
    BEARER("Bearer Token"),
    BASIC("Basic Auth"),
    BASIC_BASE64("Basic Auth Base64");
    
    private final String displayName;
    
    AuthType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
