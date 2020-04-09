package fr.hegsis.spawnerpickaxe;

import fr.hegsis.spawnerpickaxe.commands.SpawnerCommand;
import fr.hegsis.spawnerpickaxe.commands.SpawnerPickaxeCommand;
import fr.hegsis.spawnerpickaxe.listeners.*;
import fr.hegsis.spawnerpickaxe.manager.ManagerMain;
import fr.hegsis.spawnerpickaxe.manager.Option;
import fr.hegsis.spawnerpickaxe.utils.Entities;
import fr.hegsis.spawnerpickaxe.utils.Inventories;
import fr.hegsis.spawnerpickaxe.utils.Utils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main extends JavaPlugin {

    public static Economy economy = null;

    // GENERAL
    public Map<Option, Boolean> optionsUsed = new HashMap<>(); // Map contenant les options du plugin
    private Material spawnerItem;
    private Material playerHeadItem;
    private Material signItem;
    private SpawnerPickaxe spawnerPickaxe;

    // ENTITEES
    public List<EntityType> entityList = new ArrayList<>(); // Liste des entités vivantes
    public String entityListString; // Chaîne de caractère qui contient la liste des entités vivantes

    // INVENTAIRES
    public Inventory manageInventory; // Inventaire du /spawnerpickaxe manage ou /spawnerpickaxe gui
    public Inventory spawnerInventory; // Inventaire du /spawner ou /spawner list
    public Inventory spawnerInventoryNext; // Inventaire du /spawner ou /spawner list
    public Inventory shopInventory; // Inventaire du /spawnerpickaxe shop

    @Override
    public void onEnable() {

        if(!setupEconomy()) {
            getLogger().severe("§4Economy plugin needed !");
        }

        saveDefaultConfig();
        setItems();
        ManagerMain.getOptions(this);
        setAllDefaultInventoriesAndEntities();

        registerEvents();
        getCommand("spawner").setExecutor(new SpawnerCommand(this));
        getCommand("spawnerpickaxe").setExecutor(new SpawnerPickaxeCommand(this));

        this.getServer().getConsoleSender().sendMessage("§7SpawnerPickaxe §5→ §aON §f§l(By HegSiS)");
    }

    // On check si il y a un plugin d'économie
    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = (Economy) economyProvider.getProvider();
        }
        return economy != null;
    }

    // On register les events
    private void registerEvents() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new InventoriesListeners(this), this);
        pm.registerEvents(new SignListeners(this), this);
        pm.registerEvents(new SpawnerBlastListeners(this), this);
        pm.registerEvents(new SpawnerBreakListeners(this), this);
        pm.registerEvents(new SpawnerPlaceListeners(this), this);
    }

    private void setItems() {
        String material = getConfig().getString("spawner-item");
        spawnerItem = Material.getMaterial(material);
        Utils.isMaterial(material, spawnerItem, this);

        material = getConfig().getString("player-head-item");
        playerHeadItem = Material.getMaterial(material);
        Utils.isMaterial(material, playerHeadItem, this);

        material = getConfig().getString("sign-item");
        signItem = Material.getMaterial(material);
        Utils.isMaterial(material, signItem, this);

        material = getConfig().getString("pickaxe.item-type");
        Material mat = Material.getMaterial(material);
        Utils.isMaterial(material, mat, this);
        spawnerPickaxe = new SpawnerPickaxe(new ItemStack(mat));
        spawnerPickaxe.setDisplayName(getConfig().getString("pickaxe.name").replaceAll("&", "§"));
        spawnerPickaxe.setLore(Utils.convertListColorCode(getConfig().getStringList("pickaxe.description")));
    }

    private void setAllDefaultInventoriesAndEntities() {
        entityList = Entities.setEntityList(this);
        entityListString = Entities.setEntityListString(entityList);
        manageInventory = ManagerMain.setManageInventory(this);
        Inventories.setEntityInventoryList(this);
        Inventories.setShopInventory(this);
    }

    public Material getSpawnerItem() {
        return spawnerItem;
    }

    public Material getPlayerHeadItem() {
        return playerHeadItem;
    }

    public Material getSignItem() {
        return playerHeadItem;
    }

    public ItemStack getPickaxe() {
        return spawnerPickaxe.getPickaxe();
    }
}
