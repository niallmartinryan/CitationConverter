#!/bin/bash
# @author : Niall Martin Ryan
echo "compiling"
javac -cp ".:/root/Resources/citeproc-java-tool-1.0.1/lib/*:/root/Resources/*:/root/CitationConverter/*:/root/Resources/styles-master/:/root/Resources/styles-master/dependent/" App.java MyItemProvider.java Resources.java
read -p "Press enter to continue"
echo "executing"
java -d64 -Xmx4g -cp ".:/root/Resources/citeproc-java-tool-1.0.1/lib/*:/root/Resources/*:/root/CitationConverter/*:/root/Resources/styles-master/:/root/Resources/styles-master/dependent/" App $1
