package com.broadcastsender.view;

import com.broadcastsender.model.*;
import com.broadcastsender.viewmodel.MainViewModel;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.StatusBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * Main view of the application with dashboard layout.
 */
public class MainView extends BorderPane {
    private static final Logger logger = LoggerFactory.getLogger(MainView.class);
    
    private final MainViewModel viewModel;
    private final Stage stage;
    
    // UI Components
    private ListView<Endpoint> endpointListView;
    private ListView<FileAttachment> fileListView;
    private TableView<UploadResult> resultsTableView;
    private Spinner<Integer> concurrentUploadSpinner;
    private Spinner<Integer> retryAttemptsSpinner;
    private ProgressBar overallProgressBar;
    private Button startUploadButton;
    private Button retryButton;
    private StatusBar statusBar;
    private Label statsLabel;
    
    public MainView(MainViewModel viewModel, Stage stage) {
        this.viewModel = viewModel;
        this.stage = stage;
        
        initializeUI();
        setupBindings();
    }
    
    private void initializeUI() {
        // Create main layout
        setTop(createTopBar());
        setCenter(createCenterContent());
        setBottom(createBottomBar());
        
        setPadding(new Insets(10));
    }
    
    private Node createTopBar() {
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(0, 0, 10, 0));
        topBar.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label("BroadcastSender FX");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button importButton = new Button("Import Config");
        importButton.setOnAction(e -> handleImportConfiguration());
        
        Button exportButton = new Button("Export Config");
        exportButton.setOnAction(e -> handleExportConfiguration());
        
        topBar.getChildren().addAll(titleLabel, spacer, importButton, exportButton);
        
        return topBar;
    }
    
    private Node createCenterContent() {
        // Create split pane for main content
        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.3, 0.7);
        
        // Left panel - Endpoints and Files
        VBox leftPanel = createLeftPanel();
        
        // Center panel - Configuration and Controls
        VBox centerPanel = createCenterPanel();
        
        // Right panel - Results
        VBox rightPanel = createRightPanel();
        
        splitPane.getItems().addAll(leftPanel, centerPanel, rightPanel);
        
        return splitPane;
    }
    
    private VBox createLeftPanel() {
        VBox leftPanel = new VBox(10);
        leftPanel.setPadding(new Insets(10));
        
        // Endpoints section
        Label endpointsLabel = new Label("API Endpoints");
        endpointsLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        endpointListView = new ListView<>();
        endpointListView.setItems(viewModel.getEndpoints());
        endpointListView.setPrefHeight(200);
        
        HBox endpointButtons = new HBox(5);
        Button addEndpointButton = new Button("Add");
        addEndpointButton.setOnAction(e -> handleAddEndpoint());
        
        Button editEndpointButton = new Button("Edit");
        editEndpointButton.setOnAction(e -> handleEditEndpoint());
        editEndpointButton.disableProperty().bind(
            endpointListView.getSelectionModel().selectedItemProperty().isNull()
        );
        
        Button deleteEndpointButton = new Button("Delete");
        deleteEndpointButton.setOnAction(e -> handleDeleteEndpoint());
        deleteEndpointButton.disableProperty().bind(
            endpointListView.getSelectionModel().selectedItemProperty().isNull()
        );
        
        endpointButtons.getChildren().addAll(addEndpointButton, editEndpointButton, deleteEndpointButton);
        
        // Files section
        Label filesLabel = new Label("Selected Files");
        filesLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        fileListView = new ListView<>();
        fileListView.setItems(viewModel.getSelectedFiles());
        fileListView.setPrefHeight(200);
        
        HBox fileButtons = new HBox(5);
        Button addFilesButton = new Button("Add Files");
        addFilesButton.setOnAction(e -> handleAddFiles());
        
        Button removeFileButton = new Button("Remove");
        removeFileButton.setOnAction(e -> handleRemoveFile());
        removeFileButton.disableProperty().bind(
            fileListView.getSelectionModel().selectedItemProperty().isNull()
        );
        
        Button clearFilesButton = new Button("Clear All");
        clearFilesButton.setOnAction(e -> viewModel.clearFiles());
        
        fileButtons.getChildren().addAll(addFilesButton, removeFileButton, clearFilesButton);
        
        leftPanel.getChildren().addAll(
            endpointsLabel, endpointListView, endpointButtons,
            new Separator(),
            filesLabel, fileListView, fileButtons
        );
        
        VBox.setVgrow(endpointListView, Priority.ALWAYS);
        VBox.setVgrow(fileListView, Priority.ALWAYS);
        
        return leftPanel;
    }
    
    private VBox createCenterPanel() {
        VBox centerPanel = new VBox(15);
        centerPanel.setPadding(new Insets(10));
        centerPanel.setAlignment(Pos.TOP_CENTER);
        
        // Configuration section
        Label configLabel = new Label("Upload Configuration");
        configLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        GridPane configGrid = new GridPane();
        configGrid.setHgap(10);
        configGrid.setVgap(10);
        configGrid.setMaxWidth(400);
        
        Label concurrentLabel = new Label("Concurrent Uploads:");
        SpinnerValueFactory<Integer> concurrentFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 3);
        concurrentUploadSpinner = new Spinner<>();
        concurrentUploadSpinner.setValueFactory(concurrentFactory);
        concurrentUploadSpinner.setPrefWidth(100);
        concurrentUploadSpinner.setEditable(true);
        
        // Bind to view model
        concurrentFactory.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                viewModel.updateConcurrentUploads(newVal);
            }
        });
        viewModel.maxConcurrentUploadsProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.equals(concurrentFactory.getValue())) {
                concurrentFactory.setValue(newVal.intValue());
            }
        });
        concurrentFactory.setValue(viewModel.maxConcurrentUploadsProperty().get());
        
        Label retryLabel = new Label("Max Retry Attempts:");
        SpinnerValueFactory<Integer> retryFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 5, 2);
        retryAttemptsSpinner = new Spinner<>();
        retryAttemptsSpinner.setValueFactory(retryFactory);
        retryAttemptsSpinner.setPrefWidth(100);
        retryAttemptsSpinner.setEditable(true);
        
        // Bind to view model
        retryFactory.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                viewModel.updateRetryAttempts(newVal);
            }
        });
        viewModel.maxRetryAttemptsProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.equals(retryFactory.getValue())) {
                retryFactory.setValue(newVal.intValue());
            }
        });
        retryFactory.setValue(viewModel.maxRetryAttemptsProperty().get());
        
        configGrid.add(concurrentLabel, 0, 0);
        configGrid.add(concurrentUploadSpinner, 1, 0);
        configGrid.add(retryLabel, 0, 1);
        configGrid.add(retryAttemptsSpinner, 1, 1);
        
        // Progress section
        Label progressLabel = new Label("Overall Progress");
        progressLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        overallProgressBar = new ProgressBar(0);
        overallProgressBar.setPrefWidth(400);
        overallProgressBar.progressProperty().bind(viewModel.overallProgressProperty());
        
        statsLabel = new Label("Ready");
        statsLabel.textProperty().bind(
            viewModel.successCountProperty().asString()
                .concat(" success, ")
                .concat(viewModel.failureCountProperty().asString())
                .concat(" failed, ")
                .concat(viewModel.totalCountProperty().asString())
                .concat(" total")
        );
        
        // Control buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        
        startUploadButton = new Button("Start Upload");
        startUploadButton.setStyle("-fx-font-size: 14px; -fx-padding: 10 20;");
        startUploadButton.setOnAction(e -> viewModel.startUpload());
        startUploadButton.disableProperty().bind(viewModel.isUploadingProperty());
        
        retryButton = new Button("Retry Failed");
        retryButton.setOnAction(e -> viewModel.retryFailedUploads());
        retryButton.disableProperty().bind(
            viewModel.isUploadingProperty().or(
                viewModel.failureCountProperty().isEqualTo(0)
            )
        );
        
        buttonBox.getChildren().addAll(startUploadButton, retryButton);
        
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        centerPanel.getChildren().addAll(
            configLabel, configGrid,
            new Separator(),
            progressLabel, overallProgressBar, statsLabel,
            spacer,
            buttonBox
        );
        
        return centerPanel;
    }
    
    private VBox createRightPanel() {
        VBox rightPanel = new VBox(10);
        rightPanel.setPadding(new Insets(10));
        
        Label resultsLabel = new Label("Upload Results");
        resultsLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        resultsTableView = new TableView<>();
        resultsTableView.setItems(viewModel.getUploadResults());
        
        TableColumn<UploadResult, String> fileColumn = new TableColumn<>("File");
        fileColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFile().getName())
        );
        fileColumn.setPrefWidth(150);
        
        TableColumn<UploadResult, String> endpointColumn = new TableColumn<>("Endpoint");
        endpointColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEndpoint().getName())
        );
        endpointColumn.setPrefWidth(120);
        
        TableColumn<UploadResult, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getStatus().getDisplayName()
            )
        );
        statusColumn.setPrefWidth(100);
        statusColumn.setCellFactory(column -> new TableCell<UploadResult, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    UploadResult result = getTableView().getItems().get(getIndex());
                    
                    switch (result.getStatus()) {
                        case SUCCESS:
                            setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                            break;
                        case FAILED:
                            setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                            break;
                        case IN_PROGRESS:
                        case RETRYING:
                            setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });
        
        TableColumn<UploadResult, String> messageColumn = new TableColumn<>("Message");
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        messageColumn.setPrefWidth(200);
        
        resultsTableView.getColumns().addAll(fileColumn, endpointColumn, statusColumn, messageColumn);
        
        rightPanel.getChildren().addAll(resultsLabel, resultsTableView);
        VBox.setVgrow(resultsTableView, Priority.ALWAYS);
        
        return rightPanel;
    }
    
    private Node createBottomBar() {
        statusBar = new StatusBar();
        statusBar.textProperty().bind(viewModel.statusMessageProperty());
        
        return statusBar;
    }
    
    private void setupBindings() {
        // Any additional bindings can be set up here
    }
    
    private void handleAddEndpoint() {
        EndpointDialog dialog = new EndpointDialog(stage);
        Optional<Endpoint> result = dialog.showAndWait();
        result.ifPresent(viewModel::addEndpoint);
    }
    
    private void handleEditEndpoint() {
        Endpoint selected = endpointListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            EndpointDialog dialog = new EndpointDialog(stage, selected);
            Optional<Endpoint> result = dialog.showAndWait();
            result.ifPresent(viewModel::updateEndpoint);
        }
    }
    
    private void handleDeleteEndpoint() {
        Endpoint selected = endpointListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Delete");
            alert.setHeaderText("Delete Endpoint");
            alert.setContentText("Are you sure you want to delete: " + selected.getName() + "?");
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                viewModel.removeEndpoint(selected);
            }
        }
    }
    
    private void handleAddFiles() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Files to Upload");
        List<File> files = fileChooser.showOpenMultipleDialog(stage);
        
        if (files != null && !files.isEmpty()) {
            viewModel.addFiles(files);
        }
    }
    
    private void handleRemoveFile() {
        FileAttachment selected = fileListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            viewModel.removeFile(selected);
        }
    }
    
    private void handleImportConfiguration() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Configuration");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("JSON Files", "*.json")
        );
        
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                viewModel.importConfiguration(file);
                showInfoAlert("Success", "Configuration imported successfully");
            } catch (Exception e) {
                logger.error("Failed to import configuration", e);
                showErrorAlert("Import Failed", "Failed to import configuration: " + e.getMessage());
            }
        }
    }
    
    private void handleExportConfiguration() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Configuration");
        fileChooser.setInitialFileName("broadcastsender-config.json");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("JSON Files", "*.json")
        );
        
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                viewModel.exportConfiguration(file);
                showInfoAlert("Success", "Configuration exported successfully");
            } catch (Exception e) {
                logger.error("Failed to export configuration", e);
                showErrorAlert("Export Failed", "Failed to export configuration: " + e.getMessage());
            }
        }
    }
    
    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
