package jp.reitou_mugicha.openServerManagement.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandPayExperience implements CommandExecutor
{
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if (!(sender instanceof Player)) return false;
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) return false;

        if (args[1].toLowerCase().contains("l"))
        {
            int num = Integer.parseInt(args[1].substring(0, args[1].indexOf("l")));
            ((Player) sender).giveExpLevels(-num);
            target.giveExpLevels(num);

            sender.sendMessage(target.getName() + "に" + String.valueOf(num) + "レベル払いました。");
            target.sendMessage(sender.getName() + "から" + String.valueOf(num) + "レベルもらいました。");
        } else {
            ((Player) sender).giveExp(-Integer.parseInt(args[1]));
            target.giveExp(Integer.parseInt(args[1]));

            sender.sendMessage(target.getName() + "に" + args[1] + "経験値払いました。");
            target.sendMessage(sender.getName() + "から" + args[1] + "経験値もらいました。");
        }
        return true;
    }
}