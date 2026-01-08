# Contributing to BroadcastSender FX

Thank you for your interest in contributing to BroadcastSender FX! This guide will help you get started.

## Development Setup

### Prerequisites

- Java Development Kit (JDK) 17 or higher
- Git
- Your favorite Java IDE (IntelliJ IDEA, Eclipse, VS Code, etc.)

### Getting Started

1. Fork the repository on GitHub
2. Clone your fork locally:
   ```bash
   git clone https://github.com/YOUR_USERNAME/BroadcastSenderFX.git
   cd BroadcastSenderFX
   ```

3. Build the project:
   ```bash
   ./gradlew build
   ```

4. Run tests:
   ```bash
   ./gradlew test
   ```

5. Run the application:
   ```bash
   ./gradlew run
   ```

## Project Structure

```
BroadcastSenderFX/
├── src/main/java/com/broadcastsender/
│   ├── model/              # Data models (POJOs)
│   ├── viewmodel/          # ViewModels (MVVM pattern)
│   ├── view/               # JavaFX UI components
│   ├── service/            # Business logic services
│   ├── repository/         # Data persistence
│   └── MainApp.java        # Application entry point
├── src/main/resources/
│   ├── css/                # Stylesheets
│   ├── config/             # Configuration examples
│   └── logback.xml         # Logging configuration
└── src/test/               # Unit tests
```

## Coding Standards

### Java Style Guide

- Follow standard Java naming conventions
- Use meaningful variable and method names
- Keep methods focused and concise (Single Responsibility Principle)
- Maximum line length: 120 characters
- Use 4 spaces for indentation (no tabs)

### Documentation

- Add JavaDoc comments for all public classes and methods
- Include @param, @return, and @throws tags where appropriate
- Document complex logic with inline comments
- Update README.md if adding new features

### Example:

```java
/**
 * Uploads a file to the specified endpoint with authentication.
 * 
 * @param endpoint The API endpoint configuration
 * @param fileAttachment The file to upload
 * @return Response object from the server
 * @throws IOException If network error occurs during upload
 */
public Response uploadFile(Endpoint endpoint, FileAttachment fileAttachment) throws IOException {
    // Implementation
}
```

## MVVM Architecture

This project follows the MVVM (Model-View-ViewModel) pattern:

### Model
- Plain Java objects (POJOs)
- No business logic
- Represent data structures

### View
- JavaFX UI components
- No business logic
- Binds to ViewModel properties

### ViewModel
- Contains business logic
- Exposes Observable properties for View binding
- Coordinates Services and Repositories

### Service
- Handles external operations (HTTP, file I/O)
- Reusable business logic

### Repository
- Data persistence and retrieval
- JSON file handling

## Making Changes

### Creating a Branch

Create a feature branch for your changes:
```bash
git checkout -b feature/your-feature-name
```

Or for bug fixes:
```bash
git checkout -b bugfix/bug-description
```

### Committing Changes

- Write clear, concise commit messages
- Start with a verb in present tense (Add, Fix, Update, Remove)
- Reference issue numbers when applicable

Good commit messages:
```
Add retry mechanism for failed uploads
Fix concurrent upload thread pool leak
Update README with new authentication options
Remove deprecated authentication method
```

### Testing

- Write unit tests for new features
- Ensure all existing tests pass
- Test manually in the application
- Test edge cases and error scenarios

Run tests:
```bash
./gradlew test
```

### Code Review Checklist

Before submitting a pull request, ensure:

- [ ] Code builds successfully
- [ ] All tests pass
- [ ] New features have tests
- [ ] Code follows style guidelines
- [ ] Documentation is updated
- [ ] No unnecessary dependencies added
- [ ] Logging is appropriate (not too verbose)
- [ ] Error handling is in place
- [ ] Security considerations addressed

## Submitting Changes

### Pull Request Process

1. Commit your changes:
   ```bash
   git add .
   git commit -m "Your descriptive commit message"
   ```

2. Push to your fork:
   ```bash
   git push origin feature/your-feature-name
   ```

3. Create a Pull Request on GitHub:
   - Provide a clear title and description
   - Reference related issues
   - Include screenshots for UI changes
   - List any breaking changes

4. Wait for review and address feedback

### Pull Request Template

```markdown
## Description
Brief description of changes

## Related Issues
Fixes #123

## Changes Made
- Added feature X
- Fixed bug Y
- Updated documentation for Z

## Testing
- [ ] Unit tests added/updated
- [ ] Manual testing completed
- [ ] All tests passing

## Screenshots (if applicable)
[Add screenshots here]

## Breaking Changes
[List any breaking changes]
```

## Feature Requests

Have an idea for a new feature? Great! Here's how to suggest it:

1. Check existing issues to avoid duplicates
2. Open a new issue with the "enhancement" label
3. Describe the feature and use case
4. Explain why it would be valuable
5. Provide examples or mockups if possible

## Bug Reports

Found a bug? Help us fix it:

1. Check if it's already reported
2. Open a new issue with the "bug" label
3. Provide:
   - Clear description of the problem
   - Steps to reproduce
   - Expected vs actual behavior
   - Environment details (OS, Java version)
   - Logs or error messages
   - Screenshots if applicable

## Development Tips

### Debugging

Enable debug logging in `src/main/resources/logback.xml`:
```xml
<logger name="com.broadcastsender" level="DEBUG" />
```

### Running with Debugger

In IntelliJ IDEA:
1. Open `MainApp.java`
2. Right-click and select "Debug 'MainApp.main()'"

In VS Code:
1. Add a launch configuration
2. Set main class to `com.broadcastsender.MainApp`

### Common Issues

**JavaFX not found:**
- Ensure JavaFX is properly configured in build.gradle
- Check JAVA_HOME points to correct JDK

**Tests failing:**
- Clean and rebuild: `./gradlew clean build`
- Check for file permission issues
- Verify temp directory is writable

## Code of Conduct

### Our Pledge

We are committed to providing a welcoming and inclusive environment for all contributors.

### Expected Behavior

- Be respectful and constructive
- Accept feedback gracefully
- Focus on what's best for the project
- Show empathy towards others

### Unacceptable Behavior

- Harassment or discriminatory language
- Personal attacks or trolling
- Publishing private information
- Any conduct that could be considered inappropriate in a professional setting

## Questions?

If you have questions about contributing:

1. Check existing issues and discussions
2. Open a new issue with the "question" label
3. Contact the maintainers

## License

By contributing, you agree that your contributions will be licensed under the MIT License.

## Thank You!

Your contributions help make BroadcastSender FX better for everyone. We appreciate your time and effort!
