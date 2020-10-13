#!/bin/bash
#
# Copyright 2017 Tarek Hosni El Alaoui
# Copyright 2020 CloudNetService
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

cd "$(dirname "$0")"
while true; do
  java -Dfile.encoding=UTF-8 -Xmx128m -jar CloudNet-Master.jar --installWrapper
  echo "If you want to completely stop the server process now, press Ctrl+C before the time is up!"
  echo "Rebooting in:"
  for i in 5 4 3 2 1; do
    echo "$i..."
    sleep 1
  done
  echo "Rebooting now!"
done
