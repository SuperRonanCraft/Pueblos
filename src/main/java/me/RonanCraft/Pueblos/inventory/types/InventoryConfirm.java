package me.RonanCraft.Pueblos.inventory.types;

import me.RonanCraft.Pueblos.inventory.*;
import me.RonanCraft.Pueblos.resources.claims.ClaimMain;
import me.RonanCraft.Pueblos.resources.claims.ClaimMember;
import me.RonanCraft.Pueblos.resources.claims.ClaimRequest;
import me.RonanCraft.Pueblos.resources.tools.Confirmation;
import me.RonanCraft.Pueblos.resources.tools.HelperClaim;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InventoryConfirm extends PueblosInvLoader implements PueblosInv_Confirming {

    private final HashMap<Player, HashMap<Integer, PueblosItem>> itemInfo = new HashMap<>();
    private final HashMap<Player, Confirmation> confirmation = new HashMap<>();

    @Override
    public Inventory open(Player p, Confirmation confirmation) {
        Inventory inv = Bukkit.createInventory(null, 9 * 5, getTitle(p, confirmation));

        addBorder(inv);

        HashMap<Integer, PueblosItem> itemInfo = new HashMap<>();

        addButtonBack(inv, p, itemInfo, PueblosInventory.CONFIRM, confirmation.info);

        for (ITEMS i : ITEMS.values()) {
            //Accept button
            ItemStack item = getItem(i.section, p, confirmation);
            int slot = i.slot;
            inv.setItem(slot, item);
            itemInfo.put(slot, new PueblosItem(item, ITEM_TYPE.NORMAL, i));
        }

        this.itemInfo.put(p, itemInfo);
        this.confirmation.put(p, confirmation);
        p.openInventory(inv);
        return inv;
    }

    @Override
    public void clickEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (    !itemInfo.containsKey(p)
                || !confirmation.containsKey(p)
                || checkItems(e, itemInfo.get(p))
                || !itemInfo.get(p).containsKey(e.getSlot()))
            return;

        Confirmation confirmation = this.confirmation.get(p);
        ITEMS buttonPressed = (ITEMS) this.itemInfo.get(p).get(e.getSlot()).info;
        switch (buttonPressed) {
            case ACCEPT:
                //p.sendMessage("Accepted!");
                switch (confirmation.type) {
                    case CLAIM_LEAVE:
                        HelperClaim.leaveClaim(p, (ClaimMember) confirmation.info);
                        break;
                    case MEMBER_REMOVE:
                        HelperClaim.removeMember(p, (ClaimMember) confirmation.info);
                        break;
                    case REQUEST_DECLINE:
                        HelperClaim.requestAction(false, p, (ClaimRequest) confirmation.info);
                        break;
                    case CLAIM_DELETE:
                        HelperClaim.deleteClaim(p, (ClaimMain) confirmation.info);
                        p.closeInventory();
                        return;
                }
                goBack(p, this.itemInfo.get(p));
                break;
            case CANCEL:
                //p.sendMessage("Declined?");
                goBack(p, this.itemInfo.get(p));
                break;
        }
    }

    @Override
    public void clear(Player p) {

    }

    @Override
    protected String getSection() {
        return "Confirm";
    }

    @Override
    protected List<String> getSections() {
        List<String> list = new ArrayList<>();
        for (ITEMS i : ITEMS.values())
            list.add(i.section);
        return list;
    }

    private enum ITEMS {
        ACCEPT("Accept", 22),
        CANCEL("Cancel", 16);

        String section;
        int slot;

        ITEMS(String section, int slot) {
            this.section = section;
            this.slot = slot;
        }
    }
}
