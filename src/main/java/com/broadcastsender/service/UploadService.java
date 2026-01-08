package com.broadcastsender.service;

import com.broadcastsender.model.Endpoint;
import com.broadcastsender.model.FileAttachment;
import com.broadcastsender.model.UploadResult;
import com.broadcastsender.model.UploadStatus;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * Service for managing file uploads with concurrent processing and retry logic.
 */
public class UploadService {
    private static final Logger logger = LoggerFactory.getLogger(UploadService.class);
    
    private final HttpService httpService;
    private ExecutorService executorService;
    private int maxRetryAttempts;
    
    public UploadService(HttpService httpService) {
        this.httpService = httpService;
        this.maxRetryAttempts = 2;
    }
    
    /**
     * Configures the thread pool for concurrent uploads.
     */
    public void configureThreadPool(int maxConcurrentUploads) {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        
        this.executorService = Executors.newFixedThreadPool(maxConcurrentUploads);
        logger.info("Thread pool configured with {} threads", maxConcurrentUploads);
    }
    
    /**
     * Sets the maximum number of retry attempts for failed uploads.
     */
    public void setMaxRetryAttempts(int maxRetryAttempts) {
        this.maxRetryAttempts = maxRetryAttempts;
    }
    
    /**
     * Uploads files to multiple endpoints with progress callback.
     */
    public CompletableFuture<List<UploadResult>> uploadFiles(
            List<FileAttachment> files,
            List<Endpoint> endpoints,
            Consumer<UploadResult> progressCallback) {
        
        if (executorService == null || executorService.isShutdown()) {
            configureThreadPool(3); // Default to 3 threads
        }
        
        List<UploadResult> results = new CopyOnWriteArrayList<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        
        // Create upload tasks for each file-endpoint combination
        for (FileAttachment file : files) {
            for (Endpoint endpoint : endpoints) {
                UploadResult result = new UploadResult(endpoint, file);
                results.add(result);
                
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    uploadWithRetry(result, progressCallback);
                }, executorService);
                
                futures.add(future);
            }
        }
        
        // Wait for all uploads to complete
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> results);
    }
    
    /**
     * Uploads a single file with retry logic.
     */
    private void uploadWithRetry(UploadResult result, Consumer<UploadResult> progressCallback) {
        int attempts = 0;
        boolean success = false;
        
        while (attempts <= maxRetryAttempts && !success) {
            attempts++;
            result.setAttemptNumber(attempts);
            
            if (attempts > 1) {
                result.setStatus(UploadStatus.RETRYING);
                result.setMessage("Retry attempt " + attempts + " of " + maxRetryAttempts);
                if (progressCallback != null) {
                    progressCallback.accept(result);
                }
                logger.info("Retrying upload: {} to {} (attempt {})", 
                        result.getFile().getName(), 
                        result.getEndpoint().getUrl(), 
                        attempts);
            } else {
                result.setStatus(UploadStatus.IN_PROGRESS);
                result.setMessage("Uploading...");
                if (progressCallback != null) {
                    progressCallback.accept(result);
                }
            }
            
            try {
                Response response = httpService.uploadFile(result.getEndpoint(), result.getFile());
                
                result.setStatusCode(response.code());
                
                if (response.isSuccessful()) {
                    result.setStatus(UploadStatus.SUCCESS);
                    result.setMessage("Upload successful");
                    result.setResponseBody(response.body() != null ? response.body().string() : "");
                    success = true;
                    logger.info("Upload successful: {} to {}", 
                            result.getFile().getName(), 
                            result.getEndpoint().getUrl());
                } else {
                    result.setStatus(UploadStatus.FAILED);
                    result.setMessage("HTTP " + response.code() + ": " + response.message());
                    result.setResponseBody(response.body() != null ? response.body().string() : "");
                    logger.warn("Upload failed with status {}: {} to {}", 
                            response.code(),
                            result.getFile().getName(), 
                            result.getEndpoint().getUrl());
                }
                
                response.close();
                
            } catch (Exception e) {
                result.setStatus(UploadStatus.FAILED);
                result.setMessage("Error: " + e.getMessage());
                logger.error("Upload error: {} to {}", 
                        result.getFile().getName(), 
                        result.getEndpoint().getUrl(), 
                        e);
            }
            
            result.setProgress(1.0);
            if (progressCallback != null) {
                progressCallback.accept(result);
            }
        }
        
        if (!success) {
            result.setStatus(UploadStatus.FAILED);
            result.setMessage("Upload failed after " + attempts + " attempts");
            if (progressCallback != null) {
                progressCallback.accept(result);
            }
        }
    }
    
    /**
     * Retries failed uploads.
     */
    public CompletableFuture<Void> retryFailedUploads(
            List<UploadResult> failedResults,
            Consumer<UploadResult> progressCallback) {
        
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        
        for (UploadResult result : failedResults) {
            if (result.getStatus() == UploadStatus.FAILED) {
                result.setAttemptNumber(0); // Reset attempt counter
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    uploadWithRetry(result, progressCallback);
                }, executorService);
                
                futures.add(future);
            }
        }
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }
    
    /**
     * Shuts down the executor service.
     */
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            logger.info("Upload service shut down");
        }
    }
}
