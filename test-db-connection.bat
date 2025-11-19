@echo off
echo === TESTING DATABASE CONNECTION ===
echo.

echo Testing if database 'securemail' exists...
"C:\Program Files\PostgreSQL\17\bin\psql.exe" -U postgres -h localhost -l | findstr securemail
if %ERRORLEVEL% EQU 0 (
    echo ✓ Database 'securemail' found
) else (
    echo ✗ Database 'securemail' not found
)
echo.

echo Testing connection to securemail database...
"C:\Program Files\PostgreSQL\17\bin\psql.exe" -U securemail -h localhost -d securemail -c "\dt"
if %ERRORLEVEL% EQU 0 (
    echo ✓ Successfully connected to database as user 'securemail'
) else (
    echo ✗ Failed to connect to database as user 'securemail'
)
echo.

pause
