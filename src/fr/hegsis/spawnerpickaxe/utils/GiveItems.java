package fr.hegsis.spawnerpickaxe.utils;

import fr.hegsis.spawnerpickaxe.Main;
import fr.hegsis.spawnerpickaxe.objects.SpawnerPickaxe;
import fr.hegsis.spawnerpickaxe.objects.SuperSpawnerPickaxe;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagString;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class GiveItems {

    public static void payAndGiveSpawnerPickaxe(Player p, double price, int durability, Main main) {
        if (p.getInventory().firstEmpty() != -1) {
            OfflinePlayer of = Bukkit.getOfflinePlayer(p.getUniqueId());
            double playerBalance = main.economy.getBalance(of);
            if (playerBalance >= price) {
                givePickaxe(p, durability, main);
                main.economy.withdrawPlayer(of, price);
                p.sendMessage(Utils.getConfigMessage("buy-pickaxe", main).replaceAll("%amount%", ""+durability).replaceAll("%price%", ""+price));
            } else {
                Utils.sendMessage(p, "buy-pickaxe-fail", main);
            }
        } else {
            Utils.sendMessage(p, "full-inventory", main);
        }
    }

    public static void givePickaxe(Player p, int durability, @NotNull Main main) {
        SpawnerPickaxe sp = new SpawnerPickaxe(main.getPickaxe().clone());
        sp.setDurability(durability);
        sp.give(p);
    }

    public static void giveSuperPickaxe(Player p, int durability, @NotNull Main main) {
        SuperSpawnerPickaxe ssp = new SuperSpawnerPickaxe(main.getSuperPickaxe().clone());
        ssp.setDurability(durability);
        ssp.give(p);
    }

    // Fonction qui permet de give le spawner
    public static void giveSpawner(@NotNull Player p, @NotNull EntityType entityType, int amount, boolean onTheGround, @NotNull Main main) {
        ItemStack itemStack = new ItemStack(main.getSpawnerItem(), amount);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(main.getConfig().getString("spawner-inventory.item-name").replaceAll("&", "§").replaceAll("%entity%", Entities.getEntityName(entityType)));
        itemMeta.setLore(Utils.convertListColorCode(main.getConfig().getStringList("spawner-inventory.item-lore")));
        itemStack.setItemMeta(itemMeta);

        net.minecraft.server.v1_8_R3.ItemStack nmsSpawner = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound spawnerCompound = (nmsSpawner.hasTag()) ? nmsSpawner.getTag() : new NBTTagCompound();
        spawnerCompound.set("spawnerEntity", new NBTTagString(entityType.toString()));
        nmsSpawner.setTag(spawnerCompound);
        CraftItemStack.asBukkitCopy(nmsSpawner);

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
