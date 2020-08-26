package me.reykench.commands;

import me.reykench.BungeeUtils;
import me.reykench.Text;
import me.reykench.database.SQLManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class BugReport extends Command {
    public BugReport(String name, String... aliases) {
        super(name, BungeeUtils.NORMAL_PERM, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        /* IS NOT PLAYER */
        if (!(sender instanceof ProxiedPlayer)) {
            Text.sendMessage(sender, "Only players can use this command!");
            return;
        }

        if (args.length < 1) {
            Text.usage(sender, this.getName() + " <bug>");
            return;
        }

        StringBuilder str = new StringBuilder();
        for (String arg : args)
            str.append(arg).append(" ");

        Text.sendMessage(sender, Text.BUGREPORT_PREFIX + ChatColor.GRAY + "Your bug report has been sent.");
        Text.sendMessage(sender, Text.BUGREPORT_PREFIX + ChatColor.GRAY + "Thank you for taking your time!");

        for(ProxiedPlayer pp : BungeeUtils.getInstance().getProxy().getPlayers()) {
            if(!pp.hasPermission(BungeeUtils.LIST_BUGREPORTS_PERM)) continue;

            if(BugReports.alerts.contains(pp.getUniqueId())) continue;

            Text.sendMessage(pp, Text.BUGREPORT_PREFIX + ChatColor.LIGHT_PURPLE + sender.getName() + ChatColor.GRAY + " reported: " + str.toString().trim());
        }

        BugReportElement bug = new BugReportElement(-1, ((ProxiedPlayer) sender).getUniqueId(), sender.getName(), str.toString().trim());
        BungeeUtils.getInstance().getDatabase().createBug(bug);

    }

    public static class BugReportElement {

        private final UUID player;
        private final String playerName;
        private final String bug;
        private final int id;

        public BugReportElement(int id, UUID player, String playerName, String bug) {
            this.player = player;
            this.playerName = playerName;
            this.bug = bug;
            if(id == -1) {
                this.id = BungeeUtils.getInstance().getReportConfig().bugreportcount;
                BungeeUtils.getInstance().getReportConfig().increaseBugReportCount();
            } else {
                this.id = id;
            }
        }

        public int getId() {
            return id;
        }

        public String getBug() {
            return bug;
        }

        public UUID getPlayer() {
            return player;
        }

        public String getPlayerName() {
            return playerName;
        }
    }
}
