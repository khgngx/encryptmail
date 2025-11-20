# FIX TOÀN BỘ VẤN ĐỀ LOGIN

## Vấn đề hiện tại
1. ❌ "Email already exists" khi đăng ký
2. ❌ "Failed to find account" khi đăng nhập
3. ❌ Database chưa có column `plain_password`

## Giải pháp hoàn chỉnh

### BƯỚC 1: Fix Database Schema
```sql
-- Kết nối PostgreSQL và chạy từng lệnh:

-- 1. Thêm column plain_password
ALTER TABLE accounts ADD COLUMN IF NOT EXISTS plain_password VARCHAR(255);

-- 2. Xóa tất cả accounts cũ (để tránh conflict)
DELETE FROM accounts WHERE email LIKE '%@gmail.com';

-- 3. Reset sequence
ALTER SEQUENCE accounts_id_seq RESTART WITH 1;

-- 4. Kiểm tra kết quả
SELECT COUNT(*) as total_accounts FROM accounts;
SELECT column_name FROM information_schema.columns WHERE table_name = 'accounts';
```

### BƯỚC 2: Logic Login đã được sửa
✅ **Đã sửa `DefaultAuthService.login()`**:
- Thử `plainPassword` trước (trong GUI_REMOTE mode)
- Nếu không match, thử `hashedPassword`
- Có logging để debug

### BƯỚC 3: Test Registration và Login

#### 3.1. Đăng ký account mới
1. Chạy app Java
2. Chọn "Sign up"
3. Đăng ký:
   - Email: `khang@gmail.com`
   - Password: `123456`
   - Confirm: `123456`
4. **Kết quả mong đợi**: "Account created successfully!"

#### 3.2. Đăng nhập
1. Chuyển về "Sign in"
2. Nhập:
   - Email: `khang@gmail.com`
   - Password: `123456`
3. **Kết quả mong đợi**: Vào được main app

### BƯỚC 4: Kiểm tra Database
```sql
-- Xem account vừa tạo
SELECT id, email, password_hash, plain_password, active 
FROM accounts 
WHERE email = 'khang@gmail.com';
```

**Kết quả mong đợi**:
- `password_hash`: có giá trị (Base64 encoded)
- `plain_password`: `123456`
- `active`: `true`

### BƯỚC 5: Test trên máy thứ 2
1. Copy toàn bộ folder `encryptmail` sang máy bạn của bạn
2. Đảm bảo `application.properties` giống nhau
3. Đăng ký account thứ 2:
   - Email: `dat123@gmail.com`
   - Password: `123456`

## Troubleshooting

### Nếu vẫn lỗi "Email already exists"
```sql
-- Xem accounts hiện có
SELECT email FROM accounts;

-- Xóa account cụ thể
DELETE FROM accounts WHERE email = 'khang@gmail.com';
```

### Nếu vẫn lỗi "Failed to find account"
1. Kiểm tra console log để xem:
   - "Plain password check: true/false"
   - "Hashed password check: true/false"
2. Nếu cả 2 đều false → password không đúng
3. Nếu không thấy log → account không tồn tại

### Nếu database connection error
1. Kiểm tra PostgreSQL có chạy không
2. Kiểm tra `application.properties`:
   ```properties
   db.url=jdbc:postgresql://172.16.0.163:5432/securemail
   db.user=securemail
   db.password=secret
   ```

## Files đã thay đổi
- ✅ `DefaultAuthService.java`: Logic login mới
- ✅ `Account.java`: Thêm plainPassword field
- ✅ `PgAccountRepository.java`: Handle plain_password column
- ✅ `schema.sql`: Thêm plain_password column

## Kết quả cuối cùng
Sau khi làm theo các bước trên:
1. ✅ Đăng ký account thành công
2. ✅ Đăng nhập thành công  
3. ✅ Gửi mail thành công (không còn lỗi 535)
4. ✅ Nhận mail thành công

**Hãy làm theo từng bước và báo cho tôi kết quả!** 🚀
