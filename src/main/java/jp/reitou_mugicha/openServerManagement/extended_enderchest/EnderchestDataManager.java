package jp.reitou_mugicha.openServerManagement.extended_enderchest;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class EnderchestDataManager
{
    private final File dataFolder;

    public EnderchestDataManager(File dataFolder) {
        this.dataFolder = dataFolder;
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }
    }

    public FileConfiguration getPlayerData(UUID uuid) {
        File playerFile = new File(dataFolder, uuid + ".yml");
        if (!playerFile.exists()) {
            try {

                playerFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return YamlConfiguration.loadConfiguration(playerFile);
    }

    public void savePlayerData(UUID uuid, FileConfiguration data) {
        File playerFile = new File(dataFolder, uuid + ".yml");
        try {
            data.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deletePlayerData(UUID uuid) {
        File playerFile = new File(dataFolder, uuid + ".yml");
        if (playerFile.exists()) {
            playerFile.delete();
        }
    }
}