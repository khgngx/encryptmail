-- Force clean database completely
TRUNCATE TABLE accounts RESTART IDENTITY CASCADE;
TRUNCATE TABLE login_history RESTART IDENTITY CASCADE;
TRUNCATE TABLE emails RESTART IDENTITY CASCADE;

-- Verify tables are empty
SELECT 'accounts' as table_name, COUNT(*) as count FROM accounts
UNION ALL
SELECT 'login_history', COUNT(*) FROM login_history
UNION ALL  
SELECT 'emails', COUNT(*) FROM emails;
