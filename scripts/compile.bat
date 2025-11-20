@echo off
set SCRIPT_DIR=%~dp0
cd /d "%SCRIPT_DIR%.."
echo === Compiling Secure Mail Client ===

REM Copy resources
xcopy /E /I /Y "src\main\resources" "target\classes" >nul 2>&1

REM Compile in correct order
echo Compiling core models...
javac -cp "lib\*" -d target\classes src\main\java\core\model\*.java

echo Compiling core interfaces...
javac -cp "lib\*;target\classes" -d target\classes src\main\java\core\service\*.java src\main\java\core\repository\*.java

echo Compiling config...
javac -cp "lib\*;target\classes" -d target\classes src\main\java\config\*.java

echo Compiling crypto utilities...
javac -cp "lib\*;target\classes" -d target\classes src\main\java\crypto\*.java

echo Compiling core registry...
javac -cp "lib\*;target\classes" -d target\classes src\main\java\core\ServiceRegistry.java

echo Compiling infrastructure...
javac -cp "lib\*;target\classes" -d target\classes src\main\java\infra\db\*.java
javac -cp "lib\*;target\classes" -d target\classes src\main\java\infra\auth\*.java
javac -cp "lib\*;target\classes" -d target\classes src\main\java\infra\crypto\*.java
javac -cp "lib\*;target\classes" -d target\classes src\main\java\infra\mail\*.java
javac -cp "lib\*;target\classes" -d target\classes src\main\java\infra\service\*.java

echo Compiling mail components...
javac -cp "lib\*;target\classes" -d target\classes src\main\java\mail\*.java

echo Compiling utilities...
javac -cp "lib\*;target\classes" -d target\classes src\main\java\util\*.java

echo Compiling UI theme...
javac -cp "lib\*;target\classes" -d target\classes src\main\java\ui\theme\*.java

echo Compiling UI components...
javac -cp "lib\*;target\classes" -d target\classes src\main\java\ui\modern\components\*.java

echo Compiling UI modern...
javac -cp "lib\*;target\classes" -d target\classes src\main\java\ui\modern\*.java

echo Compiling CLI...
javac -cp "lib\*;target\classes" -d target\classes src\main\java\ui\cli\*.java

echo Compiling legacy UI...
javac -cp "lib\*;target\classes" -d target\classes src\main\java\ui\*.java

echo Compiling main application...
javac -cp "lib\*;target\classes" -d target\classes src\main\java\app\*.java

echo === Compilation Complete ===
