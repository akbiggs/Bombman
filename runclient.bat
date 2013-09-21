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
java.exe -version >nul 2>&1
if ERRORLEVEL 1 goto :DefaultHome
ECHO [Java Found]
java.exe -classpath %BUILD_CLASS_PATH% RunClient %*
goto :eof

:DefaultHome
echo [Checking JDK_HOME Variable]
IF DEFINED JDK_HOME "%JDK_HOME%"\bin\java.exe -version >nul 2>&1
if ERRORLEVEL 1 goto :UserHome25
ECHO [Java Found]
"%JDK_HOME%"\bin\java.exe -classpath %BUILD_CLASS_PATH% RunClient %*
goto :eof

:UserHome25
echo [Checking Default JDK 1.7.0_25 Folder]
IF DEFINED JDK_HOME25 %JDK_HOME25%\java.exe -version >nul 2>&1
if ERRORLEVEL 1 goto :UserHome25_
ECHO [Java Found]
%JDK_HOME25%\java.exe -classpath %BUILD_CLASS_PATH% RunClient %*
goto :eof


:UserHome25_
IF DEFINED JDK_HOME25 %JDK_HOME25_%\java.exe -version >nul 2>&1
if ERRORLEVEL 1 goto :UserHome40
ECHO [Java Found]
%JDK_HOME25_%\java.exe -classpath %BUILD_CLASS_PATH% RunClient %*
goto :eof

:UserHome40
echo [checking default JDK 1.7.0_40 folder]
IF DEFINED JDK_HOME40 %JDK_HOME40%\java.exe -version >nul 2>&1
if ERRORLEVEL 1 goto :UserHome40_
ECHO [Java Found]
%JDK_HOME40%\java.exe -classpath %BUILD_CLASS_PATH% RunClient %*
goto :eof

:UserHome40_
IF DEFINED JDK_HOME40 %JDK_HOME40_%\java.exe -version >nul 2>&1
if ERRORLEVEL 1 goto :usage
ECHO [Java Found]
%JDK_HOME40_%\java.exe -classpath %BUILD_CLASS_PATH% RunClient %*
goto :eof

:usage
echo java.exe was not found on your path!
echo This script requires:
echo 1) an installation of the Java Development Kit, and
echo 2) either java.exe is on the path, or that the JDK_HOME environment variable 
echo is set and points to the installation directory. By default it will look under
echo C:\Program Files\Java\jdk1.7.0_25\bin, and
echo C:\Program Files\Java\jdk1.7.0_40\bin