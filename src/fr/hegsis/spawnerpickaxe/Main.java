package fr.hegsis.spawnerpickaxe;

import fr.hegsis.spawnerpickaxe.commands.SpawnerCommand;
import fr.hegsis.spawnerpickaxe.commands.SpawnerPickaxeCommand;
import fr.hegsis.spawnerpickaxe.commands.SuperSpawnerPickaxeCommand;
import fr.hegsis.spawnerpickaxe.listeners.*;
import fr.hegsis.spawnerpickaxe.manager.ManagerMain;
import fr.hegsis.spawnerpickaxe.manager.Option;
import fr.hegsis.spawnerpickaxe.objects.SpawnerPickaxe;
import fr.hegsis.spawnerpickaxe.objects.SuperSpawnerPickaxe;
import fr.hegsis.spawnerpickaxe.utils.Entities;
import fr.hegsis.spawnerpickaxe.utils.Inventories;
import fr.hegsis.spawnerpickaxe.utils.file.yaml.YamlFileUtils;
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
    private static Main instance = null;
    public static Main getInstance() { return instance; }

    // GENERAL
    public Map<Option, Boolean> optionsUsed = new HashMap<>(); // Map contenant les options du plugin
    private Material spawnerItem; // Item du spawner
    private Material playerHeadItem; // Item de la tête du joueur
    private Material signItem; // Item du panneau
    private SpawnerPickaxe spawnerPickaxe; // Item de la pioche à spawner
    private SuperSpawnerPickaxe superSpawnerPickaxe; // Item de la super pioche à spawner

    // ENTITEES
    public List<EntityType> entityList = new ArrayList<>(); // Liste des entités vivantes
    public String entityListString; // Chaîne de caractère qui contient la liste des entités vivantes
    public Map<EntityType, String> entityMapName; // Map contenant chaque entité avec son nom modifié

    // INVENTAIRES
    public Inventory manageInventory; // Inventaire du /spawnerpickaxe manage ou /spawnerpickaxe gui
    public Inventory spawnerInventory; // Inventaire du /spawner ou /spawner list
    public Inventory spawnerInventoryNext; // Inventaire du /spawner ou /spawner list
    public Inventory shopInventory; // Inventaire du /spawnerpickaxe shop
    public Inventory rightClickSpawnerInventory; // Inventaire quand le joueur fait un clique droit sur un spawner

    @Override
    public void onEnable() {

        instance = this;

        if(!setupEconomy()) {
            getLogger().severe("§4Economy plugin needed !");
        }

        saveDefaultConfig();
        ManagerMain.setDefaultItems(this); // Permet de définir les items
        ManagerMain.getOptions(this); // Permet de récupérer le statut des options (true ou false)
        setDefaultEntities(); // Permet de définir la liste des entités
        setDefaultInventories(); // Permet de définit la liste des inventaires
        loadEntityFile(); // Permet de charger le fichier des entités (ou de le créer s'il n'existe pas)

        registerEventsAndCommands(); // Permet d'enregistrer les events

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
    private void registerEventsAndCommands() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new InventoriesListeners(this), this);
        pm.registerEvents(new SignListeners(this), this);
        pm.registerEvents(new SpawnerBlastListeners(this), this);
        pm.registerEvents(new SpawnerBreakListeners(this), this);
        pm.registerEvents(new SpawnerClickListeners(this), this);
        pm.registerEvents(new SpawnerPlaceListeners(this), this);

        getCommand("spawner").setExecutor(new SpawnerCommand(this));
        getCommand("spawnerpickaxe").setExecutor(new SpawnerPickaxeCommand(this));
        getCommand("superspawnerpickaxe").setExecutor(new SuperSpawnerPickaxeCommand(this));
    }

    // Permet de définir la liste des entités
    private void setDefaultEntities() {
        entityList = Entities.setEntityList(this);
        entityListString = Entities.setEntityListString(entityList);
    }

    // Permet de définit la liste des inventaires
    public void setDefaultInventories() {
        manageInventory = ManagerMain.setManageInventory(this);
        Inventories.setEntityInventoryList(this);
        Inventories.setShopInventory(this);
        Inventories.setRightClickSpawnerInventory(this);
    }

    // Permet de charger le fichier des entités (ou de le créer s'il n'existe pas)
    private void loadEntityFile() {
        if (!YamlFileUtils.fileExist("entity")) {
            YamlFileUtils.createFile("entity");
            Entities.addAllEntitiesOnYaml(this, "entity");
        }
        entityMapName = Entities.setEntityMapName("entity");
    }

    public void setItems(Material spawnerItem, Material playerHeadItem, Material signItem, SpawnerPickaxe spawnerPickaxe, SuperSpawnerPickaxe superSpawnerPickaxe) {
        this.spawnerItem = spawnerItem;
        this.playerHeadItem = playerHeadItem;
        this.signItem = signItem;
        this.spawnerPickaxe = spawnerPickaxe;
        this.superSpawnerPickaxe = superSpawnerPickaxe;
    }

    public Material getSpawnerItem() {
        return spawnerItem;
    }

    public Material getPlayerHeadItem() {
        return playerHeadItem;
    }

    public Material getSignItem() {
        return signItem;
    }

    public ItemStack getPickaxe() {
        return spawnerPickaxe.getPickaxe();
    }

    public ItemStack getSuperPickaxe() { return superSpawnerPickaxe.getPickaxe(); }
}
