@echo off
setlocal
cd /d "%~dp0"
call mvn clean javadoc:javadoc
if errorlevel 1 (
    echo No fue posible generar la documentacion. Revise Maven y el JDK 17.
    pause
    exit /b 1
)
start "" "target\site\apidocs\index.html"
endlocal
