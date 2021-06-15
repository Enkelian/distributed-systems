## Running the Zookeeper

### Running servers

`sudo docker-compose -f stack.yml up`

### Running console

`sudo docker run -it --rm --link nosek_anna_3_zoo1_1:zookeeper --net nosek_anna_3_default zookeeper zkCli.sh -server zookeeper`

### AppRunner arguments
* host (e.g. `0.0.0.0:2181`)
* program to run when /z is created (e.g. `gedit`)
