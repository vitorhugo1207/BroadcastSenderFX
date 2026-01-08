package com.broadcastsender.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Model representing the application configuration.
 */
public class AppConfiguration {
    private int maxConcurrentUploads;
    private int maxRetryAttempts;
    private List<Endpoint> endpoints;
    
    public AppConfiguration() {
        this.maxConcurrentUploads = 3;
        this.maxRetryAttempts = 2;
        this.endpoints = new ArrayList<>();
    }
    
    @JsonCreator
    public AppConfiguration(@JsonProperty("maxConcurrentUploads") Integer maxConcurrentUploads,
                           @JsonProperty("maxRetryAttempts") Integer maxRetryAttempts,
                           @JsonProperty("endpoints") List<Endpoint> endpoints) {
        this.maxConcurrentUploads = maxConcurrentUploads != null ? maxConcurrentUploads : 3;
        this.maxRetryAttempts = maxRetryAttempts != null ? maxRetryAttempts : 2;
        this.endpoints = endpoints != null ? endpoints : new ArrayList<>();
    }
    
    public int getMaxConcurrentUploads() {
        return maxConcurrentUploads;
    }
    
    public void setMaxConcurrentUploads(int maxConcurrentUploads) {
        this.maxConcurrentUploads = maxConcurrentUploads;
    }
    
    public int getMaxRetryAttempts() {
        return maxRetryAttempts;
    }
    
    public void setMaxRetryAttempts(int maxRetryAttempts) {
        this.maxRetryAttempts = maxRetryAttempts;
    }
    
    public List<Endpoint> getEndpoints() {
        return endpoints;
    }
    
    public void setEndpoints(List<Endpoint> endpoints) {
        this.endpoints = endpoints;
    }
}
