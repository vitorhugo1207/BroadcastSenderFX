package com.broadcastsender.model;

import java.io.File;
import java.util.Objects;

/**
 * Model representing a file attachment for upload.
 */
public class FileAttachment {
    private final File file;
    private final long size;
    private final String name;
    
    public FileAttachment(File file) {
        this.file = file;
        this.size = file.length();
        this.name = file.getName();
    }
    
    public File getFile() {
        return file;
    }
    
    public long getSize() {
        return size;
    }
    
    public String getName() {
        return name;
    }
    
    /**
     * Returns a human-readable file size string.
     */
    public String getFormattedSize() {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024.0 * 1024.0));
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileAttachment that = (FileAttachment) o;
        return Objects.equals(file.getAbsolutePath(), that.file.getAbsolutePath());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(file.getAbsolutePath());
    }
    
    @Override
    public String toString() {
        return name + " (" + getFormattedSize() + ")";
    }
}
