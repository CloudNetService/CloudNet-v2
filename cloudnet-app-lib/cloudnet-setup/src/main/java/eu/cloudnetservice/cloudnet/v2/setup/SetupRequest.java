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

package eu.cloudnetservice.cloudnet.v2.setup;

import eu.cloudnetservice.cloudnet.v2.lib.interfaces.Nameable;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Class that represents a request for setup data.
 */
public class SetupRequest implements Nameable {
    /**
     * Name of this setup request.
     */
    private final String name;
    /**
     * Question that is displayed to the user.
     */
    private final String question;
    /**
     * Message that is shown when the input was invalid.
     */
    private final String invalidMessage;
    /**
     * The type of response that is required from the user.
     */
    private final SetupResponseType<?> responseType;
    /**
     * The validation function that determines whether the input is valid or not.
     */
    private final Predicate<String> validator;

    public SetupRequest(String name,
                        String question,
                        String invalidMessage,
                        SetupResponseType<?> responseType,
                        Predicate<String> validator) {
        this.name = name;
        this.question = question;
        this.invalidMessage = invalidMessage;
        this.responseType = responseType;
        this.validator = validator;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (question != null ? question.hashCode() : 0);
        result = 31 * result + (invalidMessage != null ? invalidMessage.hashCode() : 0);
        result = 31 * result + (responseType != null ? responseType.hashCode() : 0);
        result = 31 * result + (validator != null ? validator.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SetupRequest)) {
            return false;
        }

        final SetupRequest that = (SetupRequest) o;

        if (!Objects.equals(name, that.name)) {
            return false;
        }
        if (!Objects.equals(question, that.question)) {
            return false;
        }
        if (!Objects.equals(invalidMessage, that.invalidMessage)) {
            return false;
        }
        if (responseType != that.responseType) {
            return false;
        }
        return Objects.equals(validator, that.validator);
    }

    @Override
    public String toString() {
        return "SetupRequest{" +
            "name='" + name + '\'' +
            ", question='" + question + '\'' +
            ", invalidMessage='" + invalidMessage + '\'' +
            ", responseType=" + responseType +
            ", validator=" + validator +
            '}';
    }

    @Override
    public String getName() {
        return name;
    }

    public Predicate<String> getValidator() {
        return validator;
    }

    public boolean hasValidator() {
        return validator != null;
    }

    public SetupResponseType<?> getResponseType() {
        return responseType;
    }

    public String getInvalidMessage() {
        return invalidMessage;
    }

    public String getQuestion() {
        return question;
    }
}
