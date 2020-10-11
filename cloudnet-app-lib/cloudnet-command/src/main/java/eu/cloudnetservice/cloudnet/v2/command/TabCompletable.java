/*
 * Copyright 2017 Tarek Hosni El Alaoui
 * Copyright 2020 CloudNetService
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cloudnetservice.cloudnet.v2.command;

import java.util.List;

/**
 * Interface denoting classes that complete inputs and return a list of possible
 * completion candidates.
 */
public interface TabCompletable {

    /**
     * This method is called when a tab completion is requested by a {@link CommandSender}.
     *
     * @param argsLength the amount of arguments currently given to the command
     * @param lastWord   the last word, given to the command
     *
     * @return a list of tab complete candidates
     */
    List<String> onTab(long argsLength, String lastWord);

}
