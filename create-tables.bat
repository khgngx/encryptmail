@echo off
echo === CREATING DATABASE TABLES ===
echo.
echo This will create all tables in the securemail database.
echo.
pause

echo Creating tables from schema.sql...
"C:\Program Files\PostgreSQL\17\bin\psql.exe" -U securemail -h localhost -d securemail -f "src\main\resources\db\schema.sql"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✓ Tables created successfully!
    echo.
    echo Testing connection...
    "C:\Program Files\PostgreSQL\17\bin\psql.exe" -U securemail -h localhost -d securemail -c "SELECT COUNT(*) FROM accounts;"
    
    if %ERRORLEVEL% EQU 0 (
        echo ✓ Database is ready for use!
    )
) else (
    echo ✗ Failed to create tables
)

echo.
pause
