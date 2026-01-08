package com.broadcastsender.repository;

import com.broadcastsender.model.AppConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Repository for persisting and loading application configuration.
 */
public class ConfigurationRepository {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationRepository.class);
    private static final String CONFIG_DIR = System.getProperty("user.home") + "/.broadcastsender";
    private static final String CONFIG_FILE = "config.json";
    
    private final ObjectMapper objectMapper;
    private final Path configPath;
    
    public ConfigurationRepository() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapper.registerModule(new JavaTimeModule());
        this.configPath = Paths.get(CONFIG_DIR, CONFIG_FILE);
        
        // Ensure config directory exists
        try {
            Files.createDirectories(Paths.get(CONFIG_DIR));
        } catch (IOException e) {
            logger.error("Failed to create configuration directory", e);
        }
    }
    
    /**
     * Loads the application configuration from file.
     * Returns a default configuration if file doesn't exist.
     */
    public AppConfiguration load() {
        try {
            File file = configPath.toFile();
            if (file.exists()) {
                logger.info("Loading configuration from: {}", configPath);
                return objectMapper.readValue(file, AppConfiguration.class);
            } else {
                logger.info("Configuration file not found, creating default configuration");
                AppConfiguration defaultConfig = new AppConfiguration();
                save(defaultConfig);
                return defaultConfig;
            }
        } catch (IOException e) {
            logger.error("Failed to load configuration", e);
            return new AppConfiguration();
        }
    }
    
    /**
     * Saves the application configuration to file.
     */
    public void save(AppConfiguration config) {
        try {
            logger.info("Saving configuration to: {}", configPath);
            objectMapper.writeValue(configPath.toFile(), config);
            logger.info("Configuration saved successfully");
        } catch (IOException e) {
            logger.error("Failed to save configuration", e);
        }
    }
    
    /**
     * Exports the configuration to a specified file.
     */
    public void exportConfiguration(AppConfiguration config, File targetFile) throws IOException {
        logger.info("Exporting configuration to: {}", targetFile.getAbsolutePath());
        objectMapper.writeValue(targetFile, config);
        logger.info("Configuration exported successfully");
    }
    
    /**
     * Imports the configuration from a specified file.
     */
    public AppConfiguration importConfiguration(File sourceFile) throws IOException {
        logger.info("Importing configuration from: {}", sourceFile.getAbsolutePath());
        AppConfiguration config = objectMapper.readValue(sourceFile, AppConfiguration.class);
        logger.info("Configuration imported successfully");
        return config;
    }
}
