# Deployment Guide

## Overview

Secure Mail Client có thể được triển khai theo 3 mode:

1. **DEMO**: Chạy với mock server, không cần cấu hình
2. **GUI_REMOTE**: GUI kết nối tới mail server trên máy khác
3. **CLI_LOCAL**: CLI gửi mail giữa các user trên cùng máy Ubuntu

## Prerequisites

### Chung
- Java 17 hoặc cao hơn
- Maven 3.6+

### Cho GUI_REMOTE mode
- Mail server (Gmail hoặc tự dựng) trên máy khác
- PostgreSQL (optional, để lưu lịch sử)

### Cho CLI_LOCAL mode
- Ubuntu/Linux
- PostgreSQL
- Postfix (SMTP server)
- Dovecot (IMAP server)

## Quick Start

### 1. Chạy setup script

```bash
chmod +x setup.sh
./setup.sh
```

### 2. Hoặc setup thủ công

#### Build application
```bash
mvn clean package
```

#### Setup PostgreSQL (nếu cần)
```bash
# Install PostgreSQL
sudo apt install postgresql postgresql-contrib

# Create database
sudo -u postgres createdb securemail
sudo -u postgres createuser securemail -P  # Enter password: secret

# Grant permissions
sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE securemail TO securemail;"

# Create schema
PGPASSWORD=secret psql -h localhost -U securemail -d securemail -f src/main/resources/db/schema.sql
```

#### Setup mail server cho CLI mode
```bash
# Install mail server components
sudo apt install postfix dovecot-imapd

# Configure Postfix for local delivery
sudo dpkg-reconfigure postfix
# Choose: Local only
# System mail name: your-hostname

# Configure Dovecot
sudo systemctl enable dovecot
sudo systemctl start dovecot
```

## Configuration

### Database Configuration

Edit `src/main/resources/application.properties`:

```properties
# Database
db.url=jdbc:postgresql://localhost:5432/securemail
db.user=securemail
db.password=secret
```

### Mail Server Configuration

#### GUI_REMOTE mode
```properties
app.mode=GUI_REMOTE

# Remote mail server
mail.smtp.host.remote=your-server-ip
mail.smtp.port.remote=587
mail.imap.host.remote=your-server-ip
mail.imap.port.remote=993
mail.domain.remote=yourdomain.com
```

#### CLI_LOCAL mode
```properties
app.mode=CLI_LOCAL

# Local mail server
mail.smtp.host.local=localhost
mail.smtp.port.local=25
mail.imap.host.local=localhost
mail.imap.port.local=143
```

## Running the Application

### Demo Mode
```bash
java -jar target/secure-mail-gui.jar
```

### GUI Mode
```bash
java -Dapp.mode=GUI_REMOTE -jar target/secure-mail-gui.jar
```

### CLI Mode
```bash
java -jar target/secure-mail-cli.jar
```

### With custom config
```bash
java -Dapp.mode=CLI_LOCAL -Ddb.url=jdbc:postgresql://remote-host:5432/securemail -jar target/secure-mail-cli.jar
```

## Testing

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn verify
```

### Manual Testing

#### Test GUI Mode
1. Start GUI app
2. Login với email/password
3. Compose mail với encryption/signing
4. Verify mail được gửi và nhận

#### Test CLI Mode
1. Tạo 2 user Ubuntu: `user1`, `user2`
2. Login với `user1`, chạy CLI
3. Generate key pair
4. Compose mail tới `user2@hostname`
5. Switch sang `user2`, chạy CLI
6. Check inbox, verify mail nhận được

## Troubleshooting

### Database Connection Issues
```bash
# Check PostgreSQL status
sudo systemctl status postgresql

# Check connection
PGPASSWORD=secret psql -h localhost -U securemail -d securemail -c "SELECT 1;"
```

### Mail Server Issues
```bash
# Check Postfix
sudo systemctl status postfix
sudo tail -f /var/log/mail.log

# Check Dovecot
sudo systemctl status dovecot
sudo tail -f /var/log/dovecot.log

# Test mail delivery
echo "Test message" | mail -s "Test" user2@$(hostname)
```

### Application Logs
```bash
# Enable debug logging
java -Djava.util.logging.config.file=logging.properties -jar target/secure-mail-cli.jar
```

### Common Issues

1. **"Database not available"**
   - Check PostgreSQL is running
   - Verify connection string in application.properties
   - Check user permissions

2. **"Mail server connection failed"**
   - Verify SMTP/IMAP host and ports
   - Check firewall settings
   - For Gmail: use App Password, not regular password

3. **"Key pair not found"**
   - Generate key pair first in Keys menu
   - Check `keys/` directory permissions

4. **"Permission denied" on Ubuntu**
   - Check user has permission to write to app directory
   - For mail delivery: check user exists in system

## Production Deployment

### Security Considerations
- Use strong database passwords
- Enable SSL/TLS for mail connections
- Secure key storage (consider HSM for production)
- Regular security updates

### Performance Tuning
- Adjust database connection pool size
- Configure mail server connection limits
- Monitor memory usage for large mailboxes

### Monitoring
- Database connection health
- Mail server connectivity
- Application logs
- Key pair integrity

### Backup Strategy
- Database backups (pg_dump)
- Key pair backups
- Configuration backups
- Mail data backups

## Docker Deployment (Optional)

```dockerfile
FROM openjdk:17-jdk-slim

COPY target/secure-mail-cli.jar /app/
COPY src/main/resources/application.properties /app/

WORKDIR /app
EXPOSE 8080

CMD ["java", "-jar", "secure-mail-cli.jar"]
```

```bash
docker build -t secure-mail .
docker run -d -p 8080:8080 --name secure-mail-app secure-mail
```
