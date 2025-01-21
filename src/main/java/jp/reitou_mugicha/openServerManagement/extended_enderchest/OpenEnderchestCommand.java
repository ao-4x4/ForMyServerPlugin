package jp.reitou_mugicha.openServerManagement.extended_enderchest;

import jp.reitou_mugicha.openServerManagement.OpenServerManagement;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class OpenEnderchestCommand implements CommandExecutor
{
    private final EnderchestDataManager dataManager;

    public OpenEnderchestCommand(EnderchestDataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;

        if (player.hasPermission("openservermanagement.openenderchest"))
        {
            Player target = Bukkit.getPlayer(args[0]);
            OfflinePlayer targetOffline = Bukkit.getOfflinePlayer(args[0]);

            if ((targetOffline == null && target != null) || (targetOffline != null && target == null))
            {
                return true;
            }

            ExtendedEnderchest ee = new ExtendedEnderchest(dataManager);
            ee.openEnderchest(player, 1, target == null ? targetOffline.getUniqueId() : target.getUniqueId());
            return true;
        }

        return false;
    }
}
