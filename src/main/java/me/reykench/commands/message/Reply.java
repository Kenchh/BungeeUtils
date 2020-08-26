package me.reykench.commands.message;

import me.reykench.BungeeUtils;
import me.reykench.Text;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class Reply extends Command {


    public Reply(String name, String... aliases) {
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
        if (args.length < 1) {
            Text.usage(sender, this.getName() + " <message>");
            return;
        }

        /* HASN'T RECENTLY TALKED TO ANYONE */
        ProxiedPlayer playerSender = (ProxiedPlayer) sender;
        if (MessageHandler.getReplier(playerSender.getUniqueId()) == null) {
            Text.noneToReply(playerSender);
            return;
        }

        /* PLAYER TO REPLY NOT ONLINE */
        ProxiedPlayer playerReceiver = MessageHandler.getReplier(playerSender.getUniqueId());
        if (playerReceiver == null || !playerReceiver.isConnected()) {
            Text.notOnlineAnymore(playerSender);
            return;
        }

        /* SENDING MESSAGE */       /* GETTING THE PLAYER'S MESSAGE */
        StringBuilder str = new StringBuilder();
        for (String arg : args) str.append(arg).append(" ");
        Text.message(playerSender, playerReceiver, str.toString().trim());

    }

}
