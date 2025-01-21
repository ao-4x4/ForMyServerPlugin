package jp.reitou_mugicha.openServerManagement.craftwar;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class CraftwarEvents implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!Craftwar.isPlaying) return;
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        if (Craftwar.hasObjectiveItem(player)) {
            Craftwar.gameFinish(player);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!Craftwar.isPlaying) return;
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = (Player) event.getPlayer();
        if (Craftwar.hasObjectiveItem(player)) {
            Craftwar.gameFinish(player);
        }
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!Craftwar.isPlaying) return;
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (Craftwar.hasObjectiveItem(player)) {
            Craftwar.gameFinish(player);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        if (Craftwar.isPlaying)
        {
            Craftwar.joinBossbar(event.getPlayer());
        }
    }
}