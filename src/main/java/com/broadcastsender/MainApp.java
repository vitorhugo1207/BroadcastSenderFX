package com.broadcastsender;

import com.broadcastsender.view.MainView;
import com.broadcastsender.viewmodel.MainViewModel;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main application class for BroadcastSender FX.
 */
public class MainApp extends Application {
    private static final Logger logger = LoggerFactory.getLogger(MainApp.class);
    
    private MainViewModel viewModel;
    
    @Override
    public void start(Stage primaryStage) {
        try {
            logger.info("Starting BroadcastSender FX application");
            
            // Initialize ViewModel
            viewModel = new MainViewModel();
            
            // Create main view
            MainView mainView = new MainView(viewModel, primaryStage);
            
            // Create scene
            Scene scene = new Scene(mainView, 1200, 700);
            
            // Apply CSS if available
            try {
                String css = getClass().getResource("/css/styles.css").toExternalForm();
                scene.getStylesheets().add(css);
            } catch (Exception e) {
                logger.warn("Could not load CSS file", e);
            }
            
            // Configure stage
            primaryStage.setTitle("BroadcastSender FX - Multi-Endpoint File Uploader");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            
            // Handle close request
            primaryStage.setOnCloseRequest(event -> {
                logger.info("Application closing");
                viewModel.shutdown();
            });
            
            primaryStage.show();
            
            logger.info("Application started successfully");
            
        } catch (Exception e) {
            logger.error("Failed to start application", e);
            throw new RuntimeException("Failed to start application", e);
        }
    }
    
    @Override
    public void stop() {
        logger.info("Application stopping");
        if (viewModel != null) {
            viewModel.shutdown();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
