#!/bin/bash
# @author : Niall Martin Ryan
echo "compiling"
javac -cp ".:/mnt/c/Users/niall/Documents/A college/FourthYear/dissertation/citeproc-java-tool-1.0.1/citeproc-java-tool-1.0.1/lib/*:/mnt/c/Users/niall/Documents/A college/FourthYear/dissertation/testJavaDriver/*" App.java
read -p "Press enter to continue"
echo "executing"
java -cp ".:/mnt/c/Users/niall/Documents/A college/FourthYear/dissertation/citeproc-java-tool-1.0.1/citeproc-java-tool-1.0.1/lib/*:/mnt/c/Users/niall/Documents/A college/FourthYear/dissertation/testJavaDriver/*" App
