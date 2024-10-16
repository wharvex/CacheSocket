#!/bin/zsh

# compile
find . -name "*.java" -print | xargs javac -d out

# run
# args: server port, protocol
java -cp out server.server 20000 snw &
server_pid=$!
# args: cache port, server ip, server port, protocol
java -cp out cache.cache 20001 localhost 20000 snw &
cache_pid=$!
sleep 1
# args: server IP, server port, cache IP, cache port, protocol
java -cp out client.client localhost 20000 localhost 20001 snw

# kill background processes
kill $server_pid
kill $cache_pid
