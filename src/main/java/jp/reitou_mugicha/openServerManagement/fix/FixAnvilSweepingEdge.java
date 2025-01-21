package jp.reitou_mugicha.openServerManagement.fix;

import jp.reitou_mugicha.openServerManagement.Helpers;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class FixAnvilSweepingEdge implements Listener
{
    @EventHandler
    public void onAnvilPrepare(PrepareAnvilEvent event)
    {
        AnvilInventory inventory = event.getInventory();
        Player player = (Player) event.getView().getPlayer();

        if (!Helpers.isBedrockPlayer(player)) return;

        ItemStack firstItem = inventory.getItem(0);
        ItemStack secondItem = inventory.getItem(1);

        if (firstItem != null && secondItem != null)
        {
            if (secondItem.getType() == Material.ENCHANTED_BOOK)
            {
                EnchantmentStorageMeta meta = (EnchantmentStorageMeta) secondItem.getItemMeta();
                if (meta.hasStoredEnchant(Enchantment.SWEEPING_EDGE) && meta.getStoredEnchants().size() == 1)
                {
                    List<String> lores = new ArrayList<String>();
                    lores.add("耐久力Iは一時的なものです。\nエンチャント適用後削除されます。");

                    meta.addItemFlags(ItemFlag.HIDE_STORED_ENCHANTS);
                    meta.addStoredEnchant(Enchantment.UNBREAKING, 1, false);
                    meta.setLore(lores);
                    secondItem.setItemMeta(meta);
                }
            }
        }
    }

    @EventHandler
    public void removeEnchant(InventoryClickEvent event)
    {
        Player player = (Player) event.getWhoClicked();

        if (!Helpers.isBedrockPlayer(player)) return;
        if (event.getClickedInventory() == null) return;

        ItemStack item = event.getCurrentItem();

        if (item == null) return;

        if (item.getType() == Material.ENCHANTED_BOOK)
        {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
            if (meta.hasStoredEnchant(Enchantment.SWEEPING_EDGE) && meta.hasStoredEnchant(Enchantment.UNBREAKING) && meta.hasItemFlag(ItemFlag.HIDE_STORED_ENCHANTS))
            {
                meta.removeStoredEnchant(Enchantment.BANE_OF_ARTHROPODS);
                meta.removeItemFlags(ItemFlag.HIDE_STORED_ENCHANTS);
                meta.setLore(null);
                item.setItemMeta(meta);
                event.setCurrentItem(item);
            }
        }

        ItemMeta enchantedMeta = item.getItemMeta();
        if (enchantedMeta == null) return;
        if (enchantedMeta.hasEnchant(Enchantment.SWEEPING_EDGE) && enchantedMeta.hasEnchant(Enchantment.UNBREAKING))
        {
            if (enchantedMeta.getEnchantLevel(Enchantment.UNBREAKING) == 1) {
                enchantedMeta.removeEnchant(Enchantment.UNBREAKING);
                item.setItemMeta(enchantedMeta);
                event.setCurrentItem(item);
            }
        }
    }
}