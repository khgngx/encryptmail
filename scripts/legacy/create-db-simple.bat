@echo off
echo === SIMPLE DATABASE CREATION ===
echo.
echo This will create the securemail database and user.
echo You will be prompted for the postgres password multiple times.
echo.
pause

echo Step 1: Creating database...
"C:\Program Files\PostgreSQL\17\bin\psql.exe" -U postgres -h localhost -c "CREATE DATABASE securemail;"

echo.
echo Step 2: Creating user...
"C:\Program Files\PostgreSQL\17\bin\psql.exe" -U postgres -h localhost -c "CREATE USER securemail WITH PASSWORD 'secret';"

echo.
echo Step 3: Granting privileges...
"C:\Program Files\PostgreSQL\17\bin\psql.exe" -U postgres -h localhost -c "GRANT ALL PRIVILEGES ON DATABASE securemail TO securemail;"

echo.
echo Step 4: Granting schema privileges...
"C:\Program Files\PostgreSQL\17\bin\psql.exe" -U postgres -h localhost -d securemail -c "GRANT ALL ON SCHEMA public TO securemail;"

echo.
echo === BASIC SETUP COMPLETE ===
echo Now run create-tables.bat to create the database schema
pause
