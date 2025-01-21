package jp.reitou_mugicha.openServerManagement.craftwar;

import jp.reitou_mugicha.openServerManagement.OpenServerManagement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.Random;

public class Craftwar
{
    public static boolean isPlaying = false;
    public static ItemStack selectedItem = null;
    public static String selectedItemString = "";
    public static BossBar objective = null;

    public static Material[] blacklist = {
            Material.BEDROCK,
            Material.BARRIER,
            Material.COMMAND_BLOCK,
            Material.CHAIN_COMMAND_BLOCK,
            Material.REPEATING_COMMAND_BLOCK,
            Material.PETRIFIED_OAK_SLAB,
            Material.LIGHT,
            Material.FIRE,
            Material.DEBUG_STICK,
            Material.SPAWNER,
    };

    private static void sleep(Runnable task, int delay)
    {
        new BukkitRunnable() {
            @Override
            public void run() {
                task.run();
            }
        }.runTaskLater(OpenServerManagement.getPlugin(OpenServerManagement.class), delay);
    }

    private static void makeTimer(int second)
    {
        if (second == -1) return;

        final int totalTicks = second * 20;
        final int[] remainingTicks = {totalTicks};

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!isPlaying) {
                    this.cancel();
                    return;
                }

                remainingTicks[0]--;

                double progress = (double) remainingTicks[0] / totalTicks;
                objective.setProgress(progress);

                if (remainingTicks[0] <= 0) {
                    timeOut();
                    this.cancel();
                }
            }
        }.runTaskTimer(OpenServerManagement.getPlugin(OpenServerManagement.class), 0, 1);
    }

    public static void init()
    {
        objective.removeAll();
        objective = null;

        selectedItem = null;
        selectedItemString = "";
    }

    public static void startCraftWar(int second)
    {
        if (isPlaying) return;
        if (second < -1 || second == 0) return;

        isPlaying = true;

        selectedItem = selectRandomItem();
        selectedItemString = PlainTextComponentSerializer.plainText().serialize(selectedItem.displayName());
        objective = Bukkit.createBossBar(ChatColor.GREEN + "[???]をゲットしろ！", BarColor.GREEN, BarStyle.SEGMENTED_10);

        for (Player player : Bukkit.getOnlinePlayers())
        {
            objective.addPlayer(player);

            player.showTitle(Title.title(Component.text("CRAFTWAR START!!!").color(TextColor.color(105, 250, 100)), Component.text("")));
            sleep(() -> {
                player.showTitle(Title.title(Component.text("お題は...").color(TextColor.color(105, 250, 100)), Component.text("")));
            }, 60);

            sleep(() -> {
                makeTimer(second);
                player.showTitle(Title.title(selectedItem.displayName().color(TextColor.color(255,0,0)), Component.text("をゲットしろ！")));
                objective.setTitle(ChatColor.GREEN + selectedItemString + "をゲットしろ！");
            }, 120);
        }
    }

    public static void gameFinish(Player winner)
    {
        if (!isPlaying) return;
        isPlaying = false;

        for (Player player : Bukkit.getOnlinePlayers())
        {
            objective.setTitle("GAME FINISHED!");
            objective.setColor(BarColor.RED);

            player.showTitle(Title.title(Component.text("CRAFTWAR FINISH!!!").color(TextColor.color(105, 250, 100)), Component.text("")));
            sleep(() -> player.showTitle(Title.title(Component.text("勝者は...").color(TextColor.color(105, 250, 100)), Component.text(""))), 60);
            sleep(() -> {
                player.showTitle(Title.title(winner.displayName().color(TextColor.color(105, 250, 100)), Component.text("")));
                objective.setTitle(ChatColor.GREEN + PlainTextComponentSerializer.plainText().serialize(winner.displayName()) + ChatColor.WHITE + "が勝利しました!");
                objective.setColor(BarColor.YELLOW);

                sleep(Craftwar::init, 100);
            }, 160);
        }
    }

    public static void timeOut()
    {
        if (!isPlaying) return;

        isPlaying = false;

        for (Player player : Bukkit.getOnlinePlayers())
        {
            player.showTitle(Title.title(Component.text("CRAFTWAR FINISH!!!").color(TextColor.color(105, 250, 100)), Component.text("時間切れ!")));
            objective.setTitle("時間切れ!");
            objective.setColor(BarColor.RED);

            sleep(Craftwar::init, 100);
        }
    }

    public static void forceStop()
    {
        if (!isPlaying) return;

        isPlaying = false;

        for (Player player : Bukkit.getOnlinePlayers())
        {
            player.showTitle(Title.title(Component.text("CRAFTWAR FINISH!!!").color(TextColor.color(105, 250, 100)), Component.text("コマンドにより強制停止されました。")));
            init();
        }
    }

    private static void update()
    {
        selectedItemString = PlainTextComponentSerializer.plainText().serialize(selectedItem.displayName());
        objective.setTitle(ChatColor.GREEN + selectedItemString + "をゲットしろ！");

        for (Player player : Bukkit.getOnlinePlayers())
        {
            player.showTitle(Title.title(selectedItem.displayName().color(TextColor.color(105, 250, 100)), Component.text("目標アイテムが変更されました。")));
        }
    }

    public static void rerollItem()
    {
        if (!isPlaying) return;
        selectedItem = selectRandomItem();
        update();
    }

    public static ItemStack selectRandomItem()
    {
        Random rand = new Random();
        Material[] allItems = Arrays.stream(Material.values()).filter(Material::isItem).toArray(Material[]::new);
        Material selectedItem = allItems[rand.nextInt(allItems.length)];

        if (Arrays.stream(blacklist).toList().contains(selectedItem)) return selectRandomItem(); // Block blacklist items.
        if (String.valueOf(selectedItem).contains("SPAWN_EGG")) return selectRandomItem(); // Block spawner egg items.

        return new ItemStack(selectedItem);
    }

    public static void setObjective(ItemStack item)
    {
        selectedItem = item;
        update();
    }

    public static void joinBossbar(Player player)
    {
        objective.addPlayer(player);
    }

    public static boolean hasAnyMusicDisc(Player player)
    {
        for (ItemStack item : player.getInventory().getContents())
        {
            if (item != null && item.getType().isRecord() && selectedItem.getType().isRecord())
            {
                return true;
            }
        }

        return false;
    }

    public static boolean hasObjectiveItem(Player player)
    {
        return player.getInventory().contains(selectedItem.getType()) || hasAnyMusicDisc(player);
    }
}