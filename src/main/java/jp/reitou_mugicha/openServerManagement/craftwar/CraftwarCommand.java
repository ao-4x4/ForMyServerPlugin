package jp.reitou_mugicha.openServerManagement.craftwar;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class CraftwarCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        switch (args[0])
        {
            case "start":
                int time = -1;
                if (args.length > 1 && !args[1].trim().isEmpty()) {
                    try {
                        time = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.RED + "時間は整数秒で指定してください！");
                        return true;
                    }
                }

                Craftwar.startCraftWar(time);
                break;
            case "stop":
                Craftwar.forceStop();
                break;
            case "reroll":
                Craftwar.rerollItem();
                break;
            case "setobjective":
                String materialName = args[1].toUpperCase();

                Material material;
                try {
                    material = Material.valueOf(materialName);
                } catch (IllegalArgumentException e) {
                    return false;
                }

                if (!material.isItem()) return false;

                Craftwar.setObjective(new ItemStack(material));
                break;
        }

        return true;
    }
}

