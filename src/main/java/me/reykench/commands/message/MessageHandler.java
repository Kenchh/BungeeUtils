package me.reykench.commands.message;

import me.reykench.BungeeUtils;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.UUID;

public class MessageHandler {

    private static final HashMap<UUID, UUID> replies = new HashMap<>();

    public static void setReplier(UUID player, UUID toSet) {
        if (replies.containsKey(player)) replies.replace(player, toSet);
        else replies.put(player, toSet);
    }

    public static ProxiedPlayer getReplier(UUID player) {
        if (!replies.containsKey(player)) return null;
        for (ProxiedPlayer p : BungeeUtils.getInstance().getProxy().getPlayers())
            if (p.getUniqueId().equals(replies.get(player))) return p;
        return null;
    }

}
