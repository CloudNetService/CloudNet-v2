#!/bin/sh
screen -RS Wrapper-1 java -XX:+UseG1GC -XX:MaxGCPauseMillis=50 -XX:CompileThreshold=100 -XX:MaxPermSize=256M -Xmx64m -jar CloudNet-Wrapper.jar
