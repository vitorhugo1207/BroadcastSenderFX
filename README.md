# BroadcastSender FX

![Java](https://img.shields.io/badge/Java-17-orange)
![JavaFX](https://img.shields.io/badge/JavaFX-17.0.2-blue)
![License](https://img.shields.io/badge/license-MIT-green)

A powerful and intuitive JavaFX desktop application for broadcasting HTTP POST requests with file attachments to multiple API endpoints simultaneously. Perfect for bulk file uploads, API testing, and data distribution across multiple services.

## ✨ Features

### 🎯 Core Capabilities

- **Multi-Endpoint Management**: Add, edit, and delete multiple API endpoints with persistent storage
- **Concurrent Uploads**: Configure simultaneous uploads (1-10 concurrent requests) with intelligent thread pool management
- **Multiple Authentication Methods**:
  - Bearer Token authentication
  - Basic Auth (username/password)
  - Basic Auth Base64 (automatic encoding)
  - No authentication option
- **File Attachment System**: Select and upload single or multiple files with visual feedback
- **Smart Retry Mechanism**: Configurable automatic retry for failed requests with manual retry option
- **Real-Time Progress Tracking**: 
  - Individual progress indicators for each upload
  - Overall progress bar showing total completion
  - Live status updates (pending, in progress, success, failed, retrying)
- **Configuration Management**: Export/import all settings and endpoints as JSON
- **Professional UI/UX**: Modern, clean interface with responsive layout and intuitive controls

### 📊 Dashboard Features

- Organized sections for endpoint management, file selection, and results
- Color-coded status indicators for quick visual feedback
- Detailed upload results table with file, endpoint, status, and message columns
- Real-time statistics showing success/failure counts

## 🛠️ Technical Stack

- **Java 17**: Modern Java features and performance
- **JavaFX 17.0.2**: Rich desktop UI framework
- **ControlsFX 11.2.1**: Enhanced UI components
- **OkHttp 4.12.0**: Reliable HTTP client with connection pooling
- **Jackson 2.16.1**: JSON serialization/deserialization
- **SLF4J + Logback**: Comprehensive logging framework
- **Gradle**: Build automation and dependency management

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

- **Java Development Kit (JDK) 17** or higher
  - Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://adoptium.net/)
  - Verify installation: `java -version`
- **Gradle** (optional, wrapper included)
  - The project includes Gradle wrapper (`gradlew`/`gradlew.bat`)

## 🚀 Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/vitorhugo1207/BroadcastSenderFX.git
cd BroadcastSenderFX
```

### 2. Build the Project

#### On Linux/macOS:
```bash
./gradlew build
```

#### On Windows:
```bash
gradlew.bat build
```

### 3. Run the Application

#### On Linux/macOS:
```bash
./gradlew run
```

#### On Windows:
```bash
gradlew.bat run
```

## 📖 Usage Guide

### Getting Started

1. **Launch the Application**: Run the application using the command above
2. **Add API Endpoints**:
   - Click "Add" button in the Endpoints section
   - Enter endpoint name, URL, and authentication details
   - Click "Save"
3. **Select Files**:
   - Click "Add Files" in the Files section
   - Select one or multiple files from your file system
4. **Configure Upload Settings**:
   - Set "Concurrent Uploads" (1-10 simultaneous uploads)
   - Set "Max Retry Attempts" (0-5 retry attempts for failures)
5. **Start Upload**:
   - Click "Start Upload" button
   - Monitor progress in real-time via progress bars and results table
6. **Retry Failed Uploads** (if needed):
   - Click "Retry Failed" to attempt failed uploads again

### Managing Endpoints

#### Add Endpoint
- Click "Add" in the Endpoints section
- Fill in the form:
  - **Name**: Descriptive name for the endpoint
  - **URL**: Full API endpoint URL
  - **Auth Type**: Select authentication method
  - Provide credentials based on auth type

#### Edit Endpoint
- Select an endpoint from the list
- Click "Edit"
- Modify fields as needed
- Click "Save"

#### Delete Endpoint
- Select an endpoint from the list
- Click "Delete"
- Confirm deletion

### Authentication Types

#### No Authentication
No credentials required - for public APIs.

#### Bearer Token
Provide a bearer token in the "Bearer Token" field.
```
Header: Authorization: Bearer <your-token>
```

#### Basic Auth
Provide username and password. Credentials are automatically encoded to Base64.
```
Header: Authorization: Basic <base64-encoded-credentials>
```

#### Basic Auth Base64
Same as Basic Auth - credentials are Base64 encoded before sending.

### File Management

- **Add Files**: Select single or multiple files using the file chooser
- **Remove File**: Select a file and click "Remove"
- **Clear All**: Remove all selected files at once

### Configuration Import/Export

#### Export Configuration
1. Click "Export Config" in the top bar
2. Choose save location and filename
3. Configuration saved as JSON file with all endpoints and settings

#### Import Configuration
1. Click "Import Config" in the top bar
2. Select a previously exported JSON configuration file
3. All endpoints and settings are loaded

### Example Configuration File

```json
{
  "maxConcurrentUploads": 3,
  "maxRetryAttempts": 2,
  "endpoints": [
    {
      "id": "endpoint-1",
      "name": "Production API",
      "url": "https://api.example.com/upload",
      "authType": "BEARER",
      "bearerToken": "your-token-here"
    }
  ]
}
```

## 📁 Project Structure

```
BroadcastSenderFX/
├── src/
│   ├── main/
│   │   ├── java/com/broadcastsender/
│   │   │   ├── model/              # Data models
│   │   │   │   ├── Endpoint.java
│   │   │   │   ├── FileAttachment.java
│   │   │   │   ├── UploadResult.java
│   │   │   │   └── ...
│   │   │   ├── viewmodel/          # ViewModels (MVVM)
│   │   │   │   └── MainViewModel.java
│   │   │   ├── view/               # UI Views
│   │   │   │   ├── MainView.java
│   │   │   │   └── EndpointDialog.java
│   │   │   ├── service/            # Business logic
│   │   │   │   ├── HttpService.java
│   │   │   │   └── UploadService.java
│   │   │   ├── repository/         # Data persistence
│   │   │   │   └── ConfigurationRepository.java
│   │   │   └── MainApp.java        # Application entry point
│   │   └── resources/
│   │       ├── css/                # Stylesheets
│   │       ├── config/             # Example configs
│   │       └── logback.xml         # Logging config
│   └── test/                       # Unit tests
├── build.gradle                    # Gradle build file
├── settings.gradle                 # Gradle settings
└── README.md                       # This file
```

## 🏗️ Architecture

The application follows the **MVVM (Model-View-ViewModel)** architecture pattern:

- **Model**: Data structures and business entities (`Endpoint`, `FileAttachment`, `UploadResult`)
- **View**: JavaFX UI components (`MainView`, `EndpointDialog`)
- **ViewModel**: Business logic and state management (`MainViewModel`)
- **Service**: HTTP operations and upload coordination
- **Repository**: Data persistence and configuration management

## 📝 Logging

The application uses SLF4J with Logback for comprehensive logging:

- **Console Output**: Real-time logs during execution
- **File Logs**: Stored in `~/.broadcastsender/logs/application.log`
- **Log Rotation**: Daily rotation with 30-day retention
- **Log Levels**: DEBUG for application code, INFO for general logs

View logs at: `${USER_HOME}/.broadcastsender/logs/`

## 🔧 Configuration Storage

Application configuration and endpoints are automatically saved to:
```
${USER_HOME}/.broadcastsender/config.json
```

This ensures your settings persist across application restarts.

## 🐛 Troubleshooting

### Issue: Application won't start

**Solution**:
- Verify Java 21 is installed: `java -version`
- Check if another instance is running
- Review logs in `~/.broadcastsender/logs/application.log`

### Issue: Upload fails with connection error

**Solution**:
- Verify the endpoint URL is correct and accessible
- Check network connectivity
- Ensure authentication credentials are valid
- Review error message in the results table

### Issue: Files not uploading

**Solution**:
- Ensure at least one endpoint is configured
- Check file permissions
- Verify the endpoint accepts multipart/form-data
- Review server-side logs if available

### Issue: JavaFX not found

**Solution**:
- Ensure you're using JDK 21 (JavaFX is bundled with the dependencies)
- Run `./gradlew --refresh-dependencies`
- Clean and rebuild: `./gradlew clean build`

### Issue: Gradle build fails

**Solution**:
- Ensure internet connection (for dependency download)
- Clear Gradle cache: `rm -rf ~/.gradle/caches/`
- Try: `./gradlew clean build --refresh-dependencies`

## 🔐 Security Notes

- **Credentials Storage**: Credentials are stored in plain text in the configuration file. For production use, consider implementing encryption.
- **HTTPS Recommended**: Always use HTTPS endpoints to protect data in transit
- **Token Management**: Rotate bearer tokens and credentials regularly
- **File Permissions**: Ensure configuration files have appropriate permissions

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👨‍💻 Author

**Vitor Hugo**

## 🙏 Acknowledgments

- JavaFX community for excellent documentation
- ControlsFX for enhanced UI components
- OkHttp team for the robust HTTP client
- All open-source contributors

## 📞 Support

For issues, questions, or suggestions, please open an issue on the GitHub repository.

---

**Happy Broadcasting! 🚀**
