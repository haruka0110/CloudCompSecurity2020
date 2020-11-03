#!/bin/bash
# log analysis in python

# run log analysis 
hadoop jar /usr/local/hadoop/share/hadoop/tools/lib/hadoop-streaming-2.7.2.jar \
-file mapper.py    -mapper mapper.py \
-file reducer.py   -reducer reducer.py \
-input input -output output_python

# print the output of wordcount
echo -e "\noutput:"
hdfs dfs -cat output_python/part-00000
