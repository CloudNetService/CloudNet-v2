#!/bin/bash
cd "$(dirname "$0")"
while true
do
java -XX:+UseG1GC -XX:MaxGCPauseMillis=50 -XX:MaxPermSize=256M -XX:CompileThreshold=100 -Dcloudnet.localwrapper.disableConsole=true -Xmx128m -jar CloudNet-Master.jar --installWrapper
echo "If you want to completely stop the server process now, press Ctrl+C before
the time is up!"
echo "Rebooting in:"
for i in 5 4 3 2 1
do
echo "$i..."
sleep 1
done
echo "Rebooting now!"
done 