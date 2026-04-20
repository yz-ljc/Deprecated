package top.yzljc.atri.manager;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PrivateMessageManager {

    private static final long REPLY_EXPIRY_MS = 5 * 60 * 1000L;

    private record LastReplyEntry(UUID targetUuid, long expiresAt) {

        boolean isExpired() {
                return System.currentTimeMillis() >= expiresAt;
            }
        }

    private final Map<UUID, LastReplyEntry> lastReplyBySender = new ConcurrentHashMap<>();

    public void setLastReply(Player sender, Player recipient) {
        long expiresAt = System.currentTimeMillis() + REPLY_EXPIRY_MS;
        lastReplyBySender.put(sender.getUniqueId(), new LastReplyEntry(recipient.getUniqueId(), expiresAt));
        lastReplyBySender.put(recipient.getUniqueId(), new LastReplyEntry(sender.getUniqueId(), expiresAt));
    }

    public Player getLastReplyTarget(Player sender) {
        LastReplyEntry entry = lastReplyBySender.get(sender.getUniqueId());
        if (entry == null || entry.isExpired()) {
            if (entry != null) {
                lastReplyBySender.remove(sender.getUniqueId());
            }
            return null;
        }
        return org.bukkit.Bukkit.getPlayer(entry.targetUuid);
    }

    public void clear(UUID playerUuid) {
        lastReplyBySender.remove(playerUuid);
    }
}
