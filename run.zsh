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
# args: server port, protocol
java -cp out server.server 20000 tcp &
server_pid=$!
# args: cache port, server ip, server port, protocol
java -cp out cache.cache 20001 localhost 20000 tcp &
cache_pid=$!
sleep 1
# args: server IP, server port, cache IP, cache port, protocol
java -cp out client.client localhost 20000 localhost 20001 tcp

# kill background processes
kill $server_pid
kill $cache_pid
