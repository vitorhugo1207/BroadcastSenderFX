package com.broadcastsender.service;

import com.broadcastsender.model.AuthType;
import com.broadcastsender.model.Endpoint;
import com.broadcastsender.model.FileAttachment;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

/**
 * Service for handling HTTP requests using OkHttp.
 */
public class HttpService {
    private static final Logger logger = LoggerFactory.getLogger(HttpService.class);
    private static final MediaType MEDIA_TYPE_OCTET_STREAM = MediaType.parse("application/octet-stream");
    
    private final OkHttpClient client;
    
    public HttpService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
    }
    
    /**
     * Uploads a file to the specified endpoint with authentication.
     */
    public Response uploadFile(Endpoint endpoint, FileAttachment fileAttachment) throws IOException {
        File file = fileAttachment.getFile();
        
        logger.info("Uploading file {} to endpoint {}", file.getName(), endpoint.getUrl());
        
        // Build multipart request body
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        
        RequestBody fileBody = RequestBody.create(file, MEDIA_TYPE_OCTET_STREAM);
        bodyBuilder.addFormDataPart("file", file.getName(), fileBody);
        
        RequestBody requestBody = bodyBuilder.build();
        
        // Build request with authentication
        Request.Builder requestBuilder = new Request.Builder()
                .url(endpoint.getUrl())
                .post(requestBody);
        
        addAuthentication(requestBuilder, endpoint);
        
        Request request = requestBuilder.build();
        
        // Execute request
        Response response = client.newCall(request).execute();
        logger.info("Upload completed with status code: {}", response.code());
        
        return response;
    }
    
    /**
     * Adds authentication headers to the request based on endpoint configuration.
     */
    private void addAuthentication(Request.Builder requestBuilder, Endpoint endpoint) {
        AuthType authType = endpoint.getAuthType();
        
        if (authType == null || authType == AuthType.NONE) {
            return;
        }
        
        switch (authType) {
            case BEARER:
                if (endpoint.getBearerToken() != null && !endpoint.getBearerToken().isEmpty()) {
                    requestBuilder.addHeader("Authorization", "Bearer " + endpoint.getBearerToken());
                    logger.debug("Added Bearer token authentication");
                }
                break;
                
            case BASIC:
            case BASIC_BASE64:
                // Both BASIC and BASIC_BASE64 accept plain text credentials and encode to Base64
                // They are functionally identical but kept separate for user clarity
                if (endpoint.getUsername() != null && endpoint.getPassword() != null) {
                    String credentials = endpoint.getUsername() + ":" + endpoint.getPassword();
                    String encodedCredentials = Base64.getEncoder()
                            .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
                    requestBuilder.addHeader("Authorization", "Basic " + encodedCredentials);
                    logger.debug("Added Basic authentication (credentials encoded to Base64)");
                }
                break;
        }
    }
}
