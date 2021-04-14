@echo off
dir /b "target\amef-*.jar" > JAR
set /p JAR= < JAR
set JAR="target\%JAR%"
java -cp %JAR% amef.Master %*
