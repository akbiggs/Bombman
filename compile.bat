@ECHO OFF

set JDK_HOME25="C:\Program Files\Java\jdk1.7.0_25\bin"
set JDK_HOME40="C:\Program Files\Java\jdk1.7.0_40\bin"

set JDK_HOME25_="C:\Program Files (x86)\Java\jdk1.7.0_25\bin"
set JDK_HOME40_="C:\Program Files (x86)\Java\jdk1.7.0_40\bin"

SET JARPATH="%CD%\bombman.jar;%CD%\lib\*"
SET CLASS_PATH="%CD%\bombmanplayer"
SET BUILD_CLASS_PATH=%JARPATH%;%CLASS_PATH%
SET SRC_PATH="%CD%\bombmanplayer\PlayerAI.java"

echo [Checking PATH Variable]
javac.exe -version >nul 2>&1
if ERRORLEVEL 1 goto :DefaultHome
echo [Found `]
set JAVA=javac.exe
goto :JavaFound

:DefaultHome
echo [Checking JDK_HOME Variable]
IF DEFINED JDK_HOME "%JDK_HOME%"\bin\javac.exe -version >nul 2>&1
if ERRORLEVEL 1 goto :UserHome25
echo [Found Java]
set JAVA="%JDK_HOME%"\bin\javac.exe
set PATH=%Path%;%JDK_HOME%\bin
goto :JavaFound

:UserHome25
echo [Checking Default JDK 1.7.0_25 Folder]
IF DEFINED JDK_HOME25 %JDK_HOME25%\javac.exe -version >nul 2>&1
if ERRORLEVEL 1 goto :UserHome25_
echo [Found Java]
set JAVA=%JDK_HOME25%\javac.exe
set PATH=%Path%;%JDK_HOME25%
goto :JavaFound

:UserHome25_
IF DEFINED JDK_HOME25 %JDK_HOME25_%\javac.exe -version >nul 2>&1
if ERRORLEVEL 1 goto :UserHome40
echo [Found Java]
set JAVA=%JDK_HOME25_%\javac.exe
set PATH=%Path%;%JDK_HOME25_%`
goto :JavaFound

:UserHome40
echo [Checking Default JDK 1.7.0_40 Folder]
IF DEFINED JDK_HOME40 %JDK_HOME40%\javac.exe -version >nul 2>&1
if ERRORLEVEL 1 goto :UserHome40_
echo [Found Java]
set JAVA=%JDK_HOME40%\javac.exe
set PATH=%Path%;%JDK_HOME40%`
goto :JavaFound

:UserHome40_
IF DEFINED JDK_HOME40 %JDK_HOME40_%\javac.exe -version >nul 2>&1
if ERRORLEVEL 1 goto :usage
echo [Found Java]
set JAVA=%JDK_HOME40_%\javac.exe
set PATH=%Path%;%JDK_HOME40_%`
goto :JavaFound

:usage
echo javac.exe was not found on your path!
echo This script requires:
echo 1) an installation of the Java Development Kit, and
echo 2) either javac.exe is on the path, or that the JDK_HOME environment variable 
echo is set and points to the installation directory. By default it will look under
echo C:\Program Files\Java\jdk1.7.0_25\bin, and
echo C:\Program Files\Java\jdk1.7.0_40\bin

:JavaFound
if exist %CLASS_PATH%\*.class (
  del /Q %CLASS_PATH%\*.class
)

%JAVA% -classpath %BUILD_CLASS_PATH% %SRC_PATH% -verbose 2>&1
if ERRORLEVEL 1 goto :errorend
echo ==================================
echo COMPILATION COMPLETED SUCCESSFULLY 
echo ==================================
goto :eof

:errorend
echo ==================================
echo COMPILATION FAILED
echo Check above to find the compilation errors
echo ==================================