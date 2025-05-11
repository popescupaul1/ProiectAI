@echo off
setlocal

set OPENCV_JAR="D:\Coding\Java\opencv\build\java\opencv-4110.jar"
set OPENCV_DLL_PATH="D:\Coding\Java\opencv\build\java\x64"

java -cp .;%OPENCV_JAR% -Djava.library.path=%OPENCV_DLL_PATH% svm.SVM

endlocal
pause
