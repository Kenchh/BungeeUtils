package me.reykench.commands;

import me.reykench.BungeeUtils;
import me.reykench.Text;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class Shout extends Command {
    public Shout(String name, String... aliases) {
        super(name, BungeeUtils.SHOUT_PERM, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        /* IS NOT PLAYER */
        if (!(sender instanceof ProxiedPlayer)) {
            Text.sendMessage(sender, "Only players can use this command!");
            return;
        }

        if (args.length < 2) {
            Text.usage(sender, this.getName() + " <server> <message>");
            return;
        }

        /* GETTING THE MESSAGE HE'S TRYING TO ANNOUNCE */
        StringBuilder str = new StringBuilder();
        for (String arg : args) {
            if(arg.equals(args[0])) {
                continue;
            }
            str.append(arg).append(" ");
        }

        /* SENDING MESSAGE */
        Text.shoutMessage(sender, str.toString().trim(), args[0]);
    }
}
