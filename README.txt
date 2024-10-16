First, compile the program with the following command:

find . -name "*.java" -print | xargs javac -d out

Then, for tcp, run server, cache, and client with the following commands:

java -cp out server.server 20000 tcp &
java -cp out cache.cache 20001 localhost 20000 tcp &
sleep 1
java -cp out client.client localhost 20000 localhost 20001 tcp

Then, enter the following commands when prompted:

get cache_fl/File1.txt
get cache_fl/File2.txt
put client_fl/File4.txt

The first command gets File1 from the cache.
The second command gets File2 from the server (checks cache first).
The third command sends File4 from client to server.

For udp, use the following bash commands:

java -cp out server.server 20000 snw &
java -cp out cache.cache 20001 localhost 20000 snw &
sleep 1
java -cp out client.client localhost 20000 localhost 20001 snw

Then, enter the same get/put commands when prompted. These do not work for some reason.

When I put the two UDP sockets on the same port, I get an error saying the address is already in use.
When I put the two UDP sockets on different ports, the transmission times out.

I wish I understood what the issue is.
