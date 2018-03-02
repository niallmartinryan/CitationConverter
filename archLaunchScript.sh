#!/bin/bash
# @author : Niall Martin Ryan
echo "compiling"
javac -cp ".:/home/tiphany/College/fyp/Resources/citeproc-java-tool-1.0.1/lib/*:/home/tiphany/College/fyp/Resources/*:/home/tiphany/College/fyp/CitationConverter/*:/home/tiphany/College/fyp/Resources/styles-master/:/home/tiphany/College/fyp/Resources/styles-master/dependent/" App.java MyItemProvider.java Resources.java
read -p "Press enter to continue"
echo "executing"
java -cp ".:/home/tiphany/College/fyp/Resources/citeproc-java-tool-1.0.1/lib/*:/home/tiphany/College/fyp/Resources/*:/home/tiphany/College/fyp/CitationConverter/*:/home/tiphany/College/fyp/Resources/styles-master/:/home/tiphany/College/fyp/Resources/styles-master/dependent/" App
