@echo off
echo Cleaning...
rmdir /s /q target\classes 2>nul
mkdir target\classes

echo Compiling...
javac -encoding UTF-8 -cp "lib/*;lib/poi-5.2.3.jar;lib/commons-collections4-4.4.jar;target/classes" -d target/classes ^
src/com/library/util/*.java ^
src/com/library/models/*.java ^
src/com/library/dao/*.java ^
src/com/library/components/*.java ^
src/com/library/panels/*.java ^
src/com/library/main/*.java

echo Running...
color 0A
echo.
echo.
echo.  ---------------- BUILD AND DEVELOPED BY: GROUP 1 ----------------
echo.

:: Copy resources to target directory
xcopy /y /s "src\com\library\resources" "target\classes\com\library\resources\"

java -cp "lib/*;lib/poi-5.2.3.jar;lib/commons-collections4-4.4.jar;target/classes" com.library.main.Main 