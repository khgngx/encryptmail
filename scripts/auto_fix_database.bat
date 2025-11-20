@echo off
echo ========================================
echo AUTO FIX DATABASE - SECURE MAIL CLIENT
echo ========================================
echo.

echo Step 1: Adding plain_password column to database...
psql -h localhost -p 5432 -U securemail -d securemail -c "ALTER TABLE accounts ADD COLUMN IF NOT EXISTS plain_password VARCHAR(255);"

if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Failed to connect to PostgreSQL
    echo Please make sure PostgreSQL is running and credentials are correct
    pause
    exit /b 1
)

echo Step 2: Cleaning old accounts to avoid conflicts...
psql -h localhost -p 5432 -U securemail -d securemail -c "DELETE FROM accounts WHERE email LIKE '%%@gmail.com';"

echo Step 3: Resetting ID sequence...
psql -h localhost -p 5432 -U securemail -d securemail -c "ALTER SEQUENCE accounts_id_seq RESTART WITH 1;"

echo Step 4: Verifying database schema...
psql -h localhost -p 5432 -U securemail -d securemail -c "SELECT column_name FROM information_schema.columns WHERE table_name = 'accounts' ORDER BY ordinal_position;"

echo.
echo ========================================
echo DATABASE FIX COMPLETED!
echo ========================================
echo Now you can run the Java application:
echo java -cp "target\classes;lib\*" app.MainApp
echo.
pause
