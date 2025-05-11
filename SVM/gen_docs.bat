set "OPENCV_JAR=D:\Coding\Java\opencv\build\java\opencv-4110.jar"


javadoc -d docs -classpath .;%OPENCV_JAR% ^
 svm\*.java ^
 svm\gui\*.java ^
 svm\util\*.java ^
 svm\io\*.java

pause