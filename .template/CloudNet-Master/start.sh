#!/bin/sh
screen -S CloudNet java -XX:+UseG1GC -XX:MaxGCPauseMillis=50  -XX:+OptimizeStringConcat -XX:CompileThreshold=100 -XX:MaxPermSize=256M -Xmx128m -jar CloudNet-Master.jar --ssl