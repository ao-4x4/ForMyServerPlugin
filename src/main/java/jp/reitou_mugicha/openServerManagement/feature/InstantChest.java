package jp.reitou_mugicha.openServerManagement.feature;

import jp.reitou_mugicha.openServerManagement.Helpers;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InstantChest implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (player.isSneaking() && event.getAction() == Action.RIGHT_CLICK_BLOCK && Helpers.isBedrockPlayer(player)) {
            if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.CHEST) {
                Inventory chestInventory = ((org.bukkit.block.Chest) event.getClickedBlock().getState()).getInventory();
                ItemStack itemInHand = player.getInventory().getItemInMainHand();

                if (itemInHand.getType() != Material.AIR) {
                    if (getEmptyInventorySlots(chestInventory) == 0) {
                        event.setCancelled(true);
                        return;
                    }
                    chestInventory.addItem(itemInHand);
                    player.getInventory().setItemInMainHand(null);
                    player.sendMessage("アイテムをチェストに移動しました！");
                    event.setCancelled(true);
                }
            }
        }
    }

    public int getEmptyInventorySlots(Inventory inventory) {
        int emptySlots = 0;
        for (int i = 0; i <= inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null) {
                emptySlots++;
            }
        }
        return emptySlots;
    }
}
