package jp.reitou_mugicha.openServerManagement;

import jp.reitou_mugicha.openServerManagement.command.CommandFakeChat;
import jp.reitou_mugicha.openServerManagement.command.CommandPayExperience;
import jp.reitou_mugicha.openServerManagement.command.CommandSleep;
import jp.reitou_mugicha.openServerManagement.craftwar.CraftwarCommand;
import jp.reitou_mugicha.openServerManagement.craftwar.CraftwarCommandTabComplete;
import jp.reitou_mugicha.openServerManagement.craftwar.CraftwarEvents;
import jp.reitou_mugicha.openServerManagement.extended_enderchest.EnderchestDataManager;
import jp.reitou_mugicha.openServerManagement.extended_enderchest.EnderchestUpgrade;
import jp.reitou_mugicha.openServerManagement.extended_enderchest.ExtendedEnderchest;
import jp.reitou_mugicha.openServerManagement.extended_enderchest.OpenEnderchestCommand;
import jp.reitou_mugicha.openServerManagement.feature.*;
import jp.reitou_mugicha.openServerManagement.fix.FixAnvilSweepingEdge;
import org.mineacademy.fo.plugin.SimplePlugin;

public final class OpenServerManagement extends SimplePlugin
{
    public static EnderchestDataManager enderchestDataManager;
    @Override
    public void onPluginStart()
    {
        enderchestDataManager = new EnderchestDataManager(getDataFolder());

        // REGISTER GENERAL COMMANDS
        this.getCommand("fakechat").setExecutor(new CommandFakeChat());
        this.getCommand("payexperience").setExecutor(new CommandPayExperience());
        this.getCommand("sleep").setExecutor(new CommandSleep());
        this.getCommand("opencustomenderchest").setExecutor(new OpenEnderchestCommand(enderchestDataManager));

        // REGISTER CRAFTWAR FUNCTIONS
        this.getCommand("craftwar").setExecutor(new CraftwarCommand());
        this.getCommand("craftwar").setTabCompleter(new CraftwarCommandTabComplete());
        getServer().getPluginManager().registerEvents(new CraftwarEvents(), this);

        // REGISTER EVENTS
        getServer().getPluginManager().registerEvents(new FixAnvilSweepingEdge(), this);
        getServer().getPluginManager().registerEvents(new InstantChest(), this);
        getServer().getPluginManager().registerEvents(new BulkTrading(), this);
        getServer().getPluginManager().registerEvents(new ExperienceTrading(), this);
        getServer().getPluginManager().registerEvents(new UnlimitedAnvil(), this);
        getServer().getPluginManager().registerEvents(new ExtendedEnderchest(enderchestDataManager), this);
        getServer().getPluginManager().registerEvents(new EnderchestUpgrade(enderchestDataManager), this);
        getLogger().info("OpenServer Management Plugin is Starting");
    }

    @Override
    public void onPluginStop()
    {
        getLogger().info("OpenServer Management Plugin disabled.");
    }
}
