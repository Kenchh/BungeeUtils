package me.reykench.commands;

import me.reykench.BungeeUtils;
import me.reykench.Text;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Arrays;

public class MailBox extends Command {

    public MailBox(String name, String... aliases) {
        super(name, BungeeUtils.NORMAL_PERM, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        /* IS NOT PLAYER */
        if (!(sender instanceof ProxiedPlayer)) {
            Text.sendMessage(sender, "Only players can use this command!");
            return;
        }

        /* HANDLING PLAYERS */
        ProxiedPlayer player = (ProxiedPlayer) sender;
        Text.listMail(player);

    }

}
