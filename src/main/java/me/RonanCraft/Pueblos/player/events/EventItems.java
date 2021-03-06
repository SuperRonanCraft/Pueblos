package me.RonanCraft.Pueblos.player.events;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.Settings;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class EventItems implements Listener, PueblosEvents {

                        //Item, Player
    private final HashMap<UUID, UUID> items = new HashMap<>();
    private final HashMap<UUID, Integer> schedule = new HashMap<>();

    void load() {
        if (Pueblos.getInstance().getSettings().getBoolean(Settings.SETTING.PLAYER_PROTECTDEATHDROP))
            Bukkit.getPluginManager().registerEvents(this, Pueblos.getInstance());
        else
            HandlerList.unregisterAll(this);
    }

    @EventHandler
    private void onDespawn(ItemDespawnEvent e) {
        remove(e.getEntity().getUniqueId());
    }


    //(Added v1.1.0)
    //Stop picking up items from other claims
    @EventHandler
    private void onPickup(PlayerPickupItemEvent e) {
        if (e.isCancelled())
            return;
        if (items.containsKey(e.getItem().getUniqueId())) { //This item is a death item
            if (!e.getPlayer().getUniqueId().equals(items.get(e.getItem().getUniqueId()))) //Player who died matches this items owner
                e.setCancelled(true);
            remove(e.getItem().getUniqueId());
            return; //Let players who died in another claim pick up their items
        }
        Claim claim = getClaimMain(e.getItem().getLocation());
        if (claim != null && !claim.isMember(e.getPlayer()))
            e.setCancelled(true);
    }

    //(Added v1.1.0)
    //Disallow other players from picking up dead players items
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onDrop(PlayerDeathEvent e) {
        List<ItemStack> items = e.getDrops();
        for (ItemStack item : items) {
            Item drop = Objects.requireNonNull(e.getEntity().getLocation().getWorld()).dropItem(e.getEntity().getLocation(), item);
            UUID item_id = drop.getUniqueId();
            this.items.put(item_id, e.getEntity().getUniqueId());
        }
        e.getDrops().clear(); //Disallow dropping items normally
    }

    //Remove/Cancel schedule and items to protect
    private void remove(UUID item_id) {
        if (schedule.containsKey(item_id))
            if (Bukkit.getScheduler().isQueued(schedule.get(item_id)))
                Bukkit.getScheduler().cancelTask(schedule.get(item_id));
        schedule.remove(item_id);
        items.remove(item_id);
    }
}
