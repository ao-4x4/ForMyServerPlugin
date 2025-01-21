package jp.reitou_mugicha.openServerManagement.extended_enderchest;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ExtendedEnderchest implements Listener {
    private final EnderchestDataManager dataManager;

    public ExtendedEnderchest(EnderchestDataManager dataManager) {
        this.dataManager = dataManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        FileConfiguration data = dataManager.getPlayerData(player.getUniqueId());

        if (!data.contains("row")) {
            data.set("row", 3);
        }
        if (!data.contains("page")) {
            data.set("page", 1);
        }
        if (!data.contains("currentPage")) {
            data.set("currentPage", 1);
        }

        dataManager.savePlayerData(player.getUniqueId(), data);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (/*!event.getPlayer().isOp() || */!event.getPlayer().isSneaking()) return;
        if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.ENDER_CHEST) {
            event.setCancelled(true);
            openEnderchest(event.getPlayer(), 1, event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().contains("エンダーチェスト")) {
            Player player = (Player) event.getWhoClicked();
            FileConfiguration data = dataManager.getPlayerData(player.getUniqueId());
            int currentPage = data.getInt("currentPage");
            int totalPages = data.getInt("page");

            if (event.getRawSlot() >= event.getInventory().getSize() - 9) {
                switch (event.getCurrentItem().getType()) {
                    case LIME_CONCRETE -> {
                        if (currentPage < totalPages) {
                            saveContent(player, event.getInventory(), currentPage);
                            openEnderchest(player, currentPage + 1, player.getUniqueId());
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                            event.setCancelled(true);
                        }

                        event.setCancelled(true);
                    }
                    case RED_CONCRETE -> {
                        if (currentPage > 1) {
                            saveContent(player, event.getInventory(), currentPage);
                            openEnderchest(player, currentPage - 1, player.getUniqueId());
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                        }

                        event.setCancelled(true);
                    }
                    case BARRIER -> {
                        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().contains("エンダーチェスト")) {
            Player player = (Player) event.getPlayer();
            FileConfiguration data = dataManager.getPlayerData(player.getUniqueId());
            int currentPage = data.getInt("currentPage");
            saveContent(player, event.getInventory(), currentPage);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        FileConfiguration data = dataManager.getPlayerData(player.getUniqueId());
        dataManager.savePlayerData(player.getUniqueId(), data);
    }

    public void saveContent(Player player, Inventory inventory, int page) {
        FileConfiguration data = dataManager.getPlayerData(player.getUniqueId());
        HashMap<Integer, Map<String, Object>> serializedContents = new HashMap<>();
        for (int i = 0; i < inventory.getSize() - 9; i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null) {
                serializedContents.put(i, item.serialize());
            }
        }
        data.set("contents.page" + page, serializedContents);
        dataManager.savePlayerData(player.getUniqueId(), data);
    }

    public HashMap<Integer, ItemStack> loadContent(Player player, int page) {
        FileConfiguration data = dataManager.getPlayerData(player.getUniqueId());
        HashMap<Integer, ItemStack> contents = new HashMap<>();

        if (data.contains("contents.page" + page)) {
            ConfigurationSection contentsSection = data.getConfigurationSection("contents.page" + page);
            if (contentsSection != null) {
                for (String key : contentsSection.getKeys(false)) {
                    try {
                        int slot = Integer.parseInt(key);
                        Map<String, Object> itemData = contentsSection.getConfigurationSection(key).getValues(false);
                        contents.put(slot, ItemStack.deserialize(itemData));
                    } catch (NumberFormatException e) {
                        Bukkit.getLogger().warning("Invalid slot key in contents: " + key);
                    }
                }
            }
        }
        return contents;
    }

    public void openEnderchest(Player player, int page, UUID target) {
        FileConfiguration data = dataManager.getPlayerData(target);
        int row = data.getInt("row");
        int totalPages = data.getInt("page");
        int size = (row + 1) * 9;
        Inventory gui = Bukkit.createInventory(null, size, "エンダーチェスト " + page + "/" + totalPages);

        ItemStack next = new ItemStack(Material.LIME_CONCRETE);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.displayName(Component.text("次ページ | Next Page"));
        next.setItemMeta(nextMeta);

        ItemStack previous = new ItemStack(Material.RED_CONCRETE);
        ItemMeta previousMeta = previous.getItemMeta();
        previousMeta.displayName(Component.text("前ページ | Previous Page"));
        previous.setItemMeta(previousMeta);

        ItemStack barrier = new ItemStack(Material.BARRIER);
        ItemMeta barrierMeta = barrier.getItemMeta();
        barrierMeta.displayName(Component.text("無効 | Disabled"));
        barrier.setItemMeta(barrierMeta);

        for (int i = size - 9; i < size; i++) {
            gui.setItem(i, barrier);
        }
        gui.setItem(size - 4, next);
        gui.setItem(size - 6, previous);

        HashMap<Integer, ItemStack> contents = loadContent(player, page);
        for (int i = 0; i < gui.getSize() - 9; i++) {
            gui.setItem(i, contents.getOrDefault(i, null));
        }

        /*if (!data.getBoolean("initialized"))
        {
            for (int i = 0; i < player.getEnderChest().getSize(); i++) {
                ItemStack item = player.getEnderChest().getItem(i);
                if (item != null) {
                    gui.setItem(i, item);
                }
            }

            data.set("initialized", true);
            dataManager.savePlayerData(player.getUniqueId(), data);
        }*/

        data.set("currentPage", page);
        dataManager.savePlayerData(player.getUniqueId(), data);
        player.openInventory(gui);
    }
}