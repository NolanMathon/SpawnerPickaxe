package fr.hegsis.spawnerpickaxe;

import fr.hegsis.spawnerpickaxe.listeners.*;
import fr.hegsis.spawnerpickaxe.manager.ManagerMain;
import fr.hegsis.spawnerpickaxe.manager.Option;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class Main extends JavaPlugin {

    public static Economy economy = null;

    public Map<Option, Boolean> optionsUsed = new HashMap<>(); //Map contenant les options du plugin
    private Material spawnerItem;
    private SpawnerPickaxe spawnerPickaxe;

    @Override
    public void onEnable() {

        saveDefaultConfig();
        setSpawnerAndPickaxeItem();
        ManagerMain.setOptions(this);

        if(!setupEconomy()) {
            getLogger().severe("§4Economy plugin needed !");
        }

        this.getServer().getConsoleSender().sendMessage("§7SpawnerPickaxe §5→ §aON §f§l(By HegSiS)");
    }

    private void registerEvents() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new InventoriesListeners(this), this);
        pm.registerEvents(new SignListeners(this), this);
        pm.registerEvents(new SpawnerBlastListeners(this), this);
        pm.registerEvents(new SpawnerBreakListeners(this), this);
        pm.registerEvents(new SpawnerPlaceListeners(this), this);
    }

    private void setSpawnerAndPickaxeItem() {
        String material = getConfig().getString("spawner-item");
        try {
            this.spawnerItem = Material.getMaterial(material);
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.spawnerItem = null;
    }

    // On check si il y a un plugin d'économie
    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = (Economy) economyProvider.getProvider();
        }
        return economy != null;
    }

    public Material getSpawnerItem() {
        return spawnerItem;
    }
}
