@ECHO OFF

set JDK_HOME25="C:\Program Files\Java\jdk1.7.0_25\bin"
set JDK_HOME40="C:\Program Files\Java\jdk1.7.0_40\bin"
set JRE7="C:\Program Files\Java\jre7\bin"

set JDK_HOME86_25="C:\Program Files (x86)\Java\jdk1.7.0_25\bin"
set JDK_HOME86_40="C:\Program Files (x86)\Java\jdk1.7.0_40\bin"
set JRE867="C:\Program Files (x86)\Java\jre7\bin"
set JARPATH="bombman.jar" 

echo [Checking Path Variable]
java.exe -version >nul 2>&1
if ERRORLEVEL 1 goto :DefaultHome
ECHO [Java Found]
java.exe -jar %JARPATH% %*
goto :eof

:DefaultHome
echo [Checking Java_home Variable]
IF DEFINED JAVA_HOME "%JAVA_HOME%"\bin\java.exe -version >nul 2>&1
if ERRORLEVEL 1 goto :DefaultJDKHome
ECHO [Java Found]
"%JAVA_HOME%"\bin\java.exe -jar %JARPATH% %*
goto :eof

:DefaultJDKHome
echo [Checking Jdk_home Variable]
IF DEFINED JDK_HOME "%JDK_HOME%"\bin\java.exe -version >nul 2>&1
if ERRORLEVEL 1 goto :DefaultJREHome
ECHO [Java Found]
"%JDK_HOME%"\bin\java.exe -jar %JARPATH% %*
goto :eof

:DefaultJREHome
echo [Checking Jre_home Varibale]
IF DEFINED JRE_HOME "%JRE_HOME%"\bin\java.exe -version >nul 2>&1
if ERRORLEVEL 1 goto :UserHome25
ECHO [Java Found]
"%JRE_HOME%"\bin\java.exe -j %JARPATH% %*
goto :eof

:UserHome25
echo [Checking Default Jdk 1.7.0_25 Folder]
IF DEFINED JDK_HOME25 %JDK_HOME25%\java.exe -version >nul 2>&1
if ERRORLEVEL 1 goto :UserHome25_86
ECHO [Java Found]
%JDK_HOME25%\java.exe -jar %JARPATH% %*
goto :eof

:UserHome25_86
IF DEFINED JDK_HOME25 %JDK_HOME86_25%\java.exe -version >nul 2>&1
if ERRORLEVEL 1 goto :UserHome40
ECHO [Java Found]
%JDK_HOME86_25%\java.exe -jar %JARPATH% %*
goto :eof

:UserHome40
echo [Checking Default Jdk 1.7.0_40 Folder]
IF DEFINED JDK_HOME40 %JDK_HOME40%\java.exe -version >nul 2>&1
if ERRORLEVEL 1 goto :JREHOME
ECHO [Java Found]
%JDK_HOME40%\java.exe -jar %JARPATH% %*
goto :eof

:UserHome40_86
IF DEFINED JDK_HOME40 %JDK_HOME86_40%\java.exe -version >nul 2>&1
if ERRORLEVEL 1 goto :JREHOME
ECHO [Java Found]
%JDK_HOME86_40%\java.exe -jar %JARPATH% %*
goto :eof

:JREHome
echo [Checking Default Jre7 Folder]
IF DEFINED JRE7 %JRE7%\java.exe -version >nul 2>&1
if ERRORLEVEL 1 goto :JREHome86
ECHO [Java Found]
%JRE7%\java.exe -jar %JARPATH% %*
goto :eof

:JREHome86
IF DEFINED JRE7 %JRE867%\java.exe -version >nul 2>&1
if ERRORLEVEL 1 goto :usage
ECHO [Java Found]
%JRE867%\java.exe -jar %JARPATH% %*
goto :eof

:usage
echo java.exe was not found on your path!
echo This script requires:
echo 1) an installation of the Java Development Kit or an installation of the Java Runtime Environment, and
echo 2) either java.exe is on the path, or that the one of JAVA_HOME, JDK_HOME or JRE_HOME environment variables 
echo is set and points to the installation directory. If not, it will look under
echo C:\Program Files\Java\jdk1.7.0_25\bin, C:\Program Files\Java\jdk1.7.0_40\bin and 
echo C:\Program Files\Java\jre7\bin