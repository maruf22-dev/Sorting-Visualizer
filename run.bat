javac -d compiledClasses --module-path "javafx-sdk-15.0.1/lib" --add-modules javafx.controls *.java
java -cp compiledClasses/ --module-path "javafx-sdk-15.0.1/lib" --add-modules javafx.controls SortingVisualizer
pause