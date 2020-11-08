@echo off
set JAR="target\dataproc.jar"
java -cp %JAR% CLI %*
