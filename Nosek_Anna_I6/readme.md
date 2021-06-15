### Compile proto
`sudo protoc -I=. --java_out=./src/main/gen --plugin=protoc-gen-grpc-java=protoc-gen-grpc-java-1.37.0-linux-x86_64 --grpc-java_out=./src/main/gen ./src/main/resources/proto/test.proto`
### Run nginx
`sudo nginx -c /home/anna/uni/sem6/distributed_systems/reverse-proxy/src/main/resources/nginx/nginx.conf`
### Test cases
* least connected - client1: add2, client2: add1, add1, add1 - two of the three add1s end up in one server
* weighted round-robin - complex-avg 5 times - first 3 are in calculator1, then calculator2, calculator3
* ip hashing - getprime on 2 clients running on the same machine