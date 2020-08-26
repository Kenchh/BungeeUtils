package me.reykench.commands;

import me.reykench.BungeeUtils;
import me.reykench.Text;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class Announce extends Command {

    public Announce(String name, String... aliases) {
        super(name, BungeeUtils.ANNOUNCE_PERM, aliases);
    }

    public void execute(CommandSender sender, String[] args) {

        if (args.length < 1) {
            Text.usage(sender, this.getName() + " <message>");
            return;
        }

        /* GETTING THE MESSAGE HE'S TRYING TO ANNOUNCE */
        StringBuilder str = new StringBuilder();
        for (String arg : args) str.append(arg).append(" ");

        /* SENDING MESSAGE */
        Text.announceMessage(sender, str.toString().trim());

    }
}
