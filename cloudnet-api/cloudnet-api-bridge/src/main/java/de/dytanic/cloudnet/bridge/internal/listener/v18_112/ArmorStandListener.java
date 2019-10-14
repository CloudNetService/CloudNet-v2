/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.internal.listener.v18_112;

import de.dytanic.cloudnet.bridge.internal.serverselectors.MobSelector;
import de.dytanic.cloudnet.lib.utility.Acceptable;
import de.dytanic.cloudnet.lib.utility.CollectionWrapper;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Tareko on 14.09.2017.
 */
public final class ArmorStandListener implements Listener {

    @EventHandler
    public void handle(PlayerArmorStandManipulateEvent e) {
        MobSelector.MobImpl mob = CollectionWrapper.filter(MobSelector.getInstance().getMobs().values(),
                                                           new Acceptable<MobSelector.MobImpl>() {
                                                               @Override
                                                               public boolean isAccepted(MobSelector.MobImpl value) {
                                                                   try {
                                                                       return e.getRightClicked()
                                                                               .getUniqueId()
                                                                               .equals(value.getDisplayMessage()
                                                                                            .getClass()
                                                                                            .getMethod("getUniqueId")
                                                                                            .invoke(value.getDisplayMessage()));
                                                                   } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                                                                       return false;
                                                                   }
                                                               }
                                                           });
        if (mob != null) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void handle(ItemDespawnEvent e) {
        MobSelector.MobImpl mob = CollectionWrapper.filter(MobSelector.getInstance().getMobs().values(),
                                                           new Acceptable<MobSelector.MobImpl>() {
                                                               @Override
                                                               public boolean isAccepted(MobSelector.MobImpl value) {
                                                                   return ((Entity) value.getDisplayMessage()).getPassenger() != null && e.getEntity()
                                                                                                                                          .getEntityId() == ((Entity) value
                                                                       .getDisplayMessage()).getPassenger().getEntityId();
                                                               }
                                                           });
        if (mob != null) {
            e.setCancelled(true);
        }
    }
}
