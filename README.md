# gmail-alias-wizard

A native executable application for managing Gmail aliases without requiring a JVM runtime.

## Quick Start

### Option 1: Using Pre-built Native Executable (Recommended)

1. Download the native distribution for your platform
2. Run setup to create configuration:
   ```bash
   ./gmail-alias-wizard.sh setup
   ```
   
3. Edit the generated `application.yaml` file:
   - Add your Google OAuth credentials
   - Generate encryption keys: `openssl rand -hex 16`
   - Configure your Gmail settings

4. Start the server:
   ```bash
   ./gmail-alias-wizard.sh
   ```

### Option 2: Building from Source

#### Regular JAR Build

First make the application.yaml file in the `server/src/main/resources` directory.

This requires the following:
```yaml
ktor:
  application:
    modules:
      - com.moksh.ApplicationKt.module
    dependencies:
      - com.moksh.config.DatabaseConnectionKt.provideDatabaseConnection
      - com.moksh.services.AuthServiceImpl
      - com.moksh.services.GoogleServiceImpl
  deployment:
    port: 8080

database:
  url: jdbc:sqlite:./data.db # can be changed to any other database

auth:
  sessions:
    encryptKey: <CREATE_WITH_OPENSSL>
    signKey: <CREATE_WITH_OPENSSL>
  google:
    clientId: <YOUR_GOOGLE_CLIENT_ID>
    clientSecret: <YOUR_GOOGLE_CLIENT_SECRET>
    appPassword: <YOUR_GMAIL_APP_PASSWORD>
    smtpUsername: <YOUR_GMAIL_ADDRESS>
```

Build and run:
```bash
./gradlew :server:build -x test
java -jar server/build/libs/server-all.jar --setup  # Create config
java -jar server/build/libs/server-all.jar          # Start server
```

#### Native Executable Build

To build a native executable that doesn't require JVM:

1. Install GraalVM:
   ```bash
   # Using SDKMAN (recommended)
   sdk install java 22.0.2-graal
   sdk use java 22.0.2-graal
   gu install native-image
   ```

2. Build native executable:
   ```bash
   ./build-native.sh
   ```

3. Create distribution package:
   ```bash
   ./create-distribution.sh
   ```

## Features

- **No JVM Required**: Native executable runs without Java installation
- **External Configuration**: Uses external `application.yaml` file for easy configuration
- **Setup Script**: Built-in setup command creates configuration template
- **Cross-platform**: Build native executables for Linux, macOS, and Windows
- **Gmail Integration**: OAuth2 authentication with Gmail API
- **Alias Management**: Create and manage email aliases

## Configuration

The application uses an external `application.yaml` file for configuration. Run the setup command to create a template:

```bash
./gmail-alias-wizard.sh setup
```

This creates `application.yaml` with the structure shown above. Edit this file with your actual credentials and settings.

## Development

- **Server**: Kotlin with Ktor framework
- **Frontend**: React (in `react-frontend/` directory)
- **Database**: SQLite (configurable)
- **Build**: Gradle with GraalVM Native Image support

## Commands

```bash
# Setup (creates application.yaml)
./gmail-alias-wizard.sh setup

# Start server  
./gmail-alias-wizard.sh

# Help
./gmail-alias-wizard.sh help
```
