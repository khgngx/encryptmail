@echo off
echo === FIXING SECUREMAIL USER PASSWORD ===
echo.
echo This will reset the password for user 'securemail' to 'secret'
echo.
pause

echo Resetting password for securemail user...
"C:\Program Files\PostgreSQL\17\bin\psql.exe" -U postgres -h localhost -c "ALTER USER securemail WITH PASSWORD 'secret';"

if %ERRORLEVEL% EQU 0 (
    echo ✓ Password reset successfully!
    echo.
    echo Testing connection...
    "C:\Program Files\PostgreSQL\17\bin\psql.exe" -U securemail -h localhost -d securemail -c "SELECT COUNT(*) FROM accounts;"
    
    if %ERRORLEVEL% EQU 0 (
        echo ✓ Connection test successful!
        echo.
        echo Database is ready for the application!
    ) else (
        echo ✗ Connection test failed
    )
) else (
    echo ✗ Failed to reset password
)

echo.
pause
