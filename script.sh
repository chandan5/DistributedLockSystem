#!/bin/bash
echo "Compiling Server...";
javac -cp ../protobuf-java-2.6.1.jar:src/distributedlock/ src/distributedlock/Clock.java  src/distributedlock/DistributedLockInterface.java src/distributedlock/RequestProto.java src/distributedlock/DistributedLock.java src/distributedlock/Pair.java src/distributedlock/RequestQueue.java -d test
echo "Starting servers...";
if [ "$1" == "-n" -a "$3" == "-o" ]
then
    cd test
    for ((myid = 0 ; myid < $2 ; myid++ ));
        do echo "$myid";
        java -cp ../protobuf-java-2.6.1.jar:. DistributedLock -i "$myid" "${@:1}"
    done
    cd ../..
fi

