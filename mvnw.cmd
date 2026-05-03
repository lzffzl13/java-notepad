@REM Maven Wrapper script for Windows
@REM Downloads Maven if not present and runs it

@echo off
setlocal

set "MAVEN_PROJECTBASEDIR=%~dp0"
set "MAVEN_WRAPPER_PROPERTIES=%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.properties"

for /f "usebackq tokens=1,* delims==" %%a in ("%MAVEN_WRAPPER_PROPERTIES%") do (
    if "%%a"=="distributionUrl" set "distributionUrl=%%b"
)

for %%i in ("%distributionUrl%") do set "distName=%%~ni"
set "MAVEN_HOME=%USERPROFILE%\.m2\wrapper\dists\%distName%"

if not exist "%MAVEN_HOME%" (
    echo Downloading Maven...
    mkdir "%MAVEN_HOME%"
    powershell -Command "Invoke-WebRequest -Uri '%distributionUrl%' -OutFile '%MAVEN_HOME%\maven.zip'"
    powershell -Command "Expand-Archive -Path '%MAVEN_HOME%\maven.zip' -DestinationPath '%MAVEN_HOME%'"
    del "%MAVEN_HOME%\maven.zip"
    for /d %%d in ("%MAVEN_HOME%\apache-maven-*") do (
        xcopy "%%d\*" "%MAVEN_HOME%\" /E /Y /Q >nul
        rmdir /S /Q "%%d"
    )
)

"%MAVEN_HOME%\bin\mvn.cmd" %*
