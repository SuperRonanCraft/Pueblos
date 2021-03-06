package me.RonanCraft.Pueblos.player.events;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.claims.enums.CLAIM_FLAG;
import me.RonanCraft.Pueblos.resources.claims.ClaimMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;

public class EventDamage implements PueblosEvents {

    private final HashMap<Entity, Integer> damageCooldown = new HashMap<>();

    //Damage Entity (Mobs and Players)
    void onDamage(EntityDamageByEntityEvent e) {
        final Entity damager = e.getDamager();
        final Entity damaged = e.getEntity();

        if (damageCooldown.containsKey(damaged)) {
            cooldown(damaged);
            e.setCancelled(true); //In a cooldown till we check the claims again
            return;
        } else if (damageCooldown.containsKey(damager)) {
            cooldown(damager);
            e.setCancelled(true); //In a cooldown till we check the claims again
            return;
        }

        Claim claim = getClaimAt(damager.getLocation(), false);
        if (claim == null)
            claim = getClaimAt(damaged.getLocation(), false);
        if (claim != null) { //BUG: if one player is inside a child claim, attacker can hit, defender cannot
            if (damaged instanceof Player && damager instanceof Player) { //Player vs Player
                if (((Boolean) claim.getFlags().getFlag(CLAIM_FLAG.PVP))) //PvP is allowed
                    return;
            } else if (damager instanceof Player && claim.isMember((Player) damager)) {
                return;
            } else if (damaged instanceof Monster && damaged.getCustomName() == null) //Garbage mob
                return;
            e.setCancelled(true);
            cooldown(damaged);
            cooldown(damager);
        }
    }

    void cooldown(Entity e) {
        if (damageCooldown.containsKey(e))
            Bukkit.getScheduler().cancelTask(damageCooldown.get(e));
        damageCooldown.put(e, Bukkit.getScheduler().scheduleSyncDelayedTask(Pueblos.getInstance(), () -> {
            damageCooldown.remove(e);
            //Bukkit.getServer().broadcastMessage("Removed " + e.getName());
        }, 20L));
    }
}
