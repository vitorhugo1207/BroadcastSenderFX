package com.broadcastsender.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.UUID;

/**
 * Model representing an API endpoint configuration.
 */
public class Endpoint {
    private String id;
    private String name;
    private String url;
    private AuthType authType;
    private String username;
    private String password;
    private String bearerToken;
    
    public Endpoint() {
        this.id = UUID.randomUUID().toString();
        this.authType = AuthType.NONE;
    }
    
    @JsonCreator
    public Endpoint(@JsonProperty("id") String id,
                    @JsonProperty("name") String name,
                    @JsonProperty("url") String url,
                    @JsonProperty("authType") AuthType authType,
                    @JsonProperty("username") String username,
                    @JsonProperty("password") String password,
                    @JsonProperty("bearerToken") String bearerToken) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.name = name;
        this.url = url;
        this.authType = authType != null ? authType : AuthType.NONE;
        this.username = username;
        this.password = password;
        this.bearerToken = bearerToken;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public AuthType getAuthType() {
        return authType;
    }
    
    public void setAuthType(AuthType authType) {
        this.authType = authType;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getBearerToken() {
        return bearerToken;
    }
    
    public void setBearerToken(String bearerToken) {
        this.bearerToken = bearerToken;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Endpoint endpoint = (Endpoint) o;
        return Objects.equals(id, endpoint.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return name != null ? name : url;
    }
}
