package fr.hegsis.spawnerpickaxe.utils;

import fr.hegsis.spawnerpickaxe.Main;
import fr.hegsis.spawnerpickaxe.SpawnerPickaxe;
import fr.hegsis.spawnerpickaxe.manager.Option;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Inventories {

    public static void setEntityInventoryList(Main main) {
        String spawnerInventoryName = main.getConfig().getString("spawner-inventory.name").replaceAll("&", "§");
        main.spawnerInventory = Bukkit.createInventory(null, 54, spawnerInventoryName);
        main.spawnerInventoryNext = Bukkit.createInventory(null, 54, spawnerInventoryName);

        ItemStack spawnerIt;
        ItemMeta spawnerIm;
        boolean multipage = false;
        int nb_ent = 0;
        for (EntityType e : main.entityList) {
            if (main.optionsUsed.get(Option.SPAWNERLIST_MENU)) {
                //spawnerIt = Utils.playerHead("MHF_" + (e.name().replace("_", "")).toUpperCase(), main.getConfig().getString("spawner-inventory.item-name").replaceAll("%entity%", e.name()), main.getSpawnerItem().toString(), main);

                spawnerIt = new ItemStack(main.getSpawnerItem());
                spawnerIm = spawnerIt.getItemMeta();
                spawnerIm.setDisplayName(main.getConfig().getString("spawner-inventory.item-name").replaceAll("%entity%", e.name()).replaceAll("&", "§"));
                List<String> lore = new ArrayList<>();
                lore.add("§7" + e.toString());
                spawnerIm.setLore(lore);
                spawnerIt.setItemMeta(spawnerIm);

                if (nb_ent >= 45) {
                    multipage = true;
                    main.spawnerInventoryNext.addItem(spawnerIt);
                } else {
                    main.spawnerInventory.addItem(spawnerIt);
                }
                nb_ent++;
            }
        }

        // On met les vitres noirs
        spawnerIt = new ItemStack(Material.getMaterial(main.getConfig().getString("spawner-inventory.glass.item")), 1, (short) main.getConfig().getInt("spawner-inventory.glass.data"));
        spawnerIm = spawnerIt.getItemMeta();
        spawnerIm.setDisplayName(" ");
        spawnerIt.setItemMeta(spawnerIm);
        for (int i=45; i<54; i++) {
            if (i != 49) {
                main.spawnerInventory.setItem(i, spawnerIt);
                main.spawnerInventoryNext.setItem(i, spawnerIt);
            }
        }

        spawnerIt = Utils.playerHead("MHF_Exclamation", main.getConfig().getString("spawner-inventory.leave"), "BARRIER", main);

        if (multipage) {
            main.spawnerInventory.setItem(45, spawnerIt);
            main.spawnerInventoryNext.setItem(45, spawnerIt);

            // On met les tête page suivante et précédentes
            main.spawnerInventory.setItem(49, Utils.playerHead("MHF_ArrowRight", main.getConfig().getString("spawner-inventory.next-page"), "ARROW", main));
            main.spawnerInventoryNext.setItem(49, Utils.playerHead("MHF_ArrowLeft", main.getConfig().getString("spawner-inventory.previous-page"), "ARROW", main));
        } else {
            main.spawnerInventory.setItem(49, spawnerIt);
        }
    }

    public static void setShopInventory(Main main) {
        main.shopInventory = Bukkit.createInventory(null, 27, main.getConfig().getString("shop-inventory.name").replaceAll("&", "§"));

        ItemStack it;
        ItemMeta im;

        it = new ItemStack(Material.getMaterial(main.getConfig().getString("shop-inventory.glass.item")), 1, (short) main.getConfig().getInt("shop-inventory.glass.data"));
        im = it.getItemMeta();
        im.setDisplayName(" ");
        it.setItemMeta(im);

        for (int i=0; i<27; i++) {
            main.shopInventory.setItem(i, it);
        }

        int[] cell = {11, 12, 14, 15};
        int[] amount = {10, 1, 1, 10};
        String color = "§c-";
        Material material = Material.getMaterial(main.getConfig().getString("shop-inventory.remove-item.item"));
        short data = (short) main.getConfig().getInt("shop-inventory.remove-item.data");

        for (int i = 0; i<4; i++) {
            if (i == 2) {
                color = "§2+";
                material = Material.getMaterial(main.getConfig().getString("shop-inventory.add-item.item"));
                data = (short) main.getConfig().getInt("shop-inventory.add-item.data");
            }

            it = new ItemStack(material, amount[i], data);
            im = it.getItemMeta();
            im.setDisplayName(color + amount[i]);
            it.setItemMeta(im);
            main.shopInventory.setItem(cell[i], it);
        }

        //Item de la pioche
        SpawnerPickaxe sp = new SpawnerPickaxe(main.getPickaxe().clone());
        sp.setDurability(1);
        it = sp.getPickaxe();
        im = it.getItemMeta();
        im.addEnchant(Enchantment.DURABILITY, 3, true);
        im.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        it.setItemMeta(im);
        main.shopInventory.setItem(13, it);

        //Item du prix
        it = Utils.playerHead("MHF_QUESTION", main.getConfig().getString("shop-inventory.price-item.name").replace("&", "§").replaceAll("%price%",""+main.getConfig().getInt("shop-inventory.price")), main.getConfig().getString("shop-inventory.price-item.item"), main);
        main.shopInventory.setItem(22, it);

        //Item pour quitter
        it = new ItemStack(Material.getMaterial(main.getConfig().getString("shop-inventory.quit-item.item")), 1, (short) main.getConfig().getInt("shop-inventory.quit-item.data"));
        im = it.getItemMeta();
        im.setDisplayName(main.getConfig().getString("shop-inventory.quit-item.name").replace("&", "§"));
        it.setItemMeta(im);
        main.shopInventory.setItem(21, it);

        //Item pour accepter
        it = new ItemStack(Material.getMaterial(main.getConfig().getString("shop-inventory.accept-item.item")), 1, (short) main.getConfig().getInt("shop-inventory.accept-item.data"));
        im = it.getItemMeta();
        im.setDisplayName(main.getConfig().getString("shop-inventory.accept-item.name").replace("&", "§"));
        it.setItemMeta(im);
        main.shopInventory.setItem(23, it);
    }
}
