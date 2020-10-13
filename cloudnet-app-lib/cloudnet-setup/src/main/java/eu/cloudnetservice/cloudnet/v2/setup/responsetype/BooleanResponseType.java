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

package eu.cloudnetservice.cloudnet.v2.setup.responsetype;

import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;
import eu.cloudnetservice.cloudnet.v2.setup.SetupResponseType;

import java.util.regex.Pattern;

public class BooleanResponseType implements SetupResponseType<Boolean> {
    private static final Pattern FALSE_PATTERN = Pattern.compile("(false|n(o)?)", Pattern.CASE_INSENSITIVE);
    private static final Pattern TRUE_PATTERN = Pattern.compile("(true|y(es)?)", Pattern.CASE_INSENSITIVE);

    @Override
    public boolean isValidInput(final String input) {
        return TRUE_PATTERN.matcher(input).matches() || FALSE_PATTERN.matcher(input).matches();
    }

    @Override
    public Boolean getValue(final String input) {
        return TRUE_PATTERN.matcher(input).matches();
    }

    @Override
    public String userFriendlyString() {
        return "boolean (y/yes or n/no)";
    }

    @Override
    public void appendDocument(final Document document, final String name, final String input) {
        document.append(name, getValue(input));
    }
}
