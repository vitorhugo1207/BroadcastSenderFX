package com.broadcastsender.model;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class FileAttachmentTest {
    
    @Test
    void testFileAttachmentCreation() {
        // Create a temporary file for testing
        File tempFile = new File("/tmp/test-file.txt");
        try {
            tempFile.createNewFile();
            tempFile.deleteOnExit();
            
            FileAttachment attachment = new FileAttachment(tempFile);
            
            assertEquals("test-file.txt", attachment.getName());
            assertEquals(tempFile, attachment.getFile());
            assertTrue(attachment.getSize() >= 0);
            assertNotNull(attachment.getFormattedSize());
        } catch (Exception e) {
            fail("Failed to create test file: " + e.getMessage());
        }
    }
    
    @Test
    void testFormattedSize() {
        File tempFile = new File("/tmp/test-formatted-size.txt");
        try {
            tempFile.createNewFile();
            tempFile.deleteOnExit();
            
            FileAttachment attachment = new FileAttachment(tempFile);
            String formattedSize = attachment.getFormattedSize();
            
            // Should contain either B, KB, MB, or GB
            assertTrue(formattedSize.contains("B") || 
                      formattedSize.contains("KB") || 
                      formattedSize.contains("MB") || 
                      formattedSize.contains("GB"));
        } catch (Exception e) {
            fail("Failed to create test file: " + e.getMessage());
        }
    }
}
