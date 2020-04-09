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
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
    public Inventory manageInventory; // Inventaire du /spawnerpickaxe manage
    public Inventory spawnerInventory; // Inventaire du /spawner ou /spawner list
    public Inventory spawnerInventoryNext; // Inventaire du /spawner ou /spawner list

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
        isMaterial(material, spawnerItem);

        material = getConfig().getString("player-head-item");
        playerHeadItem = Material.getMaterial(material);
        isMaterial(material, playerHeadItem);

        material = getConfig().getString("sign-item");
        signItem = Material.getMaterial(material);
        isMaterial(material, signItem);

        material = getConfig().getString("pickaxe.item-type");
        Material mat = Material.getMaterial(material);
        isMaterial(material, mat);
        spawnerPickaxe = new SpawnerPickaxe(new ItemStack(mat));
        spawnerPickaxe.setDisplayName(getConfig().getString("pickaxe.name").replaceAll("&", "§"));
        spawnerPickaxe.setLore(Utils.convertListColorCode(getConfig().getStringList("pickaxe.description")));
    }

    private void isMaterial(String material, Material mat) {
        try {
            if (mat != null) return;
        } catch (NullPointerException e) { }
        getServer().getConsoleSender().sendMessage("§4Item §c" + material + " §4isn't valid !");
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

    private void setAllDefaultInventoriesAndEntities() {
        entityList = Entities.setEntityList(this);
        entityListString = Entities.setEntityListString(entityList);
        manageInventory = ManagerMain.setManageInventory(this);
        Inventories.setEntityInventoryList(this);
    }

    public void payAndGiveSpawnerPickaxe(Player p, double price, int durability) {
        if (p.getInventory().firstEmpty() != -1) {
            OfflinePlayer of = Bukkit.getOfflinePlayer(p.getUniqueId());
            double playerBalance = economy.getBalance(of);
            if (playerBalance >= price) {
                givePickaxe(p, durability);
                economy.withdrawPlayer(of, price);
                Utils.sendMessage(p, "buy-pickaxe", this);
            } else {
                Utils.sendMessage(p, "buy-pickaxe-fail", this);
            }
        } else {
            Utils.sendMessage(p, "full-inventory", this);
        }
    }

    public void givePickaxe(Player p, int durability) {
        SpawnerPickaxe sp = new SpawnerPickaxe(spawnerPickaxe.getPickaxe().clone());
        sp.setDurability(durability);
        sp.give(p);
    }

    // Fonction qui permet de give le spawner
    public void giveSpawner(Player p, EntityType entityType, int amount, boolean onTheGround) {
        ItemStack itemStack = new ItemStack(getSpawnerItem(), amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(getConfig().getString("spawner-inventory.item-name").replaceAll("&", "§").replaceAll("%entity%", entityType.toString()));
        List<String> lore = new ArrayList<>();
        lore.add("§d" + entityType.toString());
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        // Si l'inventaire du joueur est full, on lui drop le spawner au sol
        if (p.getInventory().firstEmpty() == -1 || onTheGround) {
            p.getWorld().dropItemNaturally(p.getLocation(), itemStack);
            p.sendMessage(Utils.getConfigMessage("give-spawner-on-the-ground", this).replaceAll("%amount%", ""+amount).replaceAll("%entity%", ""+entityType));
            return;
        }
        // Sinon on lui met dans son inventaire
        p.getInventory().addItem(itemStack);
        p.sendMessage(Utils.getConfigMessage("give-spawner", this).replaceAll("%amount%", ""+amount).replaceAll("%entity%", ""+entityType));
    }

}
