package me.RonanCraft.Pueblos.resources;

import me.RonanCraft.Pueblos.resources.dependencies.DepPermissions;
import org.bukkit.command.CommandSender;

public class Permissions {
    private final DepPermissions depPerms = new DepPermissions();

    public void register() {
        depPerms.register();
    }

    private final String pre = "pueblos.";

    public boolean checkPerm(String str, CommandSender sendi) {
        return depPerms.hasPerm(str, sendi);
    }
}
