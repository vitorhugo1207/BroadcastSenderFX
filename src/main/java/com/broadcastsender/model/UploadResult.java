package com.broadcastsender.model;

import java.time.LocalDateTime;

/**
 * Model representing the result of an upload operation.
 */
public class UploadResult {
    private final Endpoint endpoint;
    private final FileAttachment file;
    private UploadStatus status;
    private String message;
    private int statusCode;
    private String responseBody;
    private LocalDateTime timestamp;
    private int attemptNumber;
    private double progress;
    
    public UploadResult(Endpoint endpoint, FileAttachment file) {
        this.endpoint = endpoint;
        this.file = file;
        this.status = UploadStatus.PENDING;
        this.timestamp = LocalDateTime.now();
        this.attemptNumber = 0;
        this.progress = 0.0;
    }
    
    public Endpoint getEndpoint() {
        return endpoint;
    }
    
    public FileAttachment getFile() {
        return file;
    }
    
    public UploadStatus getStatus() {
        return status;
    }
    
    public void setStatus(UploadStatus status) {
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
    
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
    
    public String getResponseBody() {
        return responseBody;
    }
    
    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public int getAttemptNumber() {
        return attemptNumber;
    }
    
    public void setAttemptNumber(int attemptNumber) {
        this.attemptNumber = attemptNumber;
    }
    
    public double getProgress() {
        return progress;
    }
    
    public void setProgress(double progress) {
        this.progress = progress;
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s -> %s: %s", 
            timestamp, 
            file.getName(), 
            endpoint.getName(), 
            status.getDisplayName());
    }
}
