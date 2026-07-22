@echo off
setlocal EnableExtensions

set "SCRIPT_DIR=%~dp0"
for %%I in ("%SCRIPT_DIR%..") do set "MODULE_DIR=%%~fI"
for %%I in ("%MODULE_DIR%\..\..") do set "PROJECT_DIR=%%~fI"

if not defined JAVA_HOME (
    echo [ERROR] JAVA_HOME is not set.
    echo Install GraalVM JDK 21 and set JAVA_HOME to its installation directory.
    exit /b 1
)

set "NATIVE_IMAGE=%JAVA_HOME%\bin\native-image.cmd"
if not exist "%NATIVE_IMAGE%" set "NATIVE_IMAGE=%JAVA_HOME%\bin\native-image.exe"
if not exist "%NATIVE_IMAGE%" (
    echo [ERROR] native-image was not found under JAVA_HOME:
    echo         %JAVA_HOME%
    exit /b 1
)

if not exist "%PROJECT_DIR%\mvnw.cmd" (
    echo [ERROR] Maven Wrapper was not found: %PROJECT_DIR%\mvnw.cmd
    exit /b 1
)

echo Building scaffold-test-native Windows executable...
echo JAVA_HOME=%JAVA_HOME%

pushd "%PROJECT_DIR%" || exit /b 1
call mvnw.cmd -Pnative -pl scaffold-test/scaffold-test-native -am -Dmaven.test.skip=true clean package
set "BUILD_EXIT_CODE=%ERRORLEVEL%"
popd

if not "%BUILD_EXIT_CODE%"=="0" (
    echo [ERROR] Windows native build failed with exit code %BUILD_EXIT_CODE%.
    exit /b %BUILD_EXIT_CODE%
)

set "OUTPUT_FILE=%MODULE_DIR%\target\scaffold-test-native.exe"
if not exist "%OUTPUT_FILE%" (
    echo [ERROR] Build completed but the executable was not found:
    echo         %OUTPUT_FILE%
    exit /b 1
)

echo Windows native executable: %OUTPUT_FILE%
exit /b 0
