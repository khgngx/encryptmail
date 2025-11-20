@echo off
echo ========================================
echo AUTO FIX AND RUN - SECURE MAIL CLIENT
echo ========================================
echo.

echo Step 1: Compiling DatabaseFixer...
cd /d "d:\code\encryptmail"
javac -cp "target\classes;lib\*" src\main\java\util\DatabaseFixer.java -d target\classes

if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Failed to compile DatabaseFixer
    pause
    exit /b 1
)

echo Step 2: Running database fix...
java -cp "target\classes;lib\*" util.DatabaseFixer

if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Database fix failed
    pause
    exit /b 1
)

echo.
echo Step 3: Starting main application...
echo ========================================
java -cp "target\classes;lib\*" app.MainApp

pause
