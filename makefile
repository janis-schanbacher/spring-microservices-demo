up: clean mongo-db-up config-server-up discovery-server-up user-management-service-up song-service-up zuul-gateway-up
down: mongo-db-down config-server-down discovery-server-down user-management-service-down song-service-down zuul-gateway-down
services-up: discovery-server-up user-management-service-up song-service-up zuul-gateway-up

get-running-processes:
	netstat -tnlp | grep :8888
	netstat -tnlp | grep :8761
	netstat -tnlp | grep :8083
	netstat -tnlp | grep :8081
	netstat -tnlp | grep :8082
	netstat -tnlp | grep :8762
	echo "use 'kill -9 <pid>' to kill the the desired process"

clean:
	nohup mvn clean

mongo-db-up:
	mkdir -p data-mongodb
	mongod --dbpath=./data-mongodb --fork --logpath mongod.log

mongo-db-up-no-daemon:
	mkdir -p data-mongodb
	mongod --dbpath=./data-mongodb

mongo-db-down:
	mongod --shutdown --dbpath=./data-mongodb


config-server-up:
	cd configServer/ && nohup mvn spring-boot:run --quiet & cd -

config-server-up-no-daemon:
	cd configServer/ && mvn spring-boot:run ; cd -

config-server-down:
	kill -9 $(lsof -t -i:8888)


discovery-server-up:
	cd discoveryServer/ && nohup mvn spring-boot:run --quiet & cd -

discovery-server-up-no-daemon:
	cd discoveryServer/ && mvn spring-boot:run ; cd -

discovery-server-down:
	kill -9 $(lsof -t -i:8761)


user-management-service-up:
	cd userManagementService/ && nohup mvn spring-boot:run --quiet & cd -

user-management-service-up-no-daemon:
	cd userManagementService/ && mvn spring-boot:run ; cd -

user-management-service-down:
	kill -9 $(lsof -t -i:8083)


auth-service-up:
	cd authService/ && nohup mvn spring-boot:run --quiet & cd -

auth-service-up-no-daemon:
	cd authService/ && mvn spring-boot:run ; cd -

auth-service-down:
	kill -9 $(lsof -t -i:8081)


song-service-up:
	cd songService/ && nohup mvn spring-boot:run --quiet & cd -

song-service-up-no-daemon:
	cd songService/ && mvn spring-boot:run ; cd -

song-service-down:
	kill -9 $(lsof -t -i:8082)


zuul-gateway-up:
	cd zuulGateway/ && nohup mvn spring-boot:run --quiet & cd -

zuul-gateway-up-no-daemon:
	cd zuulGateway/ && mvn spring-boot:run ; cd -

zuul-gateway-down:
	kill -9 $(lsof -t -i:8762)
