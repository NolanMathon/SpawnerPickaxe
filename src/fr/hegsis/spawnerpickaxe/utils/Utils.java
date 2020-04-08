package fr.hegsis.spawnerpickaxe.utils;

import fr.hegsis.spawnerpickaxe.Main;
import fr.hegsis.spawnerpickaxe.manager.Option;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

public class Utils {

    public static boolean hasPermission(@NotNull Player p, String path, @NotNull Main main) {
        return (p.hasPermission(main.getConfig().getString("permissions.all")) || p.hasPermission(main.getConfig().getString("permissions."+path)));
    }

    public static void sendMessage(@NotNull Player p, String path, @NotNull Main main) {
        p.sendMessage(main.getConfig().getString("messages."+path).replaceAll("&", "§"));
    }

    public static String getConfigMessage(String path, @NotNull Main main) {
        return main.getConfig().getString("messages."+path).replaceAll("&", "§");
    }

    //Fonction qui permet de jouer un son
    public static void playSound(Player p, String sound, @NotNull Main main) {
        String[] use = main.getConfig().getString("sounds."+sound).split(";");
        Sound s = null;
        try {
            s = Sound.valueOf(use[0]);
        } catch (Exception e) {
            if (hasPermission(p, "show-errors", main)) {
                p.sendMessage(getConfigMessage("incorrect-sound", main).replaceAll("%sound%", sound));
            }
        }

        if (use[1].equalsIgnoreCase("true") && s != null) {
            p.playSound(p.getLocation(), s, 1F, 1F);
        }
    }

    // Permet de retourner un nombre
    public static int isNumber(String entier) {
        try {
            return Integer.parseInt(entier);
        } catch (Exception e) {
            return -1;
        }
    }

    // Permet de recupérer une tête
    public static ItemStack playerHead(String owner, String name, String other_item, @NotNull Main main) {
        ItemStack it = null;
        if (main.optionsUsed.get(Option.PLAYER_HEAD_IN_MENU)) {
            it = new ItemStack(main.getPlayerHeadItem(), 1, (short) 3);
            SkullMeta sm = (SkullMeta) it.getItemMeta();
            sm.setOwner(owner);
            sm.setDisplayName(name.replaceAll("&","§"));
            it.setItemMeta(sm);
        } else {
            it = new ItemStack(Material.getMaterial(other_item.toUpperCase()));
            ItemMeta im = it.getItemMeta();
            im.setDisplayName(name.replaceAll("&", "§"));
            it.setItemMeta(im);
        }
        return it;
    }
}