#!/bin/zsh

rm ./client_fl/File*.txt
rm ./cache_fl/File*.txt
rm ./server_fl/File*.txt

cp ./static_fl/File1.txt ./cache_fl/
cp ./static_fl/File1.txt ./server_fl/
cp ./static_fl/File2.txt ./server_fl/
cp ./static_fl/File3.txt ./server_fl/
cp ./static_fl/File4.txt ./client_fl/
