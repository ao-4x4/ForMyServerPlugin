package jp.reitou_mugicha.openServerManagement.feature;

import jp.reitou_mugicha.openServerManagement.Helpers;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class UnlimitedAnvil implements Listener {
    private final String GUIName = "改造金床";
    private final int cost = 30;
    private final int slot1Index = 11;
    private final int slot2Index = 13;
    private final int resultIndex = 15;

    private final Enchantment[] allowedEnchantments = {
            Enchantment.UNBREAKING,
            Enchantment.POWER,
            Enchantment.SHARPNESS,
            Enchantment.FORTUNE,
            Enchantment.EFFICIENCY,
            Enchantment.LOOTING,
            Enchantment.PROTECTION,
            Enchantment.FIRE_PROTECTION,
            Enchantment.BLAST_PROTECTION,
            Enchantment.PROJECTILE_PROTECTION,
            Enchantment.THORNS
    };

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.ANVIL) {
            openCustomGUI(player);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(GUIName)) {
            Player player = (Player) event.getWhoClicked();
            ItemStack item = event.getCurrentItem();
            if (item == null || item.getType() == Material.AIR) {
                return;
            }

            Inventory inventory = event.getInventory();
            ItemStack slot1 = event.getInventory().getItem(slot1Index);
            ItemStack slot2 = event.getInventory().getItem(slot2Index);

            switch (event.getSlot())
            {
                case slot1Index:
                case slot2Index:
                    if (slot1 != null && slot2 != null) {
                        if (slot1.getType() == Material.ENCHANTED_BOOK && slot2.getType() == Material.NETHERITE_INGOT)
                        {
                            if (hasAllowedEnchantment(slot1)) {
                                if (checkHasEnoughExp(player, inventory))
                                {
                                    ItemStack result = slot1.clone();
                                    EnchantmentStorageMeta resultMeta = (EnchantmentStorageMeta) result.getItemMeta();
                                    resultMeta.addStoredEnchant(getAllowedEnchantment(slot1), getAllowedEnchantmentLevel(slot1) + 1, true);
                                    result.setItemMeta(resultMeta);

                                    inventory.setItem(resultIndex, result);
                                }
                            }
                        } else if ((Helpers.isTool(slot1) || Helpers.isArmor(slot1)) && slot2.getType() == Material.ENCHANTED_BOOK)
                        {
                            if (hasAllowedEnchantment(slot2)) {
                                if (checkHasEnoughExp(player, inventory))
                                {
                                    if (slot1.getEnchantments().keySet().stream().anyMatch(enchantment -> Arrays.asList(allowedEnchantments).contains(enchantment)) &&
                                            slot1.getEnchantments().keySet().stream().anyMatch(enchantment -> Arrays.asList(allowedEnchantments).contains(enchantment) &&
                                                    slot1.getEnchantmentLevel(getAllowedEnchantment(slot2)) >= getAllowedEnchantmentLevel(slot2)))
                                    {
                                        ItemStack resultItem = new ItemStack(Material.RED_CONCRETE);
                                        ItemMeta resultMeta = resultItem.getItemMeta();
                                        resultMeta.setDisplayName("エンチャントがかぶっているか、レベルが低いです。\nEnchantment is overlapping or level is low.");
                                        resultItem.setItemMeta(resultMeta);
                                        inventory.setItem(resultIndex, resultItem);
                                        return;
                                    }

                                    ItemStack result = slot1.clone();
                                    ItemMeta resultMeta = result.getItemMeta();
                                    resultMeta.addEnchant(getAllowedEnchantment(slot2), getAllowedEnchantmentLevel(slot2), true);
                                    result.setItemMeta(resultMeta);

                                    inventory.setItem(resultIndex, result);
                                }
                            }
                        }
                    }
                    break;
                case resultIndex:
                    if (item.getType() != Material.BARRIER && item.getType() != Material.RED_CONCRETE)
                    {
                        ItemStack resultItem = new ItemStack(Material.BARRIER);
                        ItemMeta resultMeta = resultItem.getItemMeta();
                        resultMeta.setDisplayName("アイテムをセットしてください | Set an item");
                        resultItem.setItemMeta(resultMeta);

                        player.getInventory().addItem(item);
                        player.giveExpLevels(-cost);
                        slot1.setAmount(slot1.getAmount() - 1);
                        slot2.setAmount(slot2.getAmount() - 1);
                        inventory.setItem(resultIndex, resultItem);
                    }
                    break;
                default:
                    if (event.getRawSlot() < 27)
                    {
                        event.setCancelled(true);
                    }
                    break;
            }


        }
    }

    public boolean checkHasEnoughExp(Player player, Inventory inventory) {
        if (player.getExpToLevel() < cost) {
            ItemStack resultItem = new ItemStack(Material.RED_CONCRETE);
            ItemMeta resultMeta = resultItem.getItemMeta();
            resultMeta.setDisplayName(String.format("経験値が足りません。| Not enough experience.\nコスト: %d | Cost: %d", cost, cost));
            resultItem.setItemMeta(resultMeta);
            inventory.setItem(resultIndex, resultItem);
        }
        return player.getExpToLevel() >= cost;
    }

    public void openCustomGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, GUIName);

        ItemStack blank = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta blankMeta = blank.getItemMeta();
        blankMeta.setDisplayName("無効 | Invalid");
        blank.setItemMeta(blankMeta);
        for (int i = 0; i < 27; i++) {
            gui.setItem(i, blank);
        }

        ItemStack resultItem = new ItemStack(Material.BARRIER);
        ItemMeta resultMeta = resultItem.getItemMeta();
        resultMeta.setDisplayName("アイテムをセットしてください | Set an item");
        resultItem.setItemMeta(resultMeta);

        gui.setItem(slot1Index, null);
        gui.setItem(slot2Index, null);
        gui.setItem(resultIndex, resultItem);

        player.openInventory(gui);
    }

    public boolean hasAllowedEnchantment(ItemStack item) {
        EnchantmentStorageMeta storedMeta = (EnchantmentStorageMeta) item.getItemMeta();

        if (storedMeta != null) {
            for (Enchantment enchantment : storedMeta.getStoredEnchants().keySet()) {
                if (Arrays.asList(allowedEnchantments).contains(enchantment)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Enchantment getAllowedEnchantment(ItemStack item) {
        EnchantmentStorageMeta storedMeta = (EnchantmentStorageMeta) item.getItemMeta();

        if (storedMeta != null) {
            for (Enchantment enchantment : storedMeta.getStoredEnchants().keySet()) {
                if (Arrays.asList(allowedEnchantments).contains(enchantment)) {
                    return enchantment;
                }
            }
        }
        return null;
    }

    public int getAllowedEnchantmentLevel(ItemStack item) {
        EnchantmentStorageMeta storedMeta = (EnchantmentStorageMeta) item.getItemMeta();

        if (storedMeta != null) {
            for (Enchantment enchantment : storedMeta.getStoredEnchants().keySet()) {
                if (Arrays.asList(allowedEnchantments).contains(enchantment)) {
                    return storedMeta.getStoredEnchantLevel(enchantment);
                }
            }
        }
        return 0;
    }

    @EventHandler
    public void onInventoryClose(org.bukkit.event.inventory.InventoryCloseEvent event) {
        if (event.getView().getTitle().equals("Upgrade")) {
            Player player = (Player) event.getPlayer();

            Inventory inventory = event.getInventory();

            ItemStack slot1 = inventory.getItem(slot1Index);
            ItemStack slot2 = inventory.getItem(slot2Index);
            ItemStack result = inventory.getItem(resultIndex);

            if (slot1 != null) {
                player.getInventory().addItem(slot1);
            }

            if (slot2 != null) {
                player.getInventory().addItem(slot2);
            }

            if (result != null && result.getType() != Material.BARRIER)
            {
                player.getInventory().addItem(result);
            }
        }
    }
}