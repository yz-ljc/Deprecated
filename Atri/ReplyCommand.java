package top.yzljc.atri.command;

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

/**
 * /r、/reply 回复上一个私聊的玩家，五分钟内有效，风格参考 Hypixel。
 * 用法: /r <消息>
 */
public class ReplyCommand implements CommandExecutor {

    // Hypixel 风格：To/From 洋红色，名字统一白色，不显示称号
    private static final String TO_FORMAT = "<light_purple>To </light_purple><white>%s: </white><white>%s</white>";
    private static final String FROM_FORMAT = "<light_purple>From </light_purple><white>%s: </white><white>%s</white>";

    private final Atri plugin;

    public ReplyCommand(Atri plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(TC.mm("<red>仅玩家可使用回复命令。"));
            return true;
        }
        if (args.length < 1) {
            player.sendMessage(TC.mm("<gray>用法: <white>/" + label + " <消息></white>"));
            return true;
        }

        PrivateMessageManager pm = plugin.getPrivateMessageManager();
        if (pm == null) {
            player.sendMessage(TC.mm("<red>私聊功能暂不可用，请联系社区管理员处理!"));
            return true;
        }

        Player target = pm.getLastReplyTarget(player);
        if (target == null || !target.isOnline()) {
            player.sendMessage(TC.mm("<red>你没有可回复的玩家或对方已离线!"));
            return true;
        }
        if (target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(TC.mm("<red>你没有可回复的玩家!"));
            return true;
        }

        String message = Arrays.stream(args).collect(Collectors.joining(" "));
        String senderName = player.getName();
        String targetName = target.getName();

        player.sendMessage(TC.mm(String.format(TO_FORMAT, targetName, message)));
        target.sendMessage(TC.mm(String.format(FROM_FORMAT, senderName, message)));

        pm.setLastReply(player, target);

        return true;
    }
}
