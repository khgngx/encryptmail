#!/bin/bash

# Setup script for Secure Mail Client
# This script sets up the environment for both GUI and CLI modes

set -e

# Always run from project root (one level above scripts directory)
cd "$(dirname "$0")/.."

echo "=== Secure Mail Client Setup ==="

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check Java
if ! command_exists java; then
    echo "Error: Java is not installed. Please install Java 17 or higher."
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "Error: Java 17 or higher is required. Current version: $JAVA_VERSION"
    exit 1
fi

echo "✓ Java $JAVA_VERSION detected"

# Check Maven
if ! command_exists mvn; then
    echo "Error: Maven is not installed. Please install Maven."
    exit 1
fi

echo "✓ Maven detected"

# Setup PostgreSQL (optional)
setup_postgresql() {
    echo "Setting up PostgreSQL..."
    
    if ! command_exists psql; then
        echo "PostgreSQL not found. Installing..."
        
        if command_exists apt-get; then
            # Ubuntu/Debian
            sudo apt-get update
            sudo apt-get install -y postgresql postgresql-contrib
        elif command_exists yum; then
            # CentOS/RHEL
            sudo yum install -y postgresql postgresql-server
            sudo postgresql-setup initdb
        else
            echo "Please install PostgreSQL manually"
            return 1
        fi
    fi
    
    echo "✓ PostgreSQL detected"
    
    # Create database and user
    echo "Creating database and user..."
    sudo -u postgres psql << EOF
CREATE DATABASE securemail;
CREATE USER securemail WITH PASSWORD 'secret';
GRANT ALL PRIVILEGES ON DATABASE securemail TO securemail;
\q
EOF
    
    # Run schema
    echo "Creating database schema..."
    PGPASSWORD=secret psql -h localhost -U securemail -d securemail -f src/main/resources/db/schema.sql
    
    echo "✓ Database setup completed"
}

# Setup mail server for CLI mode (optional)
setup_mail_server() {
    echo "Setting up local mail server for CLI mode..."
    
    if ! command_exists postfix; then
        echo "Installing Postfix..."
        if command_exists apt-get; then
            sudo apt-get install -y postfix
        elif command_exists yum; then
            sudo yum install -y postfix
        fi
    fi
    
    if ! command_exists dovecot; then
        echo "Installing Dovecot..."
        if command_exists apt-get; then
            sudo apt-get install -y dovecot-imapd
        elif command_exists yum; then
            sudo yum install -y dovecot
        fi
    fi
    
    echo "✓ Mail server components installed"
    echo "Note: You may need to configure Postfix and Dovecot manually"
}

# Build the application
build_app() {
    echo "Building application..."
    mvn clean package -DskipTests
    echo "✓ Application built successfully"
}

# Main setup
echo "Choose setup mode:"
echo "1) Demo mode (no external dependencies)"
echo "2) GUI mode (requires remote mail server)"
echo "3) CLI mode (requires PostgreSQL + local mail server)"
echo "4) Full setup (PostgreSQL + mail server)"

read -p "Enter choice (1-4): " choice

case $choice in
    1)
        echo "Setting up demo mode..."
        build_app
        echo "✓ Demo mode setup completed"
        echo "Run: java -jar target/secure-mail-gui.jar"
        ;;
    2)
        echo "Setting up GUI mode..."
        build_app
        echo "✓ GUI mode setup completed"
        echo "Configure your remote mail server in application.properties"
        echo "Run: java -Dapp.mode=GUI_REMOTE -jar target/secure-mail-gui.jar"
        ;;
    3)
        echo "Setting up CLI mode..."
        setup_postgresql
        setup_mail_server
        build_app
        echo "✓ CLI mode setup completed"
        echo "Run: java -jar target/secure-mail-cli.jar"
        ;;
    4)
        echo "Setting up full environment..."
        setup_postgresql
        setup_mail_server
        build_app
        echo "✓ Full setup completed"
        echo "GUI: java -Dapp.mode=GUI_REMOTE -jar target/secure-mail-gui.jar"
        echo "CLI: java -jar target/secure-mail-cli.jar"
        ;;
    *)
        echo "Invalid choice"
        exit 1
        ;;
esac

echo ""
echo "=== Setup Complete ==="
echo "Check README.md for usage instructions"
