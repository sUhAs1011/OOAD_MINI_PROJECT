@echo off
echo Starting E-Voting Application...
echo Setting up classpath...

if not exist "bin" mkdir bin
if not exist "bin\styles" mkdir "bin\styles"
if not exist "bin\resources" mkdir "bin\resources"

REM Check if PATH_TO_FX is set
if "%PATH_TO_FX%"=="" (
    echo ERROR: PATH_TO_FX environment variable is not set.
    echo Please set PATH_TO_FX to point to your JavaFX SDK lib directory.
    echo Example: set PATH_TO_FX=C:\path\to\javafx-sdk\lib
    pause
    exit /b 1
)

echo Compiling application...
javac --module-path "%PATH_TO_FX%" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base -cp "./mysql-connector-j-9.2.0/mysql-connector-j-9.2.0.jar;./jfreechart-1.0.19/jfreechart-1.0.19.jar" -d "./bin" ./src/com/example/voting/*.java

echo Copying resources...
copy ".\src\styles\main.css" ".\bin\styles\" >nul 2>&1
if errorlevel 1 (
    echo Warning: Could not copy CSS file. UI styling may be affected.
) else (
    echo CSS resources copied successfully.
)

copy ".\src\resources\messages_*.properties" ".\bin\resources\" >nul 2>&1
if errorlevel 1 (
    echo Warning: Could not copy language files. Multilanguage support may be affected.
) else (
    echo Language resources copied successfully.
)

echo Running application...
java --module-path "%PATH_TO_FX%" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base -cp "./bin;./mysql-connector-j-9.2.0/mysql-connector-j-9.2.0.jar;./jfreechart-1.0.19/jfreechart-1.0.19.jar" com.example.voting.Main

echo Application closed.
pause 