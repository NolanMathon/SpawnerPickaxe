package fr.hegsis.spawnerpickaxe.utils;

import fr.hegsis.spawnerpickaxe.Main;
import fr.hegsis.spawnerpickaxe.SpawnerPickaxe;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GiveItems {

    public static void payAndGiveSpawnerPickaxe(Player p, double price, int durability, Main main) {
        if (p.getInventory().firstEmpty() != -1) {
            OfflinePlayer of = Bukkit.getOfflinePlayer(p.getUniqueId());
            double playerBalance = main.economy.getBalance(of);
            if (playerBalance >= price) {
                givePickaxe(p, durability, main);
                main.economy.withdrawPlayer(of, price);
                Utils.sendMessage(p, "buy-pickaxe", main);
            } else {
                Utils.sendMessage(p, "buy-pickaxe-fail", main);
            }
        } else {
            Utils.sendMessage(p, "full-inventory", main);
        }
    }

    public static void givePickaxe(Player p, int durability, Main main) {
        SpawnerPickaxe sp = new SpawnerPickaxe(main.getPickaxe().clone());
        sp.setDurability(durability);
        sp.give(p);
    }

    // Fonction qui permet de give le spawner
    public static void giveSpawner(Player p, EntityType entityType, int amount, boolean onTheGround, Main main) {
        ItemStack itemStack = new ItemStack(main.getSpawnerItem(), amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(main.getConfig().getString("spawner-inventory.item-name").replaceAll("&", "§").replaceAll("%entity%", entityType.toString()));
        List<String> lore = new ArrayList<>();
        lore.add("§d" + entityType.toString());
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        // Si l'inventaire du joueur est full, on lui drop le spawner au sol
        if (p.getInventory().firstEmpty() == -1 || onTheGround) {
            p.getWorld().dropItemNaturally(p.getLocation(), itemStack);
            p.sendMessage(Utils.getConfigMessage("give-spawner-on-the-ground", main).replaceAll("%amount%", ""+amount).replaceAll("%entity%", ""+entityType));
            return;
        }
        // Sinon on lui met dans son inventaire
        p.getInventory().addItem(itemStack);
        p.sendMessage(Utils.getConfigMessage("give-spawner", main).replaceAll("%amount%", ""+amount).replaceAll("%entity%", ""+entityType));
    }
}
