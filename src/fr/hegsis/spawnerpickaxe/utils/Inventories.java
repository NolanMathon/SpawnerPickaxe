package fr.hegsis.spawnerpickaxe.utils;

import fr.hegsis.spawnerpickaxe.Main;
import fr.hegsis.spawnerpickaxe.manager.Option;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
}
