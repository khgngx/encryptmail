@echo off
echo === SECURE MAIL CLIENT DEBUG MODE ===
echo.
echo This will run the application with detailed console logging
echo to help identify where the code breaks or stops executing.
echo.
echo Watch for these key indicators in the output:
echo   ✓ = Success (green checkmarks)
echo   ✗ = Error (red X marks)  
echo   Step X: = Major execution phases
echo   EDT: = Event Dispatch Thread operations
echo   CONFIG: = Configuration loading
echo   REGISTRY: = Service initialization
echo.
echo Press any key to start debugging...
pause >nul
echo.
echo =================== DEBUG OUTPUT ===================
java -cp "lib\*;target\classes" app.MainApp
echo.
echo =================== DEBUG COMPLETE =================
echo.
echo If the application failed, look for:
echo 1. The LAST successful step that completed
echo 2. Any ✗ error messages with stack traces
echo 3. Missing ✓ confirmations for expected steps
echo 4. EDT errors (Event Dispatch Thread issues)
echo 5. CONFIG errors (Configuration loading problems)
echo.
pause
