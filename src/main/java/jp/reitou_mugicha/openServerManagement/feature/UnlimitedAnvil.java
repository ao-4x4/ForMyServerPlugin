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
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();

        if (event.getView().getTitle().equals("Upgrade")) {
            if (event.getClickedInventory() == null || event.getCurrentItem() == null) {
                return;
            }

            if (event.getCurrentItem().getType() == Material.GRAY_STAINED_GLASS_PANE || event.getCurrentItem().getType() == Material.ANVIL)
            {
                event.setCancelled(true);
            }

            if (event.getSlot() == 13) {
                Inventory inventory = event.getClickedInventory();

                ItemStack slot1 = inventory.getItem(11);
                ItemStack slot2 = inventory.getItem(15);

                if (slot1 != null && slot2 != null) {
                    if (slot1.getType() == Material.ENCHANTED_BOOK && slot2.getType() == Material.NETHERITE_INGOT)
                    {
                        if (hasAllowedEnchantment(slot1)) {
                            if (getAllowedEnchantmentLevel(slot1) + 1 > 10) {
                                player.sendMessage("§c最高エンチャントレベルに到達しました。");
                                player.closeInventory();
                                return;
                            }

                            ItemStack newEnchantedBook = new ItemStack(Material.ENCHANTED_BOOK);
                            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) newEnchantedBook.getItemMeta();
                            meta.addStoredEnchant(getAllowedEnchantment(slot1), getAllowedEnchantmentLevel(slot1) + 1, true);
                            newEnchantedBook.setItemMeta(meta);

                            player.getInventory().addItem(newEnchantedBook);
                            slot1.setAmount(slot1.getAmount() - 1);
                            slot2.setAmount(slot2.getAmount() - 1);

                            player.sendMessage("§a正常にアップグレードできました。");
                            player.giveExpLevels(-10);
                            player.closeInventory();
                        }
                    }
                    else if ((Helpers.isTool(slot1) || Helpers.isArmor(slot1)) && slot2.getType() == Material.ENCHANTED_BOOK)
                    {
                        ItemStack newItem = new ItemStack(slot1.clone());
                        ItemMeta meta = newItem.getItemMeta();
                        EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) slot2.getItemMeta();

                        if (meta != null && bookMeta != null) {
                            for (Enchantment enchant : bookMeta.getStoredEnchants().keySet()) {
                                if (meta.hasEnchant(enchant)) {
                                    player.sendMessage("§c" + enchant.getKey().getKey() + " はすでに付与されています。");
                                    continue;
                                }

                                int level = bookMeta.getStoredEnchantLevel(enchant);
                                meta.addEnchant(enchant, level, true);
                            }

                            newItem.setItemMeta(meta);

                            player.getInventory().addItem(newItem);
                            slot1.setAmount(slot1.getAmount() - 1);
                            slot2.setAmount(slot2.getAmount() - 1);

                            player.sendMessage("§a正常にエンチャントが付与されました。");
                            player.giveExpLevels(-10);
                            player.closeInventory();
                        }
                    }
                }
            } else {
                event.setCancelled(false);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (player.isSneaking() && player.getInventory().getItemInMainHand().getType() == Material.ANVIL) {
                openCustomGUI(player);
                event.setCancelled(true);
            }
            else if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.ANVIL)
            {
                if (player.isSneaking())
                {
                    openCustomGUI(player);
                    event.setCancelled(true);
                }
            }
        }
    }

    public void openCustomGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "Upgrade");

        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        for (int i = 0; i < 27; i++) {
            gui.setItem(i, filler);
        }

        gui.setItem(11, null);
        gui.setItem(15, null);
        gui.setItem(13, new ItemStack(Material.ANVIL));

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

            ItemStack slot1 = inventory.getItem(11);
            ItemStack slot2 = inventory.getItem(15);

            if (slot1 != null) {
                player.getInventory().addItem(slot1);
            }

            if (slot2 != null) {
                player.getInventory().addItem(slot2);
            }
        }
    }
}