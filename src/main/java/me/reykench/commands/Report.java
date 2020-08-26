package me.reykench.commands;

import me.reykench.BungeeUtils;
import me.reykench.Text;
import me.reykench.database.SQLManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class Report extends Command {
    public Report(String name, String... aliases) {
        super(name, BungeeUtils.NORMAL_PERM, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        /* IS NOT PLAYER */
        if (!(sender instanceof ProxiedPlayer)) {
            Text.sendMessage(sender, "Only players can use this command!");
            return;
        }

        if (args.length < 2) {
            Text.usage(sender, this.getName() + " <user> <reason>");
            return;
        }

        ProxiedPlayer p = Text.matchPlayer(args[0]);
        if(p == null || !p.isConnected()) {
            Text.error(sender, "That player is not online or does not exist.");
            return;
        }

        StringBuilder str = new StringBuilder();
        for (String arg : args) {
            if(arg.equals(args[0])) {
                continue;
            }
            str.append(arg).append(" ");
        }

        if(((ProxiedPlayer) sender).getUniqueId() == p.getUniqueId()) {
            Text.error(sender, "You can not report yourself.");
            return;
        }

        Text.sendMessage(sender, Text.REPORT_PREFIX + ChatColor.GRAY + "You have reported " + ChatColor.LIGHT_PURPLE + p.getName() + ChatColor.GRAY + " for " + ChatColor.LIGHT_PURPLE + str.toString().trim());

        for(ProxiedPlayer pp : BungeeUtils.getInstance().getProxy().getPlayers()) {
            if(!pp.hasPermission(BungeeUtils.LIST_REPORTS_PERM)) continue;

            if(Reports.alerts.contains(pp.getUniqueId())) continue;

            Text.sendMessage(pp, Text.REPORT_PREFIX + ChatColor.LIGHT_PURPLE + sender.getName() + ChatColor.GRAY + " has reported " + ChatColor.LIGHT_PURPLE + p.getName() + ChatColor.GRAY + " for " + ChatColor.LIGHT_PURPLE + str.toString().trim());
        }

        PlayerReportElement pe = new PlayerReportElement(-1, ((ProxiedPlayer) sender).getUniqueId(), sender.getName(), p.getUniqueId(), p.getName(), str.toString().trim());
        BungeeUtils.getInstance().getDatabase().createReport(pe);

    }

    public static class PlayerReportElement {

        private final UUID reporter;
        private final String reporterName;
        private final UUID reportedPlayer;
        private final String reportedName;
        private final String reason;
        private final int id;

        public PlayerReportElement(int id, UUID reporter, String reporterName, UUID reportedPlayer, String reportedName, String reason) {
            this.reporter = reporter;
            this.reporterName = reporterName;
            this.reportedPlayer = reportedPlayer;
            this.reportedName = reportedName;
            this.reason = reason;
            if(id == -1) {
                this.id = BungeeUtils.getInstance().getReportConfig().reportcount;
                BungeeUtils.getInstance().getReportConfig().increaseReportCount();
            } else {
                this.id = id;
            }
        }

        public int getId() {
            return id;
        }

        public String getReason() {
            return reason;
        }

        public UUID getReportedPlayer() {
            return reportedPlayer;
        }

        public String getReportedName() {
            return reportedName;
        }

        public UUID getReporter() {
            return reporter;
        }

        public String getReporterName() {
            return reporterName;
        }
    }

}
