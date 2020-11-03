#!/bin/bash
# log analysis in Java

export HADOOP_CLASSPATH=${JAVA_HOME}/lib/tools.jar

# compile java code
hadoop com.sun.tools.javac.Main JavaLogAnalysis.java

# package
jar cf JavaLogAnalysis.jar JavaLogAnalysis*.class

# run log analysis
hadoop jar JavaLogAnalysis.jar JavaLogAnalysis input output_java

# print the output of wordcount
echo -e "\noutput:"
hdfs dfs -cat output_java/part-r-00000

