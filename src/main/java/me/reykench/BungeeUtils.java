package me.reykench;

import me.reykench.commands.*;
import me.reykench.commands.message.Message;
import me.reykench.commands.message.Reply;
import me.reykench.config.MySQLConfig;
import me.reykench.config.ReportConfig;
import me.reykench.database.ConnectionPoolManager;
import me.reykench.database.SQLManager;
import me.reykench.events.PlayerJoin;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedDataManager;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeUtils extends Plugin {

    public static final String ANNOUNCE_PERM = "swine.announce";
    public static final String SHOUT_PERM = "swine.shout";
    public static final String LIST_REPORTS_PERM = "swine.listreports";
    public static final String LIST_BUGREPORTS_PERM = "swine.listbugreports";
    public static final String NORMAL_PERM = "swine.normal";

    private static BungeeUtils bungeeUtils;
    private MySQLConfig sqlconfig;
    private ReportConfig reportConfig;
    private SQLManager database;
    public LuckPerms api;

    @Override
    public void onEnable() {
        bungeeUtils = this;

        /* DATABASE */
        sqlconfig = new MySQLConfig();
        reportConfig = new ReportConfig();
        ConnectionPoolManager pool = new ConnectionPoolManager(sqlconfig.hostname, sqlconfig.port, sqlconfig.username, sqlconfig.password, sqlconfig.database);
        database = new SQLManager(pool);

        /* API AND COMMANDS */
        api = LuckPermsProvider.get();
        getProxy().getPluginManager().registerListener(this, new PlayerJoin());
        regCommands();
        System.out.println("[BungeeUtils] Plugin now enabled!");
    }

    @Override
    public void onDisable() {
        System.out.println("[BungeeUtils] Plugin now disabled!");
        getDatabase().onDisable();
    }

    private void regCommands() {
        getProxy().getPluginManager().registerCommand(this, new Announce("announce"));
        getProxy().getPluginManager().registerCommand(this, new Shout("shout"));
        getProxy().getPluginManager().registerCommand(this, new Message("message", "msg", "m"));
        getProxy().getPluginManager().registerCommand(this, new Reply("reply", "r"));
        getProxy().getPluginManager().registerCommand(this, new Mail("mail"));
        getProxy().getPluginManager().registerCommand(this, new MailBox("mailbox", "listmails", "listmail", "mails"));
        getProxy().getPluginManager().registerCommand(this, new Report("report"));
        getProxy().getPluginManager().registerCommand(this, new Reports("reports", "listreports"));
        getProxy().getPluginManager().registerCommand(this, new BugReport("bugreport"));
        getProxy().getPluginManager().registerCommand(this, new BugReports("bugreports", "listbugreports"));
    }

    private void sendMessage(ProxiedPlayer pp, String message){
        pp.sendMessage(new TextComponent(message));
    }

    public SQLManager getDatabase() {
        return database;
    }

    public ReportConfig getReportConfig() {
        return reportConfig;
    }

    public static BungeeUtils getInstance() {
        return bungeeUtils;
    }
}
