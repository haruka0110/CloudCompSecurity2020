#!/bin/bash
# create input directory on HDFS
hadoop fs -mkdir -p input
# put input files to HDFS
hdfs dfs -put ./input/* input
