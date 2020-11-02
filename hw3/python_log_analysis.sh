#!/bin/bash
# log analysis in python

# create input directory on HDFS
hadoop fs -mkdir -p input

# put input files to HDFS
hdfs dfs -put ./input/* input

# run wordcount 
hadoop jar /usr/local/hadoop/share/hadoop/tools/lib/hadoop-streaming-2.7.2.jar \
-file mapper.py    -mapper mapper.py \
-file reducer.py   -reducer reducer.py \
-input input -output output_python

# print the output of wordcount
echo -e "\noutput:"
hdfs dfs -cat output_python/part-00000
