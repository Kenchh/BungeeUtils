package me.reykench.commands;

import me.reykench.BungeeUtils;
import me.reykench.Text;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class Mail extends Command {

    private final String usage = this.getName() + " <read|delete|send>";

    public static ArrayList<UUID> alerts = new ArrayList<>();

    public Mail(String name, String... aliases) {
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
            Text.usage(sender, usage);
            return;
        }

        /* GETTING THE WANTED ACTION */
        MailAction action = null;
        for (MailAction ma : MailAction.values()) if (ma.name.equalsIgnoreCase(args[0])) action = ma;

        if (action == null) {
            Text.usage(sender, usage);
            return;
        }

        /* HANDLING PLAYERS */
        ProxiedPlayer player = (ProxiedPlayer) sender;
        args = Arrays.copyOfRange(args, 1, args.length);
        switch (action) {
            case READ:
                if (args.length != 0) {
                    Text.usage(sender, this.getName() + " read");
                    return;
                }

                Text.listMail(player);

                break;
            case SEND:
                if (args.length < 2) {
                    Text.usage(sender, this.getName() + " send <user> <message>");
                    return;
                }

                /* MESSAGED PLAYER NOT ONLINE */
                ProxiedPlayer receiver = Text.matchPlayer(args[0]);
                if (receiver == null || !receiver.isConnected()) {
                    Text.notOnline(sender);
                    return;
                }

                if(receiver.getUniqueId() == player.getUniqueId()) {
                    Text.error(player, "You can not send mail to yourself.");
                    return;
                }

                /* GETTING THE PLAYER'S MESSAGE */
                StringBuilder str = new StringBuilder();
                for (int i = 1; i < args.length; i++) str.append(args[i]).append(" ");

                Text.sendMail(player, receiver, str.toString().trim());

                break;
            case DELETE:
                if (args.length != 0) {
                    Text.usage(sender, this.getName() + " delete");
                    return;
                }

                Text.sendMessage(sender, Text.MAIL_PREFIX + "You have cleared your mailbox.");
                BungeeUtils.getInstance().getDatabase().deleteMail(player);

                break;
            case ON:
                if(alerts.contains(((ProxiedPlayer) sender).getUniqueId())) {
                    alerts.remove(((ProxiedPlayer) sender).getUniqueId());
                }
                Text.sendMessage(sender, Text.MAIL_PREFIX + "Your mail alerts are now " + ChatColor.GREEN + "enabled" + ChatColor.GRAY + ".");
                break;
            case OFF:
                if(!alerts.contains(((ProxiedPlayer) sender).getUniqueId())) {
                    alerts.add(((ProxiedPlayer) sender).getUniqueId());
                }
                Text.sendMessage(sender, Text.MAIL_PREFIX + "Your mail alerts are now " + ChatColor.RED + "disabled" + ChatColor.GRAY + ".");
                break;
        }

    }

    private enum MailAction {
        READ("read"), DELETE("delete"), SEND("send"), ON("on"), OFF("off");

        public final String name;
        MailAction(String name){
            this.name = name;
        }
    }

    public static class MailElement {

        private final UUID sender;
        private final String text;
        public int read;

        public MailElement(UUID sender, String text, int read) {
            this.sender = sender;
            this.text = text;
            this.read = read;
        }

        public UUID getSender() {
            return sender;
        }

        public String getText() {
            return text;
        }

        public int wasRead() {
            return read;
        }

    }

}
