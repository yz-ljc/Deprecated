package top.yzljc.atri.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import top.yzljc.atri.Atri;
import top.yzljc.atri.manager.PrivateMessageManager;
import top.yzljc.atri.utils.TC;

import java.util.Arrays;
import java.util.stream.Collectors;

public class TellCommand implements CommandExecutor {

    private static final String TO_FORMAT = "<light_purple>To </light_purple><white>%s</white><gray>:</gray> <white>%s</white>";
    private static final String FROM_FORMAT = "<light_purple>From </light_purple><white>%s</white><gray>:</gray> <white>%s</white>";

    private final Atri plugin;

    public TellCommand(Atri plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(TC.mm("<red>仅玩家可使用私聊命令。"));
            return true;
        }
        if (args.length < 2) {
            player.sendMessage(TC.mm("<gray>用法: <white>/" + label + " <玩家> <消息></white>"));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null || !target.isOnline()) {
            player.sendMessage(TC.mm("<red>该玩家不在线或不存在!"));
            return true;
        }
        if (target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(TC.mm("<red>你不能给自己发私聊!"));
            return true;
        }

        String message = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));
        if (message.isEmpty()) {
            player.sendMessage(TC.mm("<red>消息不能为空!"));
            return true;
        }

        String senderName = player.getName();
        String targetName = target.getName();

        player.sendMessage(TC.mm(String.format(TO_FORMAT, targetName, message)));
        target.sendMessage(TC.mm(String.format(FROM_FORMAT, senderName, message)));

        PrivateMessageManager pm = plugin.getPrivateMessageManager();
        if (pm != null) {
            pm.setLastReply(player, target);
        }

        return true;
    }
}
