package com.broadcastsender.viewmodel;

import com.broadcastsender.model.*;
import com.broadcastsender.repository.ConfigurationRepository;
import com.broadcastsender.service.HttpService;
import com.broadcastsender.service.UploadService;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Main ViewModel for the application.
 * Manages application state and business logic.
 */
public class MainViewModel {
    private static final Logger logger = LoggerFactory.getLogger(MainViewModel.class);
    
    // Services
    private final ConfigurationRepository configRepository;
    private final HttpService httpService;
    private final UploadService uploadService;
    
    // Observable properties
    private final ObservableList<Endpoint> endpoints;
    private final ObservableList<FileAttachment> selectedFiles;
    private final ObservableList<UploadResult> uploadResults;
    private final IntegerProperty maxConcurrentUploads;
    private final IntegerProperty maxRetryAttempts;
    private final BooleanProperty isUploading;
    private final DoubleProperty overallProgress;
    private final IntegerProperty successCount;
    private final IntegerProperty failureCount;
    private final IntegerProperty totalCount;
    private final StringProperty statusMessage;
    
    // Configuration
    private AppConfiguration configuration;
    
    public MainViewModel() {
        this.configRepository = new ConfigurationRepository();
        this.httpService = new HttpService();
        this.uploadService = new UploadService(httpService);
        
        this.endpoints = FXCollections.observableArrayList();
        this.selectedFiles = FXCollections.observableArrayList();
        this.uploadResults = FXCollections.observableArrayList();
        this.maxConcurrentUploads = new SimpleIntegerProperty(3);
        this.maxRetryAttempts = new SimpleIntegerProperty(2);
        this.isUploading = new SimpleBooleanProperty(false);
        this.overallProgress = new SimpleDoubleProperty(0.0);
        this.successCount = new SimpleIntegerProperty(0);
        this.failureCount = new SimpleIntegerProperty(0);
        this.totalCount = new SimpleIntegerProperty(0);
        this.statusMessage = new SimpleStringProperty("Ready");
        
        // Load configuration
        loadConfiguration();
        
        // Configure upload service
        updateUploadServiceConfig();
    }
    
    /**
     * Loads the application configuration.
     */
    public void loadConfiguration() {
        logger.info("Loading application configuration");
        configuration = configRepository.load();
        endpoints.setAll(configuration.getEndpoints());
        maxConcurrentUploads.set(configuration.getMaxConcurrentUploads());
        maxRetryAttempts.set(configuration.getMaxRetryAttempts());
        updateUploadServiceConfig();
    }
    
    /**
     * Saves the current configuration.
     */
    public void saveConfiguration() {
        logger.info("Saving application configuration");
        configuration.setEndpoints(new ArrayList<>(endpoints));
        configuration.setMaxConcurrentUploads(maxConcurrentUploads.get());
        configuration.setMaxRetryAttempts(maxRetryAttempts.get());
        configRepository.save(configuration);
    }
    
    /**
     * Exports configuration to a file.
     */
    public void exportConfiguration(File targetFile) throws Exception {
        configuration.setEndpoints(new ArrayList<>(endpoints));
        configuration.setMaxConcurrentUploads(maxConcurrentUploads.get());
        configuration.setMaxRetryAttempts(maxRetryAttempts.get());
        configRepository.exportConfiguration(configuration, targetFile);
        logger.info("Configuration exported to: {}", targetFile.getAbsolutePath());
    }
    
    /**
     * Imports configuration from a file.
     */
    public void importConfiguration(File sourceFile) throws Exception {
        configuration = configRepository.importConfiguration(sourceFile);
        endpoints.setAll(configuration.getEndpoints());
        maxConcurrentUploads.set(configuration.getMaxConcurrentUploads());
        maxRetryAttempts.set(configuration.getMaxRetryAttempts());
        updateUploadServiceConfig();
        saveConfiguration();
        logger.info("Configuration imported from: {}", sourceFile.getAbsolutePath());
    }
    
    /**
     * Updates the upload service configuration.
     */
    private void updateUploadServiceConfig() {
        uploadService.configureThreadPool(maxConcurrentUploads.get());
        uploadService.setMaxRetryAttempts(maxRetryAttempts.get());
    }
    
    /**
     * Adds an endpoint to the list.
     */
    public void addEndpoint(Endpoint endpoint) {
        endpoints.add(endpoint);
        saveConfiguration();
        logger.info("Added endpoint: {}", endpoint.getName());
    }
    
    /**
     * Updates an existing endpoint.
     */
    public void updateEndpoint(Endpoint endpoint) {
        int index = endpoints.indexOf(endpoint);
        if (index >= 0) {
            endpoints.set(index, endpoint);
            saveConfiguration();
            logger.info("Updated endpoint: {}", endpoint.getName());
        }
    }
    
    /**
     * Removes an endpoint from the list.
     */
    public void removeEndpoint(Endpoint endpoint) {
        endpoints.remove(endpoint);
        saveConfiguration();
        logger.info("Removed endpoint: {}", endpoint.getName());
    }
    
    /**
     * Adds files to the selection list.
     */
    public void addFiles(List<File> files) {
        for (File file : files) {
            FileAttachment attachment = new FileAttachment(file);
            if (!selectedFiles.contains(attachment)) {
                selectedFiles.add(attachment);
            }
        }
        logger.info("Added {} files to selection", files.size());
    }
    
    /**
     * Removes a file from the selection list.
     */
    public void removeFile(FileAttachment file) {
        selectedFiles.remove(file);
        logger.info("Removed file: {}", file.getName());
    }
    
    /**
     * Clears all selected files.
     */
    public void clearFiles() {
        selectedFiles.clear();
        logger.info("Cleared all selected files");
    }
    
    /**
     * Starts the upload process.
     */
    public void startUpload() {
        if (selectedFiles.isEmpty()) {
            statusMessage.set("No files selected");
            return;
        }
        
        if (endpoints.isEmpty()) {
            statusMessage.set("No endpoints configured");
            return;
        }
        
        isUploading.set(true);
        uploadResults.clear();
        successCount.set(0);
        failureCount.set(0);
        totalCount.set(selectedFiles.size() * endpoints.size());
        overallProgress.set(0.0);
        statusMessage.set("Uploading...");
        
        logger.info("Starting upload of {} files to {} endpoints", selectedFiles.size(), endpoints.size());
        
        updateUploadServiceConfig();
        
        uploadService.uploadFiles(
            new ArrayList<>(selectedFiles),
            new ArrayList<>(endpoints),
            this::handleUploadProgress
        ).thenAccept(results -> {
            Platform.runLater(() -> {
                isUploading.set(false);
                updateOverallProgress();
                statusMessage.set(String.format("Upload completed: %d success, %d failed", 
                    successCount.get(), failureCount.get()));
                logger.info("Upload completed: {} success, {} failed", 
                    successCount.get(), failureCount.get());
            });
        });
    }
    
    /**
     * Handles progress updates from the upload service.
     */
    private void handleUploadProgress(UploadResult result) {
        Platform.runLater(() -> {
            // Find and update or add the result
            int existingIndex = -1;
            for (int i = 0; i < uploadResults.size(); i++) {
                UploadResult existing = uploadResults.get(i);
                if (existing.getEndpoint().equals(result.getEndpoint()) &&
                    existing.getFile().equals(result.getFile())) {
                    existingIndex = i;
                    break;
                }
            }
            
            if (existingIndex >= 0) {
                uploadResults.set(existingIndex, result);
            } else {
                uploadResults.add(result);
            }
            
            // Update counters
            long success = uploadResults.stream()
                .filter(r -> r.getStatus() == UploadStatus.SUCCESS)
                .count();
            long failed = uploadResults.stream()
                .filter(r -> r.getStatus() == UploadStatus.FAILED)
                .count();
            
            successCount.set((int) success);
            failureCount.set((int) failed);
            
            updateOverallProgress();
        });
    }
    
    /**
     * Updates the overall progress based on upload results.
     */
    private void updateOverallProgress() {
        if (totalCount.get() == 0) {
            overallProgress.set(0.0);
            return;
        }
        
        long completed = uploadResults.stream()
            .filter(r -> r.getStatus() == UploadStatus.SUCCESS || r.getStatus() == UploadStatus.FAILED)
            .count();
        
        overallProgress.set((double) completed / totalCount.get());
    }
    
    /**
     * Retries failed uploads.
     */
    public void retryFailedUploads() {
        List<UploadResult> failedResults = uploadResults.stream()
            .filter(r -> r.getStatus() == UploadStatus.FAILED)
            .collect(Collectors.toList());
        
        if (failedResults.isEmpty()) {
            statusMessage.set("No failed uploads to retry");
            return;
        }
        
        isUploading.set(true);
        statusMessage.set("Retrying failed uploads...");
        
        logger.info("Retrying {} failed uploads", failedResults.size());
        
        uploadService.retryFailedUploads(failedResults, this::handleUploadProgress)
            .thenRun(() -> {
                Platform.runLater(() -> {
                    isUploading.set(false);
                    updateOverallProgress();
                    statusMessage.set(String.format("Retry completed: %d success, %d failed", 
                        successCount.get(), failureCount.get()));
                    logger.info("Retry completed: {} success, {} failed", 
                        successCount.get(), failureCount.get());
                });
            });
    }
    
    /**
     * Updates the concurrent upload configuration.
     */
    public void updateConcurrentUploads(int value) {
        maxConcurrentUploads.set(value);
        saveConfiguration();
        updateUploadServiceConfig();
    }
    
    /**
     * Updates the retry attempts configuration.
     */
    public void updateRetryAttempts(int value) {
        maxRetryAttempts.set(value);
        saveConfiguration();
        updateUploadServiceConfig();
    }
    
    /**
     * Shuts down the services.
     */
    public void shutdown() {
        uploadService.shutdown();
        logger.info("MainViewModel shut down");
    }
    
    // Getters for observable properties
    public ObservableList<Endpoint> getEndpoints() {
        return endpoints;
    }
    
    public ObservableList<FileAttachment> getSelectedFiles() {
        return selectedFiles;
    }
    
    public ObservableList<UploadResult> getUploadResults() {
        return uploadResults;
    }
    
    public IntegerProperty maxConcurrentUploadsProperty() {
        return maxConcurrentUploads;
    }
    
    public IntegerProperty maxRetryAttemptsProperty() {
        return maxRetryAttempts;
    }
    
    public BooleanProperty isUploadingProperty() {
        return isUploading;
    }
    
    public DoubleProperty overallProgressProperty() {
        return overallProgress;
    }
    
    public IntegerProperty successCountProperty() {
        return successCount;
    }
    
    public IntegerProperty failureCountProperty() {
        return failureCount;
    }
    
    public IntegerProperty totalCountProperty() {
        return totalCount;
    }
    
    public StringProperty statusMessageProperty() {
        return statusMessage;
    }
}
