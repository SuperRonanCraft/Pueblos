package me.RonanCraft.Pueblos.resources.files.msgs;

import me.RonanCraft.Pueblos.Pueblos;
import me.RonanCraft.Pueblos.resources.claims.*;
import me.RonanCraft.Pueblos.resources.files.FileLanguage;
import me.RonanCraft.Pueblos.resources.tools.Confirmation;
import me.RonanCraft.Pueblos.resources.tools.HelperDate;
import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Message {
    static FileLanguage getLang() {
        return Pueblos.getInstance().getFiles().getLang();
    }

    public static void sms(CommandSender sendi, String msg, Object placeholderInfo) {
        if (!msg.equals(""))
            sendi.sendMessage(placeholder(sendi, getPrefix() + msg, placeholderInfo));
    }

    public static void sms(CommandSender sendi, List<String> msg, Object placeholderInfo) {
        if (msg != null && !msg.isEmpty()) {
            msg.forEach(str -> msg.set(msg.indexOf(str), placeholder(sendi, str, placeholderInfo)));
            sendi.sendMessage(msg.toArray(new String[0]));
        }
    }

    private static String getPrefix() {
        return getLang().getString("Messages.Prefix");
    }

    public static String placeholder(CommandSender p, String str, Object info) {
        if (str != null) {
            if (Pueblos.getInstance().PlaceholderAPI)
                try {
                    str = PlaceholderAPI.setPlaceholders((Player) p, str);
                } catch (Exception e) {
                    //Something went wrong with PAPI
                }
            if (str.contains("%player_name%"))
                str = str.replaceAll("%player_name%", p.getName());
            if (str.contains("%player_uuid%"))
                if (p instanceof Player)
                    str = str.replaceAll("%player_uuid%", ((Player) p).getUniqueId().toString());
            //Placeholders based off info
            if (info instanceof Claim)
                str = getPlaceholder(str, (Claim) info, null);
            else if (info instanceof ClaimMember)
                str = getPlaceholder(str, (ClaimMember) info, null);
            else if (info instanceof ClaimRequest)
                str = getPlaceholder(str, (ClaimRequest) info);
            else if (info instanceof Confirmation)
                str = getPlaceholder(str, (Confirmation) info);
            else if (info instanceof Object[] && ((Object[]) info).length == 2)
                str = getPlaceholder(str, (Object[]) info);
        }
        if (str != null)
            return ChatColor.translateAlternateColorCodes('&', str);
        return null;
    }

    //Multiple variables
    private static String getPlaceholder(String str, Object[] info) {
        if (info[0] instanceof ClaimMember && info[1] instanceof CLAIM_FLAG_MEMBER)
            str = getPlaceholder(str, (ClaimMember) info[0], (CLAIM_FLAG_MEMBER) info[1]);
        else if (info[0] instanceof Claim && info[1] instanceof CLAIM_FLAG)
            str = getPlaceholder(str, (Claim) info[0], (CLAIM_FLAG) info[1]);
        return str;
    }

    //Claims
    private static String getPlaceholder(String str, Claim info, CLAIM_FLAG flag) {
        if (str.contains("%claim_name%"))
            str = str.replace("%claim_name%", info.getName());
        if (str.contains("%claim_members%"))
            str = str.replace("%claim_members%", String.valueOf(info.getMembers().size()));
        if (str.contains("%claim_owner%"))
            str = str.replace("%claim_owner%", String.valueOf(info.ownerName));
        if (str.contains("%claim_requests%"))
            str = str.replace("%claim_requests%", String.valueOf(info.getRequests().size()));
        if (flag != null) {
            if (str.contains("%claim_flag%"))
                str = str.replace("%claim_flag%", StringUtils.capitalize(flag.name().toLowerCase().replace("_", " ")));
            if (str.contains("%claim_flag_value%"))
                str = str.replace("%claim_flag_value%", info.getFlags().getFlag(flag).toString());
        }
        return str;
    }

    //Claim Members
    private static String getPlaceholder(String str, ClaimMember info, CLAIM_FLAG_MEMBER flag) {
        if (str.contains("%member_name%"))
            str = str.replace("%member_name%", info.getName());
        if (flag != null) {
            if (str.contains("%member_flag%"))
                str = str.replace("%member_flag%", StringUtils.capitalize(flag.name().toLowerCase().replace("_", " ")));
            if (str.contains("%member_flag_value%"))
                str = str.replace("%member_flag_value%", info.getFlags().getOrDefault(flag, flag.getDefault()).toString());
        }
        return getPlaceholder(str, info.claim, null);
    }

    //Claim Requests
    private static String getPlaceholder(String str, ClaimRequest info) {
        if (str.contains("%request_name%"))
            str = str.replace("%request_name%", info.name);
        if (str.contains("%request_date%"))
            str = str.replace("%request_date%", HelperDate.getDate(info.date));
        return getPlaceholder(str, info.claim, null);
    }

    //Confirmations
    private static String getPlaceholder(String str, Confirmation info) {
        if (str.contains("%confirm_type%"))
            str = str.replace("%request_type%", info.confirmation.name());
        return str;
    }
}
