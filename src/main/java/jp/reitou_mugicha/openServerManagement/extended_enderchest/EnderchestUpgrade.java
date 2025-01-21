package jp.reitou_mugicha.openServerManagement.extended_enderchest;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class EnderchestUpgrade implements Listener
{
    private final EnderchestDataManager dataManager;

    public EnderchestUpgrade(EnderchestDataManager dataManager) {
        this.dataManager = dataManager;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_AIR && player.getInventory().getItemInMainHand().getType() == Material.ENDER_CHEST)
        {
            openUpgradeMenu(player);
        }
    }

    public void openUpgradeMenu(Player player)
    {
        Inventory gui = Bukkit.createInventory(null, 9, "エンダーチェストのアップグレード");

        ItemStack barrier = new ItemStack(Material.BARRIER);
        ItemMeta barrierMeta = barrier.getItemMeta();
        barrierMeta.displayName(Component.text("無効 | Disabled"));
        barrier.setItemMeta(barrierMeta);

        ItemStack rowUpgrade = new ItemStack(Material.DIAMOND);
        ItemMeta rowMeta = rowUpgrade.getItemMeta();
        rowMeta.displayName(Component.text("列アップグレード").color(TextColor.color(255, 190, 0)));
        rowMeta.lore(List.of(Component.text("列を増やします。\nコスト: ダイヤモンド 3個")));
        rowUpgrade.setItemMeta(rowMeta);
        rowUpgrade.setAmount(3);

        ItemStack pageUpgrade = new ItemStack(Material.NETHERITE_INGOT);
        ItemMeta pageMeta = rowUpgrade.getItemMeta();
        pageMeta.displayName(Component.text("ページアップグレード").color(TextColor.color(65, 105, 225)));
        pageMeta.lore(List.of(Component.text("ページを増やします。\nコスト: ネザライトインゴット 1個")));
        pageUpgrade.setItemMeta(pageMeta);
        pageUpgrade.setAmount(1);

        for(int i = 0; i < gui.getSize(); i++)
        {
            gui.setItem(i, barrier);
        }
        gui.setItem(4, pageUpgrade);
        gui.setItem(6, rowUpgrade);

        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event)
    {
        Player player = (Player) event.getWhoClicked();
        FileConfiguration data = dataManager.getPlayerData(player.getUniqueId());

        if (event.getView().getTitle().contains("エンダーチェストのアップグレード")) {

            if (event.getRawSlot() == 4) {
                event.setCancelled(true);

                if (player.getInventory().contains(Material.NETHERITE_INGOT, 1)) {
                    player.getInventory().removeItem(new ItemStack(Material.NETHERITE_INGOT, 1));

                    int currentPage = data.getInt("page");
                    data.set("page", currentPage + 1);
                    player.sendMessage(Component.text("ページをアップグレードしました。"));
                    dataManager.savePlayerData(player.getUniqueId(), data);
                } else {
                    player.sendMessage(Component.text("ネザライトインゴットが不足しています"));
                }
            }

            else if (event.getRawSlot() == 6) {
                event.setCancelled(true);

                if (player.getInventory().contains(Material.DIAMOND, 3)) {
                    int currentRow = data.getInt("row");
                    if (currentRow >= 5)
                    {
                        player.sendMessage(Component.text("最大値は5です。"));
                        return;
                    }

                    player.getInventory().removeItem(new ItemStack(Material.DIAMOND, 3));
                    data.set("row", currentRow + 1);
                    player.sendMessage(Component.text("列をアップグレードしました。"));
                    dataManager.savePlayerData(player.getUniqueId(), data);
                } else if (data.getInt("row") >= 5) {
                    player.sendMessage(Component.text("最大値は5です。"));
                }else {
                    player.sendMessage(Component.text("ダイヤモンドが不足しています。"));
                }
            }
        }
    }
}