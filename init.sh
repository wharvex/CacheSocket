#!/bin/zsh

rm /home/tim/projects/CacheSocket/client_fl/File*.txt
rm /home/tim/projects/CacheSocket/cache_fl/File*.txt
rm /home/tim/projects/CacheSocket/server_fl/File*.txt

cp /home/tim/projects/CacheSocket/static_fl/File1.txt /home/tim/projects/CacheSocket/cache_fl/
cp /home/tim/projects/CacheSocket/static_fl/File1.txt /home/tim/projects/CacheSocket/server_fl/
cp /home/tim/projects/CacheSocket/static_fl/File2.txt /home/tim/projects/CacheSocket/server_fl/
cp /home/tim/projects/CacheSocket/static_fl/File3.txt /home/tim/projects/CacheSocket/server_fl/
cp /home/tim/projects/CacheSocket/static_fl/File4.txt /home/tim/projects/CacheSocket/server_fl/
