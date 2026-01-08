package com.broadcastsender.view;

import com.broadcastsender.model.AuthType;
import com.broadcastsender.model.Endpoint;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * Dialog for adding or editing an endpoint.
 */
public class EndpointDialog extends Dialog<Endpoint> {
    
    private final TextField nameField;
    private final TextField urlField;
    private final ComboBox<AuthType> authTypeCombo;
    private final TextField usernameField;
    private final PasswordField passwordField;
    private final TextField bearerTokenField;
    private final GridPane authFieldsGrid;
    
    private Endpoint endpoint;
    
    public EndpointDialog(Stage owner) {
        this(owner, null);
    }
    
    public EndpointDialog(Stage owner, Endpoint existingEndpoint) {
        this.endpoint = existingEndpoint;
        
        initOwner(owner);
        setTitle(existingEndpoint == null ? "Add Endpoint" : "Edit Endpoint");
        setHeaderText(existingEndpoint == null ? "Add a new API endpoint" : "Edit API endpoint");
        
        // Create dialog buttons
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // Create form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        nameField = new TextField();
        nameField.setPromptText("Endpoint name");
        
        urlField = new TextField();
        urlField.setPromptText("https://api.example.com/upload");
        
        authTypeCombo = new ComboBox<>();
        authTypeCombo.getItems().addAll(AuthType.values());
        authTypeCombo.setValue(AuthType.NONE);
        authTypeCombo.setOnAction(e -> updateAuthFields());
        
        usernameField = new TextField();
        usernameField.setPromptText("Username");
        
        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        
        bearerTokenField = new TextField();
        bearerTokenField.setPromptText("Bearer token");
        
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("URL:"), 0, 1);
        grid.add(urlField, 1, 1);
        grid.add(new Label("Auth Type:"), 0, 2);
        grid.add(authTypeCombo, 1, 2);
        
        // Auth fields grid (dynamically shown/hidden)
        authFieldsGrid = new GridPane();
        authFieldsGrid.setHgap(10);
        authFieldsGrid.setVgap(10);
        
        grid.add(authFieldsGrid, 0, 3, 2, 1);
        
        getDialogPane().setContent(grid);
        
        // Load existing endpoint data if editing
        if (existingEndpoint != null) {
            nameField.setText(existingEndpoint.getName());
            urlField.setText(existingEndpoint.getUrl());
            authTypeCombo.setValue(existingEndpoint.getAuthType());
            usernameField.setText(existingEndpoint.getUsername());
            passwordField.setText(existingEndpoint.getPassword());
            bearerTokenField.setText(existingEndpoint.getBearerToken());
        }
        
        updateAuthFields();
        
        // Enable/disable save button
        Button saveButton = (Button) getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);
        
        // Validation
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty() || urlField.getText().trim().isEmpty());
        });
        
        urlField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty() || nameField.getText().trim().isEmpty());
        });
        
        // Result converter
        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (endpoint == null) {
                    endpoint = new Endpoint();
                }
                
                endpoint.setName(nameField.getText().trim());
                endpoint.setUrl(urlField.getText().trim());
                endpoint.setAuthType(authTypeCombo.getValue());
                endpoint.setUsername(usernameField.getText().trim());
                endpoint.setPassword(passwordField.getText());
                endpoint.setBearerToken(bearerTokenField.getText().trim());
                
                return endpoint;
            }
            return null;
        });
    }
    
    private void updateAuthFields() {
        authFieldsGrid.getChildren().clear();
        
        AuthType authType = authTypeCombo.getValue();
        if (authType == null || authType == AuthType.NONE) {
            return;
        }
        
        switch (authType) {
            case BEARER:
                authFieldsGrid.add(new Label("Bearer Token:"), 0, 0);
                authFieldsGrid.add(bearerTokenField, 1, 0);
                break;
                
            case BASIC:
            case BASIC_BASE64:
                authFieldsGrid.add(new Label("Username:"), 0, 0);
                authFieldsGrid.add(usernameField, 1, 0);
                authFieldsGrid.add(new Label("Password:"), 0, 1);
                authFieldsGrid.add(passwordField, 1, 1);
                
                if (authType == AuthType.BASIC_BASE64) {
                    Label infoLabel = new Label("(Will be encoded to Base64)");
                    infoLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");
                    authFieldsGrid.add(infoLabel, 1, 2);
                }
                break;
        }
    }
}
