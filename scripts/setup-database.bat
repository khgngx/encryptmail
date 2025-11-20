@echo off
set SCRIPT_DIR=%~dp0
cd /d "%SCRIPT_DIR%.."
echo === SECURE MAIL CLIENT - DATABASE SETUP ===
echo.
echo This script will help you set up the PostgreSQL database
echo for the Secure Mail Client application.
echo.
echo Requirements:
echo - PostgreSQL must be installed and running
echo - You need superuser access to create database and user
echo.
echo Database Configuration:
echo - Database Name: securemail
echo - Username: securemail
echo - Password: secret
echo - Host: localhost
echo - Port: 5432
echo.
echo Press any key to continue or Ctrl+C to cancel...
pause >nul
echo.

echo Step 1: Testing PostgreSQL connection...
where psql >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo PostgreSQL psql not found in PATH, trying common locations...
    set "PSQL_PATH="
    
    REM Try common PostgreSQL installation paths
    for %%P in (
        "C:\Program Files\PostgreSQL\17\bin\psql.exe"
        "C:\Program Files\PostgreSQL\16\bin\psql.exe"
        "C:\Program Files\PostgreSQL\15\bin\psql.exe"
        "C:\Program Files\PostgreSQL\14\bin\psql.exe"
        "C:\Program Files (x86)\PostgreSQL\17\bin\psql.exe"
        "C:\Program Files (x86)\PostgreSQL\16\bin\psql.exe"
    ) do (
        if exist %%P (
            set "PSQL_PATH=%%~P"
            goto :found_psql
        )
    )
    
    echo ERROR: PostgreSQL psql.exe not found
    echo Please install PostgreSQL or add it to your PATH
    echo Download from: https://www.postgresql.org/download/windows/
    pause
    exit /b 1
    
    :found_psql
    echo Found PostgreSQL at: %PSQL_PATH%
    set PSQL=%PSQL_PATH%
) else (
    set PSQL=psql
)

echo ✓ PostgreSQL found
echo.

echo Step 2: Creating database and user...
echo Please enter your PostgreSQL superuser (postgres) password when prompted
echo.

echo Creating database securemail...
"C:\Program Files\PostgreSQL\17\bin\psql.exe" -U postgres -h localhost -c "CREATE DATABASE securemail;"
if %ERRORLEVEL% NEQ 0 (
    echo WARNING: Database creation failed (may already exist)
)

echo Creating user securemail...
"C:\Program Files\PostgreSQL\17\bin\psql.exe" -U postgres -h localhost -c "CREATE USER securemail WITH PASSWORD 'secret';"
if %ERRORLEVEL% NEQ 0 (
    echo WARNING: User creation failed (may already exist)
)

echo Granting privileges...
"C:\Program Files\PostgreSQL\17\bin\psql.exe" -U postgres -h localhost -c "GRANT ALL PRIVILEGES ON DATABASE securemail TO securemail;"
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Failed to grant privileges
    pause
    exit /b 1
)

echo Granting schema privileges...
"C:\Program Files\PostgreSQL\17\bin\psql.exe" -U postgres -h localhost -d securemail -c "GRANT ALL ON SCHEMA public TO securemail;"
if %ERRORLEVEL% NEQ 0 (
    echo WARNING: Schema privileges may need manual setup
)

echo ✓ Database and user created successfully
echo.

echo Step 3: Creating tables and schema...
"C:\Program Files\PostgreSQL\17\bin\psql.exe" -U securemail -h localhost -d securemail -f "src\main\resources\db\schema.sql"
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Failed to create schema
    echo Trying with postgres user...
    "C:\Program Files\PostgreSQL\17\bin\psql.exe" -U postgres -h localhost -d securemail -f "src\main\resources\db\schema.sql"
    if %ERRORLEVEL% NEQ 0 (
        echo ERROR: Schema creation failed completely
        pause
        exit /b 1
    )
)
echo ✓ Database schema created successfully
echo.

echo Step 4: Testing connection...
"C:\Program Files\PostgreSQL\17\bin\psql.exe" -U securemail -h localhost -d securemail -c "SELECT COUNT(*) FROM accounts;"
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Connection test failed
    pause
    exit /b 1
)
echo ✓ Database connection test successful
echo.

echo === DATABASE SETUP COMPLETE ===
echo.
echo The database is now ready for use.
echo You can now run the application in GUI_REMOTE mode.
echo.
echo To switch to database mode:
echo 1. Edit src\main\resources\application.properties
echo 2. Change app.mode=DEMO to app.mode=GUI_REMOTE
echo 3. Recompile with .\compile.bat
echo 4. Run with .\run-gui.bat
echo.
pause
