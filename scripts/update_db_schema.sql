-- Script để update database schema cho plain_password column
-- Chạy script này nếu bạn đã có data trong database

-- Thêm column plain_password vào bảng accounts
ALTER TABLE accounts ADD COLUMN IF NOT EXISTS plain_password VARCHAR(255);

-- Nếu bạn muốn set plain_password cho accounts đã tồn tại
-- (Chỉ dành cho test, trong production không nên làm vậy)
-- UPDATE accounts SET plain_password = '123456' WHERE email IN ('khang@gmail.com', 'dat123@gmail.com');

-- Kiểm tra schema đã update chưa
SELECT column_name, data_type, is_nullable 
FROM information_schema.columns 
WHERE table_name = 'accounts' 
ORDER BY ordinal_position;
