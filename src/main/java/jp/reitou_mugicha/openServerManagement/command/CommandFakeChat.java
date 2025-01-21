package jp.reitou_mugicha.openServerManagement.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandFakeChat implements CommandExecutor
{
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if (args.length < 2) {
            sender.sendMessage("使い方: /fakechat <対象> <内容>");
            return false;
        }

        String targetPlayerName = args[0];
        StringBuilder messageBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            messageBuilder.append(args[i]).append(" ");
        }

        String message = messageBuilder.toString().trim();
        Player target = Bukkit.getPlayer(targetPlayerName);

        if (message.contains("/"))
        {
            if(!sender.isOp())
            {
                return false;
            }
        }

        if (target == null) {
            sender.sendMessage("プレイヤーが存在しません。");
            return false;
        }

        target.chat(message);
        return true;
    }
}