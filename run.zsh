#!/bin/zsh

# # format source files
# find . -name "*.java" -print | xargs astyle -q
# 
# # find and delete backups that astyle creates
# a=$(find . -name "*.orig" -print | wc -l)
# if [ $a -gt 0 ]
# then
#   find . -name "*.orig" -print | xargs rm
# fi

# compile
find . -name "*.java" -print | xargs javac -d out

# run
java -cp out server.server 1025 tcp &
sleep 1
java -cp out client.client localhost 1025 localhost 1026 tcp
