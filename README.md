# 🔐 Secure Mail Client  
*Hybrid Encrypted & Signed Email Client – GUI & CLI*

Secure Mail Client là ứng dụng email bảo mật hỗ trợ **mã hóa end-to-end**, **chữ ký số**, và **multi-mode (GUI/CLI)**.  
Project được phát triển phục vụ các môn học: *Lập trình mạng*, *Lập trình hệ thống*, *Linux & PM nguồn mở*.

---

## 📑 Mục lục
- [✨ Tính năng chính](#-tính-năng-chính)
- [🏗️ Kiến trúc hệ thống](#️-kiến-trúc-hệ-thống)
- [🚀 Quick Start](#-quick-start)
- [📋 Chế độ chạy](#-chế-độ-chạy)
- [⚙️ Cấu hình hệ thống](#️-cấu-hình-hệ-thống)
- [🔧 Build & Run](#-build--run)
- [🖥️ Sử dụng GUI](#️-sử-dụng-gui)
- [💻 Sử dụng CLI (Linux/Ubuntu)](#-sử-dụng-cli-linuxubuntu)
- [📂 Cấu trúc source](#-cấu-trúc-source)
- [🗺️ Roadmap](#️-roadmap)
- [👨‍💻 Development Mode](#-development-mode)

---

## ✨ Tính năng chính
- **End-to-End Encryption**: RSA + AES (Hybrid Encryption)  
- **Digital Signature**: RSA Signing & Verification  
- **Dual Interface**: GUI (Swing) và CLI (Linux Terminal)  
- **PostgreSQL Storage**: Accounts, emails, keys  
- **Multi-mode**: Demo, GUI Remote, CLI Local  
- **Multi-user** trên cùng hệ thống Linux  

---

## 🏗️ Kiến trúc hệ thống
```
┌───────────────────┬────────────────────┐
│     GUI (Swing)   │     CLI (Ubuntu)   │
├───────────────────┴────────────────────┤
│             Core Services              │
│   Auth │ Mail │ Crypto │ Key │ History │
├─────────────────────────────────────────┤
│         Infrastructure Layer            │
│   PostgreSQL │ SMTP/IMAP │ FileStore   │
└─────────────────────────────────────────┘
```

---

## 🚀 Quick Start
### 1. Tự động setup
```bash
chmod +x setup.sh
./setup.sh
```

### 2. Chạy bản demo
```bash
mvn clean package
java -jar target/secure-mail-gui.jar
```

---

## 📋 Chế độ chạy

### **1. DEMO Mode (Mặc định)**
- Không cần cấu hình  
- Có sẵn mock mail server & test data  

### **2. GUI_REMOTE Mode**
- Dùng Swing UI  
- Kết nối mail server qua mạng  
- Phù hợp demo client–server thực tế  

### **3. CLI_LOCAL Mode**
- Chạy local giữa các user Ubuntu  
- Tương thích Postfix + Dovecot  

---

## ⚙️ Cấu hình hệ thống

### **1. Mail Server**

#### GUI_REMOTE
```properties
mail.smtp.host.remote=your-server-ip
mail.smtp.port.remote=587
mail.imap.host.remote=your-server-ip
mail.imap.port.remote=993
mail.domain.remote=yourdomain.com
```

#### CLI_LOCAL
```properties
mail.smtp.host.local=localhost
mail.smtp.port.local=25
mail.imap.host.local=localhost
mail.imap.port.local=143
```

---

### **2. Cấu hình PostgreSQL**
```properties
db.url=jdbc:postgresql://localhost:5432/securemail
db.user=securemail
db.password=secret
```

---

## 🔧 Build & Run

### Build project
```bash
mvn clean package
```

Sinh ra:
- `secure-mail-gui.jar`  
- `secure-mail-cli.jar`

---

### Chạy GUI
```bash
# Run trực tiếp
mvn exec:java -Dexec.mainClass="app.MainApp"

# Run jar
java -jar target/secure-mail-gui.jar

# GUI Remote mode
java -Dapp.mode=GUI_REMOTE -jar target/secure-mail-gui.jar
```

---

### Chạy CLI
```bash
mvn exec:java -Dexec.mainClass="ui.cli.CliMain"

# Hoặc jar (auto CLI_LOCAL)
java -jar target/secure-mail-cli.jar
```

---

## 🖥️ Sử dụng GUI

1. Mở ứng dụng  
2. Đăng nhập email/password  
3. Chức năng hỗ trợ:  
   - Inbox / Sent / Drafts  
   - Compose mail (Encrypt / Sign optional)  
   - Key Manager  

---

## 💻 Sử dụng CLI (Linux/Ubuntu)

### 1. Tạo user
```bash
sudo adduser user1
sudo adduser user2
```

### 2. Cài đặt mail server
```bash
sudo apt install postfix dovecot-imapd
```

### 3. Chạy ứng dụng
```bash
su - user1
java -jar secure-mail-cli.jar
```

Gửi mail đến user2 → chuyển sang user2 và xem Inbox.

---

## 📂 Cấu trúc source
```
src/main/java/
├── app/                # GUI entry point
├── ui/
│   ├── swing/          # GUI components
│   └── cli/            # Console interface
├── core/
│   ├── service/        # Business logic
│   └── ServiceRegistry # DI container
├── infra/
│   ├── mail/           # SMTP/IMAP implementations
│   ├── db/             # PostgreSQL repos
│   └── crypto/         # Crypto services
├── config/             # Configuration
├── crypto/             # Shared crypto utilities
├── mail/               # Mail helpers
└── util/               # Utility functions
```

---

## 🗺️ Roadmap
- ✔️ Phase 1: Multi-mode + CLI base  
- ⏳ Phase 2: PostgreSQL integration  
- ⏳ Phase 3: Encryption & Signing pipeline  
- ⏳ Phase 4: Service refactoring  
- ⏳ Phase 5: Production deployment  

---

## 👨‍💻 Development Mode

### GUI Dev
```bash
mvn exec:java -Dexec.mainClass="app.MainApp" -Dapp.mode=DEMO
```

### CLI Dev
```bash
mvn exec:java -Dexec.mainClass="ui.cli.CliMain"
```

### Test modes
```bash
# GUI Remote
java -Dapp.mode=GUI_REMOTE \
     -Dmail.smtp.host.remote=192.168.1.100 \
     -jar target/secure-mail-gui.jar

# CLI Local
java -jar target/secure-mail-cli.jar
```

---
