#!/bin/bash

_terminate() {
	echo "Terminating..."
	kill -9 `pgrep -P $$`
	cd Logs
	cat *.out | sort > All.log
	grep " Lock" All.log > lock.log
	exit
}

trap _terminate SIGINT SIGTERM SIGKILL

CODE_DIR="src/distributedlock"
DEPENDENCIES="../protobuf-java-3.0.0.jar"
COMPILED_DIR="Compiled"
LOGS_DIR="Logs"

echo "Compiling..."

javac -classpath $DEPENDENCIES $CODE_DIR/*.java -d $COMPILED_DIR

echo "Compiled."

i=0
while [ $i -lt $2 ]
do
	java -classpath $DEPENDENCIES:$COMPILED_DIR DistributedLock -i $i $@ > $LOGS_DIR/DistributedLock.$i.out &
	let i=i+1
done

while true
do
	:
done