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

public class IntegerResponseType implements SetupResponseType<Integer> {
    private static final Pattern NUMBER_PATTERN = Pattern.compile("[+\\-]?\\d+");
    private static final IntegerResponseType INSTANCE = new IntegerResponseType();

    public static IntegerResponseType getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isValidInput(final String input) {
        return NUMBER_PATTERN.matcher(input).matches();
    }

    @Override
    public Integer getValue(final String input) {
        return Integer.parseInt(input);
    }

    @Override
    public String userFriendlyString() {
        return "integer";
    }

    @Override
    public void appendDocument(final Document document, final String name, final String input) {
        document.append(name, getValue(input));
    }
}
