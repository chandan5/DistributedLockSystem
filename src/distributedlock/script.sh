#!/bin/bash

javac -cp ../../../protobuf-java-2.6.1.jar:. *.java
if [ "$1" == "-n" -a "$3" == "-o" ]
then
    for ((myid = 0 ; myid < $2 ; myid++ ));
        do echo "$myid";
        java -cp ../../../protobuf-java-2.6.1.jar:. DistributedLock -i "$myid" "${@:1}"
    done
fi

