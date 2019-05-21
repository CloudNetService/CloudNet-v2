#!/bin/sh
screen -RS CloudNet java -XX:+UseG1GC -XX:MaxGCPauseMillis=50 -XX:CompileThreshold=100 -XX:MaxPermSize=256M -Xmx128m -jar CloudNet-Master.jar
