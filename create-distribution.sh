#!/bin/bash

# Gmail Alias Wizard Distribution Creator
# Creates a distribution package with the native executable

set -e

DIST_DIR="dist"
EXECUTABLE="server/build/native/nativeCompile/gmail-alias-wizard"

echo "Creating Gmail Alias Wizard Distribution"
echo "======================================="

# Check if native executable exists
if [ ! -f "$EXECUTABLE" ]; then
    echo "Error: Native executable not found at $EXECUTABLE"
    echo "Please run ./build-native.sh first"
    exit 1
fi

# Create distribution directory
rm -rf "$DIST_DIR"
mkdir -p "$DIST_DIR"

# Copy executable
echo "Copying native executable..."
cp "$EXECUTABLE" "$DIST_DIR/"

# Create wrapper script
echo "Creating wrapper scripts..."
cat > "$DIST_DIR/gmail-alias-wizard.sh" << 'EOF'
#!/bin/bash

# Gmail Alias Wizard Launcher

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
EXECUTABLE="$SCRIPT_DIR/gmail-alias-wizard"

if [ ! -f "$EXECUTABLE" ]; then
    echo "Error: gmail-alias-wizard executable not found!"
    exit 1
fi

# Check for setup command
if [ "$1" = "setup" ] || [ "$1" = "--setup" ]; then
    echo "Running Gmail Alias Wizard setup..."
    "$EXECUTABLE" --setup
elif [ "$1" = "help" ] || [ "$1" = "--help" ] || [ "$1" = "-h" ]; then
    echo "Gmail Alias Wizard - Email Alias Management"
    echo ""
    echo "Usage:"
    echo "  $0 setup    - Run initial configuration setup"
    echo "  $0          - Start the Gmail Alias Wizard server"
    echo "  $0 help     - Show this help message"
    echo ""
    echo "Configuration:"
    echo "  The setup command creates an application.yaml file with configuration templates."
    echo "  Edit this file with your actual Google OAuth credentials and other settings."
    echo ""
    echo "Server:"
    echo "  Once configured, the server will start on http://localhost:8080"
else
    echo "Starting Gmail Alias Wizard server..."
    "$EXECUTABLE" "$@"
fi
EOF

chmod +x "$DIST_DIR/gmail-alias-wizard.sh"

# Create Windows batch file
cat > "$DIST_DIR/gmail-alias-wizard.bat" << 'EOF'
@echo off

set SCRIPT_DIR=%~dp0
set EXECUTABLE=%SCRIPT_DIR%gmail-alias-wizard.exe

if not exist "%EXECUTABLE%" (
    echo Error: gmail-alias-wizard.exe not found!
    exit /b 1
)

if "%1"=="setup" goto setup
if "%1"=="--setup" goto setup
if "%1"=="help" goto help
if "%1"=="--help" goto help
if "%1"=="-h" goto help

echo Starting Gmail Alias Wizard server...
"%EXECUTABLE%" %*
goto end

:setup
echo Running Gmail Alias Wizard setup...
"%EXECUTABLE%" --setup
goto end

:help
echo Gmail Alias Wizard - Email Alias Management
echo.
echo Usage:
echo   %0 setup    - Run initial configuration setup
echo   %0          - Start the Gmail Alias Wizard server
echo   %0 help     - Show this help message
echo.
echo Configuration:
echo   The setup command creates an application.yaml file with configuration templates.
echo   Edit this file with your actual Google OAuth credentials and other settings.
echo.
echo Server:
echo   Once configured, the server will start on http://localhost:8080

:end
EOF

# Create README
cat > "$DIST_DIR/README.txt" << 'EOF'
Gmail Alias Wizard - Native Distribution
=========================================

This is a native executable distribution of Gmail Alias Wizard that doesn't require Java to be installed.

Quick Start:
-----------
1. Run setup to create configuration file:
   Linux/macOS: ./gmail-alias-wizard.sh setup
   Windows:     gmail-alias-wizard.bat setup

2. Edit the generated application.yaml file:
   - Add your Google OAuth client ID and secret
   - Generate encryption keys with OpenSSL:
     openssl rand -hex 16
   - Configure your Gmail credentials

3. Start the server:
   Linux/macOS: ./gmail-alias-wizard.sh
   Windows:     gmail-alias-wizard.bat

4. Open http://localhost:8080 in your browser

Files:
------
- gmail-alias-wizard         - Native executable (Linux/macOS)
- gmail-alias-wizard.exe     - Native executable (Windows)  
- gmail-alias-wizard.sh      - Shell script launcher (Linux/macOS)
- gmail-alias-wizard.bat     - Batch file launcher (Windows)
- application.yaml           - Configuration file (created by setup)
- README.txt                 - This file

Configuration:
-------------
The application.yaml file contains all configuration options including:
- Server port (default: 8080)
- Database connection (SQLite by default)
- Google OAuth credentials
- Session encryption keys

For more information, visit: https://github.com/mokshsinghji/gmail-alias-wizard
EOF

echo ""
echo "Distribution created in '$DIST_DIR/' directory"
echo ""
echo "Contents:"
ls -la "$DIST_DIR"
echo ""
echo "To create a zip archive:"
echo "  zip -r gmail-alias-wizard-native.zip $DIST_DIR/"