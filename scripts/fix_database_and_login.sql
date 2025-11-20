-- Script để fix toàn bộ database và login issues
-- Chạy script này trong PostgreSQL

-- 1. Thêm column plain_password nếu chưa có
ALTER TABLE accounts ADD COLUMN IF NOT EXISTS plain_password VARCHAR(255);

-- 2. Xóa tất cả accounts cũ để tránh conflict
DELETE FROM accounts WHERE email LIKE '%@gmail.com';

-- 3. Xóa luôn login_history và emails liên quan (nếu có)
DELETE FROM login_history WHERE account_id NOT IN (SELECT id FROM accounts);
DELETE FROM emails WHERE account_id NOT IN (SELECT id FROM accounts);

-- 4. Reset sequence để ID bắt đầu từ 1
ALTER SEQUENCE accounts_id_seq RESTART WITH 1;

-- 5. Kiểm tra bảng accounts đã sạch chưa
SELECT COUNT(*) as total_accounts FROM accounts;

-- 6. Kiểm tra schema đã đúng chưa
SELECT column_name, data_type, is_nullable 
FROM information_schema.columns 
WHERE table_name = 'accounts' 
AND column_name IN ('email', 'password_hash', 'plain_password')
ORDER BY ordinal_position;
