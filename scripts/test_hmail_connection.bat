@echo off
echo ========================================
echo TESTING HMAIL CONNECTION
echo ========================================
echo.
echo Testing connection to hMailServer at 172.16.0.163
echo Domain: gmail.com
echo Accounts: khang@gmail.com, dat123@gmail.com
echo Password: 123456
echo.

echo Testing SMTP port 25...
telnet 172.16.0.163 25

echo.
echo Testing IMAP port 143...
telnet 172.16.0.163 143

echo.
echo If connections succeed, hMailServer is ready!
echo Now you can run the Java app and register accounts.
pause
