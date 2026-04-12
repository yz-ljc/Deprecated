package top.yzljc.atri.utils.handler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import top.yzljc.atri.feature.nick.NickRank;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * 通过 TAB API 让 nick 后的 fakeName 按所选 rank 颜色显示（DEFAULT/VIP/VIP+/MVP/MVP+）。
 */
public final class TabNickHook {

    private static Boolean tabPresent;
    private static Object tabApiInstance;
    private static Method getPlayerByUuid;
    private static Method getTabListFormatManager;
    private static Method getNameTagManager;

    public static boolean isTabPresent() {
        if (tabPresent != null) return tabPresent;
        if (Bukkit.getPluginManager().getPlugin("TAB") == null) {
            tabPresent = false;
            return false;
        }
        try {
            Class<?> apiClass = Class.forName("me.neznamy.tab.api.TabAPI");
            Method getInstance = apiClass.getMethod("getInstance");
            tabApiInstance = getInstance.invoke(null);
            if (tabApiInstance == null) {
                tabPresent = false;
                return false;
            }
            getPlayerByUuid = apiClass.getMethod("getPlayer", UUID.class);
            getTabListFormatManager = apiClass.getMethod("getTabListFormatManager");
            try {
                getNameTagManager = apiClass.getMethod("getNameTagManager");
            } catch (NoSuchMethodException e) {
                getNameTagManager = null;
            }
            tabPresent = true;
            return true;
        } catch (Throwable t) {
            tabPresent = false;
            return false;
        }
    }

    private static void invokeTabListFormat(Object formatManager, Object tabPlayer, String prefix, String name, String suffix) {
        if (formatManager == null || tabPlayer == null) return;
        try {
            Class<?> tc = tabPlayer.getClass();
            Method setPrefix = formatManager.getClass().getMethod("setPrefix", tc, String.class);
            Method setName = formatManager.getClass().getMethod("setName", tc, String.class);
            Method setSuffix = formatManager.getClass().getMethod("setSuffix", tc, String.class);
            setPrefix.invoke(formatManager, tabPlayer, prefix);
            setName.invoke(formatManager, tabPlayer, name);
            setSuffix.invoke(formatManager, tabPlayer, suffix);
        } catch (Throwable ignored) {
        }
    }

    private static void invokeNameTagFormat(Object nameTagManager, Object tabPlayer, String prefix, String suffix) {
        if (nameTagManager == null || tabPlayer == null) return;
        try {
            Class<?> tc = tabPlayer.getClass();
            Method setPrefix = nameTagManager.getClass().getMethod("setPrefix", tc, String.class);
            Method setSuffix = nameTagManager.getClass().getMethod("setSuffix", tc, String.class);
            setPrefix.invoke(nameTagManager, tabPlayer, prefix);
            setSuffix.invoke(nameTagManager, tabPlayer, suffix);
        } catch (Throwable ignored) {
        }
    }

    public static void onNick(Player player, String fakeName, String displayRank) {
        if (!isTabPresent() || tabApiInstance == null) return;
        UUID uuid = player.getUniqueId();
        try {
            Object tabPlayer = getPlayerByUuid.invoke(tabApiInstance, uuid);
            if (tabPlayer == null) return;
            Object formatManager = getTabListFormatManager.invoke(tabApiInstance);
            Object tagManager = getNameTagManager != null ? getNameTagManager.invoke(tabApiInstance) : null;
            String color = NickRank.getColorCode(displayRank);
            String coloredName = color + fakeName;
            invokeTabListFormat(formatManager, tabPlayer, "", coloredName, "");
            if (tagManager != null) invokeNameTagFormat(tagManager, tabPlayer, color, "");
        } catch (Throwable ignored) {
        }
    }

    public static void onUnnick(Player player) {
        if (!isTabPresent() || tabApiInstance == null) return;
        UUID uuid = player.getUniqueId();
        try {
            Object tabPlayer = getPlayerByUuid.invoke(tabApiInstance, uuid);
            if (tabPlayer == null) return;
            Object formatManager = getTabListFormatManager.invoke(tabApiInstance);
            Object tagManager = getNameTagManager != null ? getNameTagManager.invoke(tabApiInstance) : null;
            invokeTabListFormat(formatManager, tabPlayer, null, null, null);
            if (tagManager != null) invokeNameTagFormat(tagManager, tabPlayer, null, null);
        } catch (Throwable ignored) {
        }
    }
}
