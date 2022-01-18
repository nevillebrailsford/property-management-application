echo off

rem copy this file to a working directory before running it
rem once this script executes correctly, replace the jar files
rem in the maven repository
rem add module-info to javax.mail

echo "====================================================="
echo "= Build javax.activation module information         ="
echo "====================================================="

set ROOT_DIR=%cd%

rmdir /S /Q work
rmdir /S /Q modules

mkdir work 
mkdir modules

set JAVAX_ACTIVATION_JAR=..\.m2\repository\javax\activation\activation\1.1\activation-1.1.jar
set JAVAX_ACTIVATION=activation

set JAVAX_MAIL_JAR=..\.m2\repository\com\sun\mail\javax.mail\1.6.2\javax.mail-1.6.2.jar
set JAVAX_MAIL=java.mail

jdeps --generate-module-info work %JAVAX_ACTIVATION_JAR%

set JARPATH=%JAVAX_ACTIVATION_JAR%
set MOD=%JAVAX_ACTIVATION%

copy %ROOT_DIR%\%JARPATH% %ROOT_DIR%\modules\%MOD%.jar

rem extract original jar

rmdir /S /Q classes
mkdir classes

cd classes
jar xf %ROOT_DIR%\%JARPATH%

rem compile module-info 

cd %ROOT_DIR%\work\%MOD%
javac -p %MOD% -d %ROOT_DIR%/classes module-info.java

rem update output jar

jar uf %ROOT_DIR%\modules\%MOD%.jar -C %ROOT_DIR%\classes module-info.class

cd %ROOT_DIR%

echo "====================================================="
echo "= Build javax.mail       module information         ="
echo "====================================================="

jdeps --module-path %ROOT_DIR%\modules --add-modules activation --generate-module-info work %JAVAX_MAIL_JAR%

set JARPATH=%JAVAX_MAIL_JAR%
set MOD=%JAVAX_MAIL%

copy %ROOT_DIR%\%JARPATH% %ROOT_DIR%\modules\%MOD%.jar

rem extract original jar

rmdir /S /Q classes
mkdir classes

cd classes
jar xf %ROOT_DIR%\%JARPATH%

rem compile module-info.java

cd %ROOT_DIR%\work\%MOD%

echo "================================================================"
echo " You need to add uses javax.mail.Provider to the end of file    "
echo " module-info.java so notepad being opened to allow this         "
echo "================================================================"
notepad module-info.java

javac --module-path %ROOT_DIR%\modules --add-modules activation -d %ROOT_DIR%\classes module-info.java

rem update output jar

jar uf %ROOT_DIR%\modules\%MOD%.jar -C %ROOT_DIR%\classes module-info.class

cd %ROOT_DIR%

rmdir /S /Q work
rmdir /S /Q classes

echo module jars are in %ROOT_DIR%\module

echo ready