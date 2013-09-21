#!/bin/bash
set -o errexit

DIR_PATH=`pwd`
JAR_PATH="$DIR_PATH/bombman.jar:$DIR_PATH/lib/*"
CLASS_PATH="$DIR_PATH/bombmanplayer"
BUILD_CLASS_PATH="$JAR_PATH:$CLASS_PATH"
SRC_PATH="$DIR_PATH/bombmanplayer/PlayerAI.java"

# Run
java -classpath $BUILD_CLASS_PATH RunClient $@