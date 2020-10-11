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

package eu.cloudnetservice.cloudnet.v2.bridge.internal.util;

import eu.cloudnetservice.cloudnet.v2.api.CloudAPI;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

@SuppressWarnings("deprecation")
public class ItemStackBuilder {

    protected ItemMeta itemMeta;
    protected ItemStack itemStack;

    public ItemStackBuilder(Material material) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = itemStack.getItemMeta();
    }

    /**
     * @deprecated will only work in versions lower than 1.13
     */
    @Deprecated
    public ItemStackBuilder(int material) {
        this.itemStack = new ItemStack(Material.getMaterial(material));
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemStackBuilder(Material material, int amount) {
        this.itemStack = new ItemStack(material, amount);
        this.itemMeta = itemStack.getItemMeta();
    }

    /**
     * @deprecated will only work in versions lower than 1.13
     */
    @Deprecated
    public ItemStackBuilder(int material, int amount) {
        this.itemStack = new ItemStack(material, amount);
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemStackBuilder(Material material, int amount, int sub) {
        this.itemStack = new ItemStack(material, amount, (short) sub);
        this.itemMeta = itemStack.getItemMeta();
    }

    /**
     * @deprecated will only work in versions lower than 1.13
     */
    @Deprecated
    public ItemStackBuilder(int material, int amount, int sub) {
        this.itemStack = new ItemStack(material, amount, (short) sub);
        this.itemMeta = itemStack.getItemMeta();
    }

    /**
     * Gets a Material whether by name or by id if not used in MC 1.13+
     *
     * @param name the materialName of the wanted material or null when the id should be used
     * @param id   the materialId of the wanted material or any other number when the name should be used
     *
     * @return the material or null if not existing
     */
    public static Material getMaterialIgnoreVersion(String name, int id) {
        if (name == null) {
            try {
                return Material.getMaterial(id);
            } catch (ExceptionInInitializerError | NoSuchMethodError exception) {
                CloudAPI.getInstance().getLogger().logp(Level.WARNING,
                                                        ItemStackBuilder.class.getSimpleName(),
                                                        "getMaterialIgnoreVersion",
                                                        String.format(
                                                            "Can't get material by id %d! Beginning with MC 1.13 you HAVE to use material names!%n",
                                                            id),
                                                        exception);
                return null;
            }
        }
        return Material.getMaterial(name);
    }

    public static ItemStackBuilder builder(Material material) {
        return new ItemStackBuilder(material);
    }

    public static ItemStackBuilder builder(Material material, int amount) {
        return new ItemStackBuilder(material, amount);
    }

    public static ItemStackBuilder builder(Material material, int amount, int sub) {
        return new ItemStackBuilder(material, amount, sub);
    }

    /**
     * @deprecated will only work in versions lower than 1.13
     */
    @Deprecated
    public static ItemStackBuilder builder(int material) {
        return new ItemStackBuilder(material);
    }

    /**
     * @deprecated will only work in versions lower than 1.13
     */
    @Deprecated
    public static ItemStackBuilder builder(int material, int amount) {
        return new ItemStackBuilder(material, amount);
    }

    /**
     * @deprecated will only work in versions lower than 1.13
     */
    @Deprecated
    public static ItemStackBuilder builder(int material, int amount, int sub) {
        return new ItemStackBuilder(material, amount, sub);
    }


    public ItemStackBuilder enchantment(Enchantment enchantment, int value) {
        itemMeta.addEnchant(enchantment, value, true);
        return this;
    }

    public ItemStackBuilder color(Color color) {
        if (itemMeta instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta) itemMeta).setColor(color);
        }
        return this;
    }

    public ItemStackBuilder owner(String name) {
        if (itemMeta instanceof SkullMeta) {
            ((SkullMeta) itemMeta).setOwner(name);
        }
        return this;
    }

    public ItemStackBuilder displayName(String name) {
        itemMeta.setDisplayName(name);
        return this;
    }

    public ItemStackBuilder lore(String... lore) {
        itemMeta.setLore(Arrays.asList(lore));
        return this;
    }

    public ItemStackBuilder lore(List<String> lore) {
        itemMeta.setLore(lore);
        return this;
    }

    public ItemStack build() {
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}
