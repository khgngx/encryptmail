# FIX LỖI 535 AUTHENTICATION FAILED

## Vấn đề
App báo lỗi **"535 Authentication failed"** khi gửi mail vì không có password để authenticate với hMailServer.

## Giải pháp đã thực hiện

### 1. Thêm field `plainPassword` vào Account model
- Lưu password gốc (không hash) để authenticate với hMailServer
- Chỉ áp dụng trong GUI_REMOTE mode

### 2. Cập nhật database schema
- Thêm column `plain_password` vào bảng `accounts`
- File: `update_db_schema.sql`

### 3. Sửa logic gửi mail
- `ModernComposeWindow` giờ lấy `plainPassword` từ database
- Truyền password thật cho `mailService.sendMail()`

## Cách khắc phục ngay

### Bước 1: Update database
```sql
-- Kết nối PostgreSQL và chạy:
ALTER TABLE accounts ADD COLUMN IF NOT EXISTS plain_password VARCHAR(255);

-- Set password cho accounts đã tồn tại (chỉ để test)
UPDATE accounts SET plain_password = '123456' 
WHERE email IN ('khang@gmail.com', 'dat123@gmail.com');
```

### Bước 2: Xóa accounts cũ và đăng ký lại
**Cách dễ nhất:**
1. Xóa accounts cũ trong database:
```sql
DELETE FROM accounts WHERE email IN ('khang@gmail.com', 'dat123@gmail.com');
```

2. Chạy lại app và đăng ký lại accounts
3. Lần này app sẽ lưu cả `password_hash` và `plain_password`

### Bước 3: Test gửi mail
- Gửi mail từ `khang@gmail.com` tới `dat123@gmail.com`
- Không còn lỗi 535 nữa

## Lưu ý bảo mật

⚠️ **Lưu plain password có rủi ro bảo mật**
- Chỉ dùng cho test/demo với hMailServer
- Trong production, nên dùng OAuth hoặc token-based auth

## Files đã thay đổi
- ✅ `Account.java`: Thêm `plainPassword` field
- ✅ `DefaultAuthService.java`: Lưu plainPassword khi đăng ký
- ✅ `PgAccountRepository.java`: Handle plain_password column
- ✅ `ModernComposeWindow.java`: Dùng plainPassword khi gửi mail
- ✅ `schema.sql`: Thêm plain_password column

## Kết quả
**Sau khi fix, app sẽ:**
1. ✅ Đăng ký account thành công (có kiểm tra hMailServer)
2. ✅ Gửi mail thành công (dùng plainPassword)
3. ✅ Nhận mail thành công (IMAP authentication OK)
