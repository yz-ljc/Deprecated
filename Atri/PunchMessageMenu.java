package top.yzljc.atri.menu.impl;

import com.frozenorb.qlib.menu.Button;
import com.frozenorb.qlib.menu.Menu;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import top.yzljc.atri.Atri;
import top.yzljc.atri.feature.nick.NickRank;
import top.yzljc.atri.common.constants.NickData;
import top.yzljc.atri.utils.CC;
import top.yzljc.atri.utils.handler.PacketEventsHook;

import java.util.*;

public class PunchMessageMenu extends Menu {

    private static final String DEFAULT_MSG_KEY = "default";
    private static final String LOVING_MSG_KEY = "loving";
    private static final String BOOP_MSG_KEY = "boop";
    private static final String SNOWBALL_MSG_KEY = "snowball";
    private static final String GLORY_MSG_KEY = "gloriously";
    private static final String SPOOK_MSG_KEY = "spook";
    private static final String FISH_MSG_KEY = "fish";
    private static final String CODEBREAK_MSG_KEY = "codebreak";
    private static final String SUN_MSG_KEY = "sun";
    private static final String CELEBRATION_MSG_KEY = "celebration";
    private static final String ROCKET_MSG_KEY = "rocket";
    private static final Map<UUID, String> selectedMessages = new HashMap<>();

    private static final Map<UUID, Long> clickCooldown = new HashMap<>();
    private static final long COOLDOWN_TIME = 500; // 500ms冷却时间

    public static String getSelectedPunchMessage(UUID uuid) {
        return selectedMessages.getOrDefault(uuid, DEFAULT_MSG_KEY);
    }

    public static void setSelectedPunchMessage(UUID uuid, String key) {
        selectedMessages.put(uuid, key);
    }

    public static void resetSelectedPunchMessage(UUID uuid) {
        selectedMessages.remove(uuid);
    }

    @Override
    public String getTitle(Player player) {
        return "Punch Messages";
    }

    @Override
    public int size(Player player) {
        return 54;
    }

    private String getFormattedName(Player player) {
        String prefix;
        String name = player.getName(); // player.getName() 已经被unsafe接管，直接读即可

        NickData nickData = PacketEventsHook.NICK_MAP.get(player.getUniqueId());
        if (nickData != null) {
            prefix = NickRank.getDisplayPrefixSection(nickData.displayRank());
        } else {
            prefix = Atri.getInstance().getTabList().getPrefix(player);
        }

        if (prefix == null) {
            prefix = "";
        }

        if (!prefix.isEmpty() && !prefix.endsWith(" ")) {
            prefix += " ";
        }

        return prefix + name;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        String selectedKey = getSelectedPunchMessage(player.getUniqueId());

        String nameFormat = getFormattedName(player);

        buttons.put(10, new PunchMessageButton(
                DEFAULT_MSG_KEY,
                Material.PAPER,
                "&cDefault Punch Message",
                nameFormat + " &7punched " + nameFormat + " &7into the sky!",
                selectedKey.equals(DEFAULT_MSG_KEY)
        ));

        buttons.put(11, new PunchMessageButton(
                LOVING_MSG_KEY,
                Material.PAPER,
                "&cLoving Punch Message",
                nameFormat + " &7lovingly punched " + nameFormat + " &7into the sky!",
                selectedKey.equals(LOVING_MSG_KEY)
        ));

        buttons.put(12, new PunchMessageButton(
                BOOP_MSG_KEY,
                Material.PAPER,
                "&cBoop Punch Message",
                nameFormat + " &d&lbooped " + nameFormat + " &7into the sky!",
                selectedKey.equals(BOOP_MSG_KEY)
        ));

        buttons.put(13, new PunchMessageButton(
                SNOWBALL_MSG_KEY,
                Material.PAPER,
                "&cSnowball Punch Message",
                nameFormat + " &f&lsnowballed " + nameFormat + " &7into the sky!",
                selectedKey.equals(SNOWBALL_MSG_KEY)
        ));

        buttons.put(14, new PunchMessageButton(
                GLORY_MSG_KEY,
                Material.PAPER,
                "&cGlorious Punch Message",
                nameFormat + " &6gloriously &7punched " + nameFormat + " &7into the sky!",
                selectedKey.equals(GLORY_MSG_KEY)
        ));

        buttons.put(15, new PunchMessageButton(
                SPOOK_MSG_KEY,
                Material.PAPER,
                "&cSpooky Punch Message",
                nameFormat + " &6&lspooked " + nameFormat + " &7into the sky!",
                selectedKey.equals(SPOOK_MSG_KEY)
        ));

        buttons.put(16, new PunchMessageButton(
                FISH_MSG_KEY,
                Material.PAPER,
                "&cFished Punch Message",
                nameFormat + " &a&lfished " + nameFormat + " &7into the sky!",
                selectedKey.equals(FISH_MSG_KEY)
        ));

        buttons.put(19, new PunchMessageButton(
                CODEBREAK_MSG_KEY,
                Material.PAPER,
                "&cCode Breaker Punch Message",
                nameFormat + " &e&k0!&c&lcode broke&e&k!0&r " + nameFormat + " &7into the sky!",
                selectedKey.equals(CODEBREAK_MSG_KEY)
        ));

        buttons.put(20, new PunchMessageButton(
                SUN_MSG_KEY,
                Material.PAPER,
                "&cSolar Punch Message",
                nameFormat + " &e&llaunched " + nameFormat + " &7into the &e&lsun&7!",
                selectedKey.equals(SUN_MSG_KEY)
        ));

        buttons.put(21, new PunchMessageButton(
                CELEBRATION_MSG_KEY,
                Material.PAPER,
                "&cCelebratory Punch Message",
                nameFormat + " &6&llaunched " + nameFormat + " &7into the sky in &6&la moment of celebration&7!",
                selectedKey.equals(CELEBRATION_MSG_KEY)
        ));

        buttons.put(22, new PunchMessageButton(
                ROCKET_MSG_KEY,
                Material.PAPER,
                "&cRocket Punch Message",
                nameFormat + " &c&lrocketed " + nameFormat + " &7into the sky!",
                selectedKey.equals(ROCKET_MSG_KEY)
        ));

        // Reset Punch Message (slot 48)
        buttons.put(48, new Button() {
            @Override
            public String getName(Player player) {
                return CC.translate("&cReset Punch Message");
            }

            @Override
            public List<String> getDescription(Player player) {
                String display = capitalizeFirst(getSelectedPunchMessage(player.getUniqueId()));
                return Collections.singletonList(CC.translate("&eCurrently Selected: " + display));
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.BARRIER;
            }

            @Override
            public void clicked(Player player, int i, ClickType clickType) {
                if (!canClick(player)) return;

                resetSelectedPunchMessage(player.getUniqueId());
                player.sendMessage(CC.translate("&cYour Punch Message has been reset."));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                new PunchMessageMenu().openMenu(player);
            }
        });

        // Back button (slot 49)
        buttons.put(49, new Button() {
            @Override
            public String getName(Player player) {
                return CC.translate("&cBack");
            }

            @Override
            public List<String> getDescription(Player player) {
                return Collections.emptyList();
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.ARROW;
            }

            @Override
            public void clicked(Player player, int i, ClickType clickType) {
                if (!canClick(player)) return;
                player.closeInventory();
            }
        });

        // Info description (slot 50)
        buttons.put(50, new Button() {
            @Override
            public String getName(Player player) {
                return CC.translate("&aPunching Players");
            }

            @Override
            public List<String> getDescription(Player player) {
                return Arrays.asList(
                        CC.translate("&7Punching players into sky!")
                );
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.BOOK;
            }

            @Override
            public void clicked(Player player, int i, ClickType clickType) {
                if (!canClick(player)) return;
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
            }
        });

        return buttons;
    }

    // 防止重复点击的方法
    private static boolean canClick(Player player) {
        UUID uuid = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        Long lastClick = clickCooldown.get(uuid);

        if (lastClick != null && (currentTime - lastClick) < COOLDOWN_TIME) {
            return false;
        }

        clickCooldown.put(uuid, currentTime);
        return true;
    }

    private static class PunchMessageButton extends Button {
        private final String key;
        private final Material material;
        private final String title;
        private final String message;
        private final boolean selected;

        public PunchMessageButton(String key, Material material, String title, String message, boolean selected) {
            this.key = key;
            this.material = material;
            this.title = title;
            this.message = message;
            this.selected = selected;
        }

        @Override
        public String getName(Player player) {
            return CC.translate(title);
        }

        @Override
        public List<String> getDescription(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add(CC.translate("&8Punch Message"));
            lore.add("");
            lore.add(CC.translate("&aRight-Click to preview!"));

            if (selected) {
                lore.add(CC.translate("&cYou already selected this Punch Message!"));
            } else {
                lore.add(CC.translate("&eClick to use!"));
            }

            return lore;
        }

        @Override
        public Material getMaterial(Player player) {
            return material;
        }

        @Override
        public void clicked(Player player, int i, ClickType clickType) {
            if (!canClick(player)) return;

            // 右键预览功能
            if (clickType == ClickType.RIGHT) {
                String preview = message.replace("{target}", "{target}");

                try {
                    if (org.bukkit.Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                        Class<?> placeholderAPIClass = Class.forName("me.clip.placeholderapi.PlaceholderAPI");
                        java.lang.reflect.Method setPlaceholdersMethod = placeholderAPIClass.getMethod("setPlaceholders", Player.class, String.class);
                        preview = (String) setPlaceholdersMethod.invoke(null, player, preview);
                    } else {
                        preview = preview.replace("%tab_placeholder_tab_tabprefix%", "");
                    }
                } catch (Exception e) {
                    preview = preview.replace("%tab_placeholder_tab_tabprefix%", "");
                }

                preview = CC.translate(preview);
                player.sendMessage(CC.translate("&aPreview: ") + preview);
                return;
            }

            // 左键选择功能
            if (selected) {
                player.sendMessage(CC.translate("&cYou have already selected this Punch Message!"));
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                return;
            }

            setSelectedPunchMessage(player.getUniqueId(), key);
            player.sendMessage(CC.translate("&aPunch message changed successfully!"));
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            new PunchMessageMenu().openMenu(player);
        }
    }

    private static String capitalizeFirst(String input) {
        if (input == null || input.isEmpty()) return "";
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
}
