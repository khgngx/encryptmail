# 🔗 P2P MAIL SETUP GUIDE

## Gửi Mail Trực Tiếp Đến IP Bạn Bè

### 📋 YÊU CẦU

1. **Cả hai máy phải có:**
   - Secure Mail Client đã cài đặt
   - Port 25 (SMTP) và 143/993 (IMAP) mở
   - Cùng mạng LAN hoặc có public IP

### 🛠️ THIẾT LẬP

#### **Máy A (Người gửi):**
```properties
# application.properties
app.mode=CLI_LOCAL
mail.smtp.host.local=192.168.1.50  # IP của máy B
mail.smtp.port.local=25
mail.imap.host.local=192.168.1.50
mail.imap.port.local=143
```

#### **Máy B (Người nhận):**
```properties
# application.properties  
app.mode=CLI_LOCAL
mail.smtp.host.local=localhost
mail.smtp.port.local=25
mail.imap.host.local=localhost
mail.imap.port.local=143
```

### 🚀 CHẠY ỨNG DỤNG

#### **Trên Máy B (Mail Server):**
```bash
# Khởi động mail server
java -cp target/classes:target/dependency/* mail.MockMailClient

# Hoặc chạy CLI mode
java -cp target/classes:target/dependency/* ui.cli.CliMain
```

#### **Trên Máy A (Client):**
```bash
# Chạy GUI mode
java -jar target/secure-mail-gui.jar

# Hoặc CLI mode
java -jar target/secure-mail-cli.jar
```

### 🔐 BẢO MẬT

App hỗ trợ 2 loại mã hóa:

1. **AES Encryption:** Mã hóa nội dung email
2. **RSA Digital Signature:** Xác thực người gửi

```java
// Trong ModernComposeWindow
encryptCheck.setSelected(true);  // Bật mã hóa
signCheck.setSelected(true);     // Bật chữ ký số
```
