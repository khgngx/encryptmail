@echo off
echo ========================================
echo SECURE MAIL CLIENT - HMAIL VERSION
echo ========================================
echo.

echo Checking Java version...
java -version
echo.

echo Current configuration:
echo - Mode: GUI_REMOTE
echo - hMailServer: 172.16.0.163:25 (SMTP), 172.16.0.163:143 (IMAP)
echo - Domain: gmail.com
echo - Available accounts: khang@gmail.com, dat123@gmail.com
echo.

set SCRIPT_DIR=%~dp0
cd /d "%SCRIPT_DIR%.."
echo Starting application...
java -cp "target\classes;lib\*" app.MainApp

echo.
echo Application closed.
pause
