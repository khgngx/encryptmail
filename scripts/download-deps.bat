@echo off
set SCRIPT_DIR=%~dp0
cd /d "%SCRIPT_DIR%.."
echo === Downloading Dependencies ===

REM Create lib directory
if not exist "lib" mkdir lib

echo Downloading Jakarta Mail API...
curl -L -o "lib\jakarta.mail-api-2.1.3.jar" "https://repo1.maven.org/maven2/jakarta/mail/jakarta.mail-api/2.1.3/jakarta.mail-api-2.1.3.jar"

echo Downloading Jakarta Mail Implementation...
curl -L -o "lib\angus-mail-2.0.3.jar" "https://repo1.maven.org/maven2/org/eclipse/angus/angus-mail/2.0.3/angus-mail-2.0.3.jar"

echo Downloading PostgreSQL JDBC Driver...
curl -L -o "lib\postgresql-42.7.4.jar" "https://repo1.maven.org/maven2/org/postgresql/postgresql/42.7.4/postgresql-42.7.4.jar"

echo Downloading HikariCP...
curl -L -o "lib\HikariCP-5.1.0.jar" "https://repo1.maven.org/maven2/com/zaxxer/HikariCP/5.1.0/HikariCP-5.1.0.jar"

echo Downloading SLF4J API...
curl -L -o "lib\slf4j-api-2.0.16.jar" "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/2.0.16/slf4j-api-2.0.16.jar"

echo Downloading SLF4J Simple...
curl -L -o "lib\slf4j-simple-2.0.16.jar" "https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/2.0.16/slf4j-simple-2.0.16.jar"

echo === Dependencies Downloaded ===
echo You can now compile and run the application!
