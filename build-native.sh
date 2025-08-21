#!/bin/bash

# Gmail Alias Wizard Native Build Script
# This script builds the native executable using GraalVM

set -e

echo "Gmail Alias Wizard Native Build"
echo "================================"

# Check if GraalVM is installed
if ! command -v native-image &> /dev/null; then
    echo "Error: GraalVM native-image is not found!"
    echo ""
    echo "Please install GraalVM and ensure native-image is available:"
    echo "1. Download GraalVM from https://github.com/graalvm/graalvm-releases/releases"
    echo "2. Set JAVA_HOME to point to GraalVM"
    echo "3. Install native-image: \$JAVA_HOME/bin/gu install native-image"
    echo ""
    echo "Alternatively, use SDKMAN!:"
    echo "  sdk install java 22.0.2-graal"
    echo "  sdk use java 22.0.2-graal"
    echo "  gu install native-image"
    exit 1
fi

echo "GraalVM version: $(java -version)"
echo "Native image: $(which native-image)"
echo ""

# Build the native executable
echo "Building native executable..."
./gradlew :server:nativeCompile --no-daemon

if [ $? -eq 0 ]; then
    echo ""
    echo "Build successful!"
    echo "Native executable: server/build/native/nativeCompile/gmail-alias-wizard"
    echo ""
    echo "To create a distribution package:"
    echo "./create-distribution.sh"
else
    echo "Build failed!"
    exit 1
fi