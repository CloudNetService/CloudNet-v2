#!/bin/sh
screen -S Wrapper-1 java -XX:+UseG1GC -XX:MaxGCPauseMillis=50  -XX:+OptimizeStringConcat -XX:CompileThreshold=100 -XX:MaxPermSize=256M -Xmx64m -jar CloudNet-Wrapper.jar
