package me.reykench;

import me.reykench.commands.BugReport;
import me.reykench.commands.Mail;
import me.reykench.commands.Report;
import me.reykench.commands.message.MessageHandler;
import net.luckperms.api.cacheddata.CachedDataManager;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class Text {

    public static final String GENERIC_PREFIX = "&d&l%s&8&l> &7";
    public static final String ERROR_PREFIX = "&d&lERROR&8&l> &7";
    public static final String MAIL_PREFIX = "&d&lMAIL&8&l> &7";
    public static final String REPORT_PREFIX = "&d&lREPORT&8&l> &7";
    public static final String BUGREPORT_PREFIX = "&d&lBUGREPORT&8&l> &7";
    public static final String SHOUT_PREFIX = "&d&lSHOUT&8&l> &7";

    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(new TextComponent(color(message)));
    }

    public static void error(CommandSender sender, String msg) {
        sendMessage(sender, ERROR_PREFIX + msg);
    }

    public static void usage(CommandSender sender, String usage) {
        sendMessage(sender, ERROR_PREFIX + "&7Usage: /" + usage);
    }

    public static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static void shoutMessage(CommandSender sender, String text, String server) {
        boolean exists = false;

        for(ServerInfo s : BungeeUtils.getInstance().getProxy().getServers().values()) {
            if(s.getName().equalsIgnoreCase(server)) {
                exists = true;
            }
        }

        if(!exists) {
            sendMessage(sender, Text.ERROR_PREFIX + "That server does not exist!");
            return;
        }

        Text.sendMessage(sender, Text.SHOUT_PREFIX + "Your shout message has been sent to " + ChatColor.LIGHT_PURPLE + server + ChatColor.GRAY + "!");

        if(sender instanceof ProxiedPlayer) {
            sendToServer((ProxiedPlayer) sender, text, server);
        } else {
            sendToServer(null, text, server);
        }
    }

    public static void announceMessage(CommandSender sender, String text) {
        if(sender instanceof ProxiedPlayer) {
            sendToAll((ProxiedPlayer) sender, text);
            return;
        }
        sendToAll(null, text);
    }

    public static void sendToAll(ProxiedPlayer pp, String message){
        for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
            if (pp != null) {
                sendMessage(p, ChatColor.GRAY + getPrefix(pp) + pp.getName() + ChatColor.YELLOW + ": " + message);
            } else {
                sendMessage(p, ChatColor.GRAY + "SERVER" + ChatColor.YELLOW + ": " + message);
            }
        }
    }

    public static void sendToServer(ProxiedPlayer pp, String message, String server){
        for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {

            if(!p.getServer().getInfo().getName().equalsIgnoreCase(server)) {
                continue;
            }

            if(pp != null) {
                sendMessage(p, ChatColor.GRAY + getPrefix(pp) + pp.getName() + ChatColor.LIGHT_PURPLE + ": " + message);
            } else {
                sendMessage(p, ChatColor.GRAY + "SERVER" + ChatColor.LIGHT_PURPLE + ": " + message);
            }
        }
    }

    public static String getPrefix(ProxiedPlayer pp) {

        User u = BungeeUtils.getInstance().api.getUserManager().getUser(pp.getUniqueId());
        if (u == null) return "";

        String rank = u.getPrimaryGroup();

        CachedDataManager data = u.getCachedData();

        return ChatColor.translateAlternateColorCodes('&',
                data.getMetaData().getPrefix() == null ? rank.toUpperCase() : data.getMetaData().getPrefix());
    }

    public static void notOnline(CommandSender sender) {
        sendMessage(sender, ERROR_PREFIX + " That player is not online!");
    }

    public static void message(ProxiedPlayer sender, ProxiedPlayer receiver, String text) {
        final String toSender = String.format(GENERIC_PREFIX + getPrefix(receiver) + receiver.getName() + ChatColor.GRAY + ": %s", "TO", text);
        final String toReceiver = String.format(GENERIC_PREFIX + getPrefix(sender) + sender.getName() + ChatColor.GRAY + ": %s", "FROM", text);

        if (sender.isConnected()) sendMessage(sender, toSender);
        if (receiver.isConnected()) sendMessage(receiver, toReceiver);

        if (sender.isConnected()) MessageHandler.setReplier(sender.getUniqueId(), receiver.getUniqueId());
    }

    public static void notOnlineAnymore(ProxiedPlayer player) {
        sendMessage(player, ERROR_PREFIX + "That player is no longer online!");
    }

    public static void noneToReply(ProxiedPlayer player) {
        sendMessage(player, ERROR_PREFIX + "There is no one to reply!");
    }

    public static ProxiedPlayer matchPlayer(String s) {
        return BungeeUtils.getInstance().getProxy().getPlayer(s);
    }

    public static ProxiedPlayer matchPlayer(UUID uuid) {
        return BungeeUtils.getInstance().getProxy().getPlayer(uuid);
    }

    /*
        MAIL SECTION
     */

    public static void listMail(ProxiedPlayer player) {
        ArrayList<Mail.MailElement> currentMail = BungeeUtils.getInstance().getDatabase().getMail(player);

        /* SENDING MAIL MESSAGE */
        if (!currentMail.isEmpty()) {
            Text.sendMessage(player, MAIL_PREFIX + "Mailbox");
        } else {
            sendMessage(player, MAIL_PREFIX + "You do not have any mail.");
            return;
        }
        for(int i=0; i<currentMail.size(); i++) {
            String sender = matchPlayer(currentMail.get(i).getSender()) == null ? "NONE" : matchPlayer(currentMail.get(i).getSender()).getName();
            Text.sendMessage(player, String.format(MAIL_PREFIX + getPrefix(BungeeUtils.getInstance().getProxy().getPlayer(sender)) + "%s&7: %s", sender, currentMail.get(i).getText()));
            currentMail.get(i).read = 1;
        }
        BungeeUtils.getInstance().getDatabase().setMail(player, currentMail);
    }

    public static void listBugReports(CommandSender sender) {
        ArrayList<BugReport.BugReportElement> bugReports = BungeeUtils.getInstance().getDatabase().getBugReports();

        /* SENDING MAIL MESSAGE */
        if (!bugReports.isEmpty()) {
            Text.sendMessage(sender, BUGREPORT_PREFIX + "Last 10 Bug Reports:");
        } else {
            sendMessage(sender, BUGREPORT_PREFIX + "There are no bug reports.");
        }

        int itsize = 0;
        if(bugReports.size() >= 10) {
            itsize = bugReports.size() - 10;
        }

        for(int i=bugReports.size(); i>itsize; i--) {

            BugReport.BugReportElement bugreport = bugReports.get(i-1);

            Text.sendMessage(sender, ChatColor.YELLOW + "#" + bugreport.getId() + " "
                    + ChatColor.LIGHT_PURPLE + bugreport.getPlayerName() + ChatColor.GRAY + ": " + ChatColor.LIGHT_PURPLE + bugreport.getBug());
        }
    }

    public static void listReports(CommandSender sender) {
        ArrayList<Report.PlayerReportElement> latestReports = BungeeUtils.getInstance().getDatabase().getReports();

        /* SENDING MAIL MESSAGE */
        if (!latestReports.isEmpty()) {
            Text.sendMessage(sender, REPORT_PREFIX + "Last 10 Player Reports:");
        } else {
            sendMessage(sender, REPORT_PREFIX + "There are no player reports.");
        }

        int itsize = 0;
        if(latestReports.size() >= 10) {
            itsize = latestReports.size() - 10;
        }

        for(int i=latestReports.size(); i>itsize; i--) {

            Report.PlayerReportElement report = latestReports.get(i-1);

            Text.sendMessage(sender, ChatColor.YELLOW + "#" + report.getId() + " "
                    + ChatColor.LIGHT_PURPLE + report.getReporterName() + ChatColor.GRAY + " reported "
                    + ChatColor.LIGHT_PURPLE + report.getReportedName() + ChatColor.GRAY + " for " + ChatColor.LIGHT_PURPLE + report.getReason());
        }
    }

    public static void sendMail(ProxiedPlayer sender, ProxiedPlayer player, String text) {
        sendMessage(sender, MAIL_PREFIX + "&7You have sent a mail to &e" + getPrefix(player) + player.getName() + "&7.");
        if(!Mail.alerts.contains(player.getUniqueId()))
            sendMessage(player, MAIL_PREFIX + "&7You have received mail from &e" + getPrefix(sender) + sender.getName() + "&7!");
        BungeeUtils.getInstance().getDatabase().sendMail(sender, player, text);
    }

}
