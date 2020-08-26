package me.reykench.commands.message;

import me.reykench.BungeeUtils;
import me.reykench.Text;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class Message extends Command {

    public Message(String name, String... aliases) {
        super(name, BungeeUtils.NORMAL_PERM, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        /* IS NOT PLAYER */
        if (!(sender instanceof ProxiedPlayer)) {
            Text.sendMessage(sender, "Only players can use this command!");
            return;
        }

        /* DID NOT PROVIDE ENOUGH ARGUMENTS*/
        if (args.length < 2) {
            Text.usage(sender, this.getName() + " <user> <message>");
            return;
        }

        /* MESSAGED PLAYER NOT LINE*/
        ProxiedPlayer toMessage = Text.matchPlayer(args[0]);
        if (toMessage == null || !toMessage.isConnected()) {
            Text.notOnline(sender);
            return;
        }

        if(((ProxiedPlayer) sender).getUniqueId() == toMessage.getUniqueId()) {
            Text.error(sender, "You can not message yourself.");
            return;
        }

        /* GETTING THE PLAYER'S MESSAGE */
        StringBuilder str = new StringBuilder();
        for (int i = 1; i < args.length; i++) str.append(args[i]).append(" ");

        /* SENDING MESSAGE */
        Text.message((ProxiedPlayer) sender, toMessage, str.toString().trim());
    }
}
