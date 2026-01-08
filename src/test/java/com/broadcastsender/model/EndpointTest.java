package com.broadcastsender.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EndpointTest {
    
    @Test
    void testEndpointCreation() {
        Endpoint endpoint = new Endpoint();
        endpoint.setName("Test API");
        endpoint.setUrl("https://api.example.com/upload");
        endpoint.setAuthType(AuthType.BEARER);
        endpoint.setBearerToken("test-token");
        
        assertEquals("Test API", endpoint.getName());
        assertEquals("https://api.example.com/upload", endpoint.getUrl());
        assertEquals(AuthType.BEARER, endpoint.getAuthType());
        assertEquals("test-token", endpoint.getBearerToken());
        assertNotNull(endpoint.getId());
    }
    
    @Test
    void testEndpointWithBasicAuth() {
        Endpoint endpoint = new Endpoint();
        endpoint.setName("Basic Auth API");
        endpoint.setUrl("https://api.example.com/upload");
        endpoint.setAuthType(AuthType.BASIC);
        endpoint.setUsername("user");
        endpoint.setPassword("pass");
        
        assertEquals(AuthType.BASIC, endpoint.getAuthType());
        assertEquals("user", endpoint.getUsername());
        assertEquals("pass", endpoint.getPassword());
    }
    
    @Test
    void testEndpointEquality() {
        Endpoint endpoint1 = new Endpoint();
        endpoint1.setName("API 1");
        
        Endpoint endpoint2 = new Endpoint();
        endpoint2.setName("API 2");
        
        // Different IDs should not be equal
        assertNotEquals(endpoint1, endpoint2);
        
        // Same endpoint should be equal to itself
        assertEquals(endpoint1, endpoint1);
    }
}
