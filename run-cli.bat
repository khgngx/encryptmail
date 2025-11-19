@echo off
echo === Starting Secure Mail Client (CLI Mode) ===
echo.
echo Mode: Command Line Interface
echo Features: Terminal-based mail client
echo.
echo Starting CLI application...
java -cp "lib\*;target\classes" ui.cli.CliMain
pause
