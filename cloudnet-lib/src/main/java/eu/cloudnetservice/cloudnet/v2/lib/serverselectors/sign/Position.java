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

package eu.cloudnetservice.cloudnet.v2.lib.serverselectors.sign;

import java.util.Objects;

public class Position {

    private final String group;
    private final String world;
    private final double x;
    private final double y;
    private final double z;

    public Position(String group, String world, double x, double y, double z) {
        this.group = group;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getZ() {
        return z;
    }

    public String getWorld() {
        return world;
    }

    public double getY() {
        return y;
    }

    public double getX() {
        return x;
    }

    public String getGroup() {
        return group;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = group != null ? group.hashCode() : 0;
        result = 31 * result + (world != null ? world.hashCode() : 0);
        temp = Double.doubleToLongBits(x);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(z);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Position)) {
            return false;
        }

        final Position position = (Position) o;

        if (Double.compare(position.x, x) != 0) {
            return false;
        }
        if (Double.compare(position.y, y) != 0) {
            return false;
        }
        if (Double.compare(position.z, z) != 0) {
            return false;
        }
        if (!Objects.equals(group, position.group)) {
            return false;
        }
        return Objects.equals(world, position.world);
    }
}
