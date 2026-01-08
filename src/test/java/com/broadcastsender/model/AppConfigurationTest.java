package com.broadcastsender.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppConfigurationTest {
    
    @Test
    void testDefaultConfiguration() {
        AppConfiguration config = new AppConfiguration();
        
        assertEquals(3, config.getMaxConcurrentUploads());
        assertEquals(2, config.getMaxRetryAttempts());
        assertNotNull(config.getEndpoints());
        assertTrue(config.getEndpoints().isEmpty());
    }
    
    @Test
    void testConfigurationWithEndpoints() {
        AppConfiguration config = new AppConfiguration();
        
        Endpoint endpoint1 = new Endpoint();
        endpoint1.setName("API 1");
        endpoint1.setUrl("https://api1.example.com");
        
        Endpoint endpoint2 = new Endpoint();
        endpoint2.setName("API 2");
        endpoint2.setUrl("https://api2.example.com");
        
        config.getEndpoints().add(endpoint1);
        config.getEndpoints().add(endpoint2);
        
        assertEquals(2, config.getEndpoints().size());
    }
    
    @Test
    void testConfigurationSettings() {
        AppConfiguration config = new AppConfiguration();
        
        config.setMaxConcurrentUploads(5);
        config.setMaxRetryAttempts(3);
        
        assertEquals(5, config.getMaxConcurrentUploads());
        assertEquals(3, config.getMaxRetryAttempts());
    }
}
