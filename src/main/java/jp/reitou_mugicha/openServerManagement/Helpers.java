package jp.reitou_mugicha.openServerManagement;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.geysermc.floodgate.api.FloodgateApi;

public class Helpers
{
    public static boolean isBedrockPlayer(Player player)
    {
        return FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId());
    }

    public static boolean isTool(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        Material material = item.getType();

        return material == Material.NETHERITE_PICKAXE || material == Material.DIAMOND_PICKAXE || material == Material.IRON_PICKAXE || material == Material.GOLDEN_PICKAXE ||
                material == Material.STONE_PICKAXE || material == Material.WOODEN_PICKAXE ||
                material == Material.NETHERITE_SHOVEL || material == Material.DIAMOND_SHOVEL || material == Material.IRON_SHOVEL || material == Material.GOLDEN_SHOVEL ||
                material == Material.STONE_SHOVEL || material == Material.WOODEN_SHOVEL ||
                material == Material.NETHERITE_AXE || material == Material.DIAMOND_AXE || material == Material.IRON_AXE || material == Material.GOLDEN_AXE ||
                material == Material.STONE_AXE || material == Material.WOODEN_AXE ||
                material == Material.NETHERITE_SWORD || material == Material.DIAMOND_SWORD || material == Material.IRON_SWORD || material == Material.GOLDEN_SWORD ||
                material == Material.STONE_SWORD || material == Material.WOODEN_SWORD;
    }


    public static boolean isArmor(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        Material material = item.getType();

        return material == Material.NETHERITE_HELMET || material == Material.DIAMOND_HELMET || material == Material.IRON_HELMET ||
                material == Material.GOLDEN_HELMET || material == Material.CHAINMAIL_HELMET || material == Material.LEATHER_HELMET ||
                material == Material.NETHERITE_CHESTPLATE || material == Material.DIAMOND_CHESTPLATE || material == Material.IRON_CHESTPLATE ||
                material == Material.GOLDEN_CHESTPLATE || material == Material.CHAINMAIL_CHESTPLATE || material == Material.LEATHER_CHESTPLATE ||
                material == Material.NETHERITE_LEGGINGS || material == Material.DIAMOND_LEGGINGS || material == Material.IRON_LEGGINGS ||
                material == Material.GOLDEN_LEGGINGS || material == Material.CHAINMAIL_LEGGINGS || material == Material.LEATHER_LEGGINGS ||
                material == Material.NETHERITE_BOOTS || material == Material.DIAMOND_BOOTS || material == Material.IRON_BOOTS ||
                material == Material.GOLDEN_BOOTS || material == Material.CHAINMAIL_BOOTS || material == Material.LEATHER_BOOTS ||
                material == Material.ELYTRA || material == Material.PLAYER_HEAD;
    }

    public static boolean isShulkerBox(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        Material material = item.getType();

        return material == Material.SHULKER_BOX || material == Material.WHITE_SHULKER_BOX || material == Material.ORANGE_SHULKER_BOX ||
                material == Material.MAGENTA_SHULKER_BOX || material == Material.LIGHT_BLUE_SHULKER_BOX || material == Material.YELLOW_SHULKER_BOX ||
                material == Material.LIME_SHULKER_BOX || material == Material.PINK_SHULKER_BOX || material == Material.GRAY_SHULKER_BOX || material == Material.LIGHT_GRAY_SHULKER_BOX ||
                material == Material.CYAN_SHULKER_BOX || material == Material.PURPLE_SHULKER_BOX || material == Material.BLUE_SHULKER_BOX || material == Material.BROWN_SHULKER_BOX ||
                material == Material.GREEN_SHULKER_BOX || material == Material.RED_SHULKER_BOX || material == Material.BLACK_SHULKER_BOX;
    }
}