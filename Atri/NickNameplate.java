package top.yzljc.atri.feature.nick;

import org.bukkit.Color;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Transformation;
import top.yzljc.atri.utils.handler.ProtocolLibHook;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class NickNameplate {
    private static final float NAMETAG_Y_OFFSET = 0.33f;
    private static final Map<UUID, Entity> NAMEPLATE_BY_REAL_UUID = new HashMap<>();

    public static void spawnFor(Player player, String fakeName, Plugin plugin) {
        removeFor(player);
        player.getWorld().getChunkAt(player.getLocation());
        String rank = ProtocolLibHook.NICK_MAP.containsKey(player.getUniqueId())
                ? ProtocolLibHook.NICK_MAP.get(player.getUniqueId()).displayRank()
                : null;
        TextDisplay display = player.getLocation().getWorld().spawn(player.getLocation(), TextDisplay.class, td -> {
            td.text(NickRank.getDisplayComponent(rank, fakeName));
            td.setBillboard(Display.Billboard.CENTER);
            td.setDefaultBackground(false);
            td.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
            td.setShadowed(false);
            td.setPersistent(false);
            td.setSeeThrough(true);
            Transformation t = td.getTransformation();
            t.getTranslation().add(0f, NAMETAG_Y_OFFSET, 0f);
            td.setTransformation(t);
        });
        player.addPassenger(display);
        player.hideEntity(plugin, display);
        NAMEPLATE_BY_REAL_UUID.put(player.getUniqueId(), display);
    }

    public static void removeFor(Player player) {
        Entity existing = NAMEPLATE_BY_REAL_UUID.remove(player.getUniqueId());
        if (existing != null && existing.isValid()) {
            if (existing.getVehicle() instanceof Player p) {
                p.removePassenger(existing);
            }
            existing.remove();
        }
    }

    public static void removeFor(UUID realUUID) {
        Entity existing = NAMEPLATE_BY_REAL_UUID.remove(realUUID);
        if (existing != null && existing.isValid()) {
            if (existing.getVehicle() instanceof Player p) {
                p.removePassenger(existing);
            }
            existing.remove();
        }
    }

    /** 关服/插件 disable 时清理所有 nick 名字板，避免 TextDisplay 残存。 */
    public static void removeAll() {
        for (Entity existing : NAMEPLATE_BY_REAL_UUID.values()) {
            if (existing != null && existing.isValid()) {
                if (existing.getVehicle() instanceof Player p) {
                    p.removePassenger(existing);
                }
                existing.remove();
            }
        }
        NAMEPLATE_BY_REAL_UUID.clear();
    }
}
