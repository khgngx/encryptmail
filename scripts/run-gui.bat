@echo off
set SCRIPT_DIR=%~dp0
cd /d "%SCRIPT_DIR%.."
echo === Starting Secure Mail Client (GUI Mode) ===
echo.
echo Mode: DEMO (for testing)
echo Features: Modern UI, Dark/Light theme, Encryption support
echo.
echo Starting application...
java -cp "lib\*;target\classes" app.MainApp
pause
