# BroadcastSender FX - Configuration Schema

This document describes the JSON schema used for configuration files.

## Configuration File Structure

The configuration file (`config.json`) contains application settings and endpoint definitions.

### Root Schema

```json
{
  "maxConcurrentUploads": <integer>,
  "maxRetryAttempts": <integer>,
  "endpoints": [<Endpoint>]
}
```

### Fields

#### maxConcurrentUploads
- **Type**: Integer
- **Range**: 1-10
- **Default**: 3
- **Description**: Maximum number of simultaneous file uploads

#### maxRetryAttempts
- **Type**: Integer
- **Range**: 0-5
- **Default**: 2
- **Description**: Number of retry attempts for failed uploads

#### endpoints
- **Type**: Array of Endpoint objects
- **Default**: Empty array
- **Description**: List of configured API endpoints

## Endpoint Schema

Each endpoint in the `endpoints` array has the following structure:

```json
{
  "id": <string>,
  "name": <string>,
  "url": <string>,
  "authType": <string>,
  "username": <string|null>,
  "password": <string|null>,
  "bearerToken": <string|null>
}
```

### Endpoint Fields

#### id
- **Type**: String (UUID)
- **Required**: Yes (auto-generated if not provided)
- **Description**: Unique identifier for the endpoint

#### name
- **Type**: String
- **Required**: Yes
- **Description**: Human-readable name for the endpoint

#### url
- **Type**: String (URL)
- **Required**: Yes
- **Description**: Full URL of the API endpoint
- **Example**: `"https://api.example.com/upload"`

#### authType
- **Type**: String (Enum)
- **Required**: Yes
- **Default**: "NONE"
- **Allowed Values**:
  - `"NONE"` - No authentication
  - `"BEARER"` - Bearer token authentication
  - `"BASIC"` - Basic authentication
  - `"BASIC_BASE64"` - Basic authentication with Base64 encoding
- **Description**: Authentication method to use

#### username
- **Type**: String or null
- **Required**: Only for `BASIC` and `BASIC_BASE64` auth types
- **Description**: Username for basic authentication

#### password
- **Type**: String or null
- **Required**: Only for `BASIC` and `BASIC_BASE64` auth types
- **Description**: Password for basic authentication
- **Security Note**: Stored in plain text; use secure file permissions

#### bearerToken
- **Type**: String or null
- **Required**: Only for `BEARER` auth type
- **Description**: Bearer token for authentication
- **Security Note**: Stored in plain text; use secure file permissions

## Complete Example

```json
{
  "maxConcurrentUploads": 5,
  "maxRetryAttempts": 3,
  "endpoints": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "name": "Production API",
      "url": "https://api.production.com/v1/upload",
      "authType": "BEARER",
      "username": null,
      "password": null,
      "bearerToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    },
    {
      "id": "660e8400-e29b-41d4-a716-446655440001",
      "name": "Staging API",
      "url": "https://api.staging.com/v1/upload",
      "authType": "BASIC",
      "username": "admin",
      "password": "secretpassword",
      "bearerToken": null
    },
    {
      "id": "770e8400-e29b-41d4-a716-446655440002",
      "name": "Development API",
      "url": "https://api.dev.com/v1/upload",
      "authType": "BASIC_BASE64",
      "username": "developer",
      "password": "devpass123",
      "bearerToken": null
    },
    {
      "id": "880e8400-e29b-41d4-a716-446655440003",
      "name": "Public API",
      "url": "https://public-api.com/upload",
      "authType": "NONE",
      "username": null,
      "password": null,
      "bearerToken": null
    }
  ]
}
```

## Authentication Details

### NONE
No authentication headers are sent.

```
(No headers)
```

### BEARER
Adds Authorization header with bearer token:

```
Authorization: Bearer <bearerToken>
```

### BASIC
Adds Authorization header with Base64-encoded credentials:

```
Authorization: Basic <base64(username:password)>
```

The application automatically encodes the username and password.

### BASIC_BASE64
Same as BASIC - credentials are Base64-encoded before sending:

```
Authorization: Basic <base64(username:password)>
```

## File Storage Location

The configuration file is automatically saved to:

- **Linux/macOS**: `~/.broadcastsender/config.json`
- **Windows**: `%USERPROFILE%\.broadcastsender\config.json`

## Import/Export

### Export
1. Click "Export Config" button
2. Choose destination file
3. Configuration is saved with all endpoints and settings

### Import
1. Click "Import Config" button
2. Select a previously exported JSON file
3. All settings and endpoints are loaded
4. Existing configuration is replaced

## Validation

The application performs the following validations:

1. **URL Format**: URLs must be valid and well-formed
2. **Auth Type**: Must be one of the allowed values
3. **Required Fields**: Name and URL are required for all endpoints
4. **Auth Credentials**: Username/password required for BASIC/BASIC_BASE64, token required for BEARER
5. **Concurrent Uploads**: Must be between 1 and 10
6. **Retry Attempts**: Must be between 0 and 5

## Security Considerations

⚠️ **Important Security Notes:**

1. **Plain Text Storage**: Credentials are stored in plain text in the configuration file
2. **File Permissions**: Set appropriate file permissions on the config file:
   - Linux/macOS: `chmod 600 ~/.broadcastsender/config.json`
   - Windows: Use NTFS permissions to restrict access
3. **Sensitive Data**: Consider using environment variables or a secrets manager for production
4. **Token Rotation**: Regularly rotate bearer tokens and passwords
5. **Backup Security**: Ensure exported configuration files are stored securely

## Troubleshooting

### Invalid JSON Format
If you edit the configuration file manually and encounter errors:
1. Validate JSON syntax using an online JSON validator
2. Check for missing commas, brackets, or quotes
3. Ensure all required fields are present

### Authentication Failures
1. Verify credentials are correct
2. Check that the auth type matches the API requirements
3. Ensure tokens haven't expired
4. Review API endpoint documentation

### Configuration Not Loading
1. Check file permissions
2. Verify file is in the correct location
3. Check application logs in `~/.broadcastsender/logs/application.log`
4. Delete corrupt config file to reset to defaults
