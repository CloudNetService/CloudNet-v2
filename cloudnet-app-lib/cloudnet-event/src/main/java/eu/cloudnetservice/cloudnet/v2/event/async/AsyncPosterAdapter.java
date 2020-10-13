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

package eu.cloudnetservice.cloudnet.v2.event.async;

/**
 * Adapter for {@link AsyncPoster}
 * Provides empty method bodies for {@link AsyncPoster#onPreCall(AsyncEvent)}
 * and {@link AsyncPoster#onPostCall(AsyncEvent)}
 *
 * @param <E> the type of the event
 */
public class AsyncPosterAdapter<E extends AsyncEvent<?>> implements AsyncPoster<E> {

    @Override
    public void onPreCall(E event) {
    }

    @Override
    public void onPostCall(E event) {
    }
}
