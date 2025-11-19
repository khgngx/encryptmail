@echo off
echo === CREATING DATABASE TABLES AS POSTGRES USER ===
echo.
echo This will create all tables using postgres superuser.
echo.
pause

echo Creating tables from schema.sql...
"C:\Program Files\PostgreSQL\17\bin\psql.exe" -U postgres -h localhost -d securemail -f "src\main\resources\db\schema.sql"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✓ Tables created successfully!
    echo.
    echo Testing connection...
    "C:\Program Files\PostgreSQL\17\bin\psql.exe" -U postgres -h localhost -d securemail -c "SELECT COUNT(*) FROM accounts;"
    
    if %ERRORLEVEL% EQU 0 (
        echo ✓ Database is ready for use!
        echo.
        echo Granting table permissions to securemail user...
        "C:\Program Files\PostgreSQL\17\bin\psql.exe" -U postgres -h localhost -d securemail -c "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO securemail;"
        "C:\Program Files\PostgreSQL\17\bin\psql.exe" -U postgres -h localhost -d securemail -c "GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO securemail;"
        echo ✓ Permissions granted!
    )
) else (
    echo ✗ Failed to create tables
)

echo.
pause
