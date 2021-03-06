package me.reykench.commands;

import me.reykench.BungeeUtils;
import me.reykench.Text;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class BugReports extends Command {
    public BugReports(String name, String... aliases) {
        super(name, BungeeUtils.LIST_BUGREPORTS_PERM, aliases);
    }

    public static ArrayList<UUID> alerts = new ArrayList<>();

    @Override
    public void execute(CommandSender sender, String[] args) {

        if(args.length == 0) {
            Text.listBugReports(sender);
            return;
        }

        if(args[0].equalsIgnoreCase("list")) {
            Text.listBugReports(sender);
            return;
        }

        if(!(sender instanceof ProxiedPlayer)) {
            Text.sendMessage(sender, "Only players can use this command!");
            return;
        }


        if(args.length < 2) {
            if(args[0].equalsIgnoreCase("delete")) {
                Text.usage(sender, "bugreports delete <ID>");
            }
            if(args[0].equalsIgnoreCase("alerts")) {
                Text.usage(sender, "bugreports alerts <on|off>");
            }
            return;
        }

        if(args[0].equalsIgnoreCase("delete")) {
            int id = Integer.parseInt(args[1]);
            BungeeUtils.getInstance().getDatabase().deleteBug(id);
            Text.sendMessage(sender, Text.BUGREPORT_PREFIX + "Bug report " + ChatColor.LIGHT_PURPLE + id + ChatColor.GRAY + " has been deleted.");
            return;
        }

        if(args[1].equalsIgnoreCase("on")) {
            if(alerts.contains(((ProxiedPlayer) sender).getUniqueId())) {
                alerts.remove(((ProxiedPlayer) sender).getUniqueId());
            }
            Text.sendMessage(sender, Text.BUGREPORT_PREFIX + "Your bugreport alerts are now " + ChatColor.GREEN + "enabled" + ChatColor.GRAY + ".");
            return;
        }

        if(args[1].equalsIgnoreCase("off")) {
            if(!alerts.contains(((ProxiedPlayer) sender).getUniqueId())) {
                alerts.add(((ProxiedPlayer) sender).getUniqueId());
            }
            Text.sendMessage(sender, Text.BUGREPORT_PREFIX + "Your bugreport alerts are now " + ChatColor.RED + "disabled" + ChatColor.GRAY + ".");
            return;
        }

    }

}
