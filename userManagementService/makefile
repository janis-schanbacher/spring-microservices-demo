all: up
up: mongo-db-up run
down: mongo-db-down

mongo-db-up:
	mkdir -p ../data-mongodb
	mongod --dbpath=../data-mongodb --fork --logpath ../mongod.log

mongo-db-down:
	mongod --shutdown --dbpath=../data-mongodb

run:
	mvn spring-boot:run
