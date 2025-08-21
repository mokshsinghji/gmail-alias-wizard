# Gmail Alias Wizard Makefile

.PHONY: help build test run setup clean native-build dist

# Default target
help:
	@echo "Gmail Alias Wizard Build Commands"
	@echo "================================="
	@echo ""
	@echo "Development:"
	@echo "  make build       - Build JAR file"
	@echo "  make test        - Run tests"
	@echo "  make run         - Run server in development mode"
	@echo "  make setup       - Run setup to create application.yaml"
	@echo ""
	@echo "Native Build:"
	@echo "  make native-build - Build native executable (requires GraalVM)"
	@echo "  make dist        - Create distribution package"
	@echo ""
	@echo "Utility:"
	@echo "  make clean       - Clean build artifacts"
	@echo ""

# Build the JAR file
build:
	./gradlew :server:build -x test

# Run tests
test:
	./gradlew :server:test

# Run the server
run: build
	java -jar server/build/libs/server-all.jar

# Run setup
setup: build
	java -jar server/build/libs/server-all.jar --setup

# Clean build artifacts
clean:
	./gradlew clean
	rm -rf dist/
	rm -f application.yaml

# Build native executable
native-build:
	@if ! command -v native-image >/dev/null 2>&1; then \
		echo "Error: GraalVM native-image not found!"; \
		echo "Please install GraalVM and native-image component"; \
		exit 1; \
	fi
	./gradlew :server:nativeCompile

# Create distribution
dist: 
	@if [ ! -f "server/build/native/nativeCompile/gmail-alias-wizard" ]; then \
		echo "Native executable not found. Run 'make native-build' first."; \
		exit 1; \
	fi
	./create-distribution.sh

# Quick native build and dist
all-native: native-build dist
	@echo "Native distribution created successfully!"