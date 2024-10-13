#!/bin/zsh

# format source files
find . -name "*.java" -print | xargs astyle -q

# find and delete backups that astyle creates
a=$(find . -name "*.orig" -print)
b=$(echo -e "$a" | wc -l)
if [ $b -gt 0 ]
then
  echo -e "$a" | xargs rm
fi

# compile
find . -name "*.java" -print | xargs javac -d out

# run
java -cp out client.client 3 4 5 6 7
java -cp out server.server 3 4
