package me.RonanCraft.Pueblos.resources.tools;

import me.RonanCraft.Pueblos.inventory.PueblosInv;
import me.RonanCraft.Pueblos.resources.claims.Claim;
import me.RonanCraft.Pueblos.resources.claims.ClaimMain;
import me.RonanCraft.Pueblos.resources.claims.ClaimMember;
import me.RonanCraft.Pueblos.resources.claims.ClaimRequest;
import me.RonanCraft.Pueblos.resources.files.FileOther;
import me.RonanCraft.Pueblos.resources.files.msgs.Message;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class HelperItem {

    public static ItemStack getItem(ItemStack item, Player p, Object info) {
        //Item Title
        if (item.getItemMeta() != null) {
            String name = item.getItemMeta().getDisplayName();
            name = Message.placeholder(p, name, info);
            PueblosInv.setTitle(item, p, name);
            //Item Lore
            if (item.getItemMeta().getLore() != null) {
                List<String> lore = item.getItemMeta().getLore();
                lore.forEach(str -> lore.set(lore.indexOf(str), Message.placeholder(p, str, info)));
                PueblosInv.setLore(item, p, lore);
            }
            /*if (item.getType() == Material.PLAYER_HEAD) {
                SkullMeta meta = (SkullMeta) item.getItemMeta();
                if (info instanceof ClaimMember)
                    meta.setOwningPlayer(((ClaimMember) info).getPlayer());
                else if (info instanceof Claim && !((Claim) info).isAdminClaim())
                    meta.setOwningPlayer(((Claim) info).getOwner());
                else if (info instanceof ClaimRequest)
                    meta.setOwningPlayer(Bukkit.getOfflinePlayer(((ClaimRequest) info).id));
                //else //Laggy code
                //    meta.setOwningPlayer(Bukkit.getOfflinePlayers()[ThreadLocalRandom.current().nextInt(0, Bukkit.getOfflinePlayers().length)]);
                item.setItemMeta(meta);
            }*/
        }

        return item;
    }

    public static ItemStack getItemFromFile(String path, FileOther.FILETYPE file) {
        //System.out.println(path);
        //Item
        ItemStack item = new ItemStack(getMat(file.getString(path + "Item")));
        //Name
        PueblosInv.setTitle(item, null, file.getString(path + "Name"));
        //Lore
        PueblosInv.setLore(item, null, file.getStringList(path + "Lore"));
        return item;
    }

    public static void enchantItem(ItemStack item, Enchantment enchant) {
        item.addUnsafeEnchantment(enchant, 1);
        if (item.getItemMeta() != null) {
            ItemMeta meta = item.getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
        }
    }

    private static Material getMat(String str) {
        try {
            return Material.valueOf(str.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Material.BEDROCK;
        }
    }
}
