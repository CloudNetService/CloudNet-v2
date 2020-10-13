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

/**
 * Class to process arguments before and after calling a command.
 */
public abstract class CommandArgument {

    /**
     * The name of the argument to process
     */
    private final String name;

    public CommandArgument(String name) {
        this.name = name;
    }

    /**
     * Method to execute before an argument is processed.
     *
     * @param command     the command that is executed after all arguments are processed
     * @param commandLine the complete command line for this command
     */
    public abstract void preExecute(Command command, String commandLine);

    /**
     * Method to execute after a command is executed.
     *
     * @param command     the command that was executed before all arguments are processed
     * @param commandLine the complete command line for this command
     */
    public abstract void postExecute(Command command, String commandLine);

    public String getName() {
        return name;
    }
}
