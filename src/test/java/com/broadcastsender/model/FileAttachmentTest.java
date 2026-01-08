package com.broadcastsender.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileAttachmentTest {
    
    @Test
    void testFileAttachmentCreation(@TempDir Path tempDir) {
        try {
            // Create a temporary file for testing
            Path tempFile = Files.createTempFile(tempDir, "test-file", ".txt");
            
            FileAttachment attachment = new FileAttachment(tempFile.toFile());
            
            assertTrue(attachment.getName().startsWith("test-file"));
            assertEquals(tempFile.toFile(), attachment.getFile());
            assertTrue(attachment.getSize() >= 0);
            assertNotNull(attachment.getFormattedSize());
        } catch (Exception e) {
            fail("Failed to create test file: " + e.getMessage());
        }
    }
    
    @Test
    void testFormattedSize(@TempDir Path tempDir) {
        try {
            // Create a temporary file for testing
            Path tempFile = Files.createTempFile(tempDir, "test-formatted-size", ".txt");
            
            FileAttachment attachment = new FileAttachment(tempFile.toFile());
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
