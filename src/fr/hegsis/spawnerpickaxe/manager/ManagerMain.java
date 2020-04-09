package fr.hegsis.spawnerpickaxe.manager;

import fr.hegsis.spawnerpickaxe.Main;
import fr.hegsis.spawnerpickaxe.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ManagerMain {

    public static void getOptions(Main main) {
        for (Option op : Option.values()) {
            main.optionsUsed.put(op, main.getConfig().getBoolean("use."+op.toString().toLowerCase()));
        }
    }

    public static boolean setOption(@NotNull Main main, Option option, boolean value) {
        if (main.optionsUsed.get(option) != value) {
            main.optionsUsed.replace(option, value);
            main.getConfig().set("use."+option.toString().toLowerCase(), value);
            main.reloadConfig();
            return true;
        }

        return false;
    }

    public static Inventory setManageInventory(@NotNull Main main) {
        Inventory inv = Bukkit.createInventory(null, 36, main.getConfig().getString("manage-gui.menu-name").replaceAll("&","§"));

        ItemStack it;
        ItemMeta im;

        it = new ItemStack(Material.getMaterial(main.getConfig().getString("manage-gui.glass.item")), 1, (short) main.getConfig().getInt("manage-gui.glass.data"));
        im = it.getItemMeta();
        im.setDisplayName(" ");
        it.setItemMeta(im);

        for (int i=0; i<36; i++) {
            inv.setItem(i, it);
        }

        int[] cell = {10, 11, 14, 15, 16, 21, 23};
        String[] material = {"OBSIDIAN", "DIAMOND_PICKAXE"/*main.getSuperPickaxeItem().toString()*/, main.getSpawnerItem().toString(), main.getPickaxe().getType().toString(), main.getSignItem().toString(),  main.getSpawnerItem().toString(), "STONE_BUTTON"};
        String[] name = new String[Option.values().length+1];
        int j=0;
        for (Option op : Option.values()) {
            if (op != Option.TNT_BREAK_SPAWNER && op != Option.PLAYER_HEAD_IN_MENU) {
                name[j] = getConfigName(op.toString().toLowerCase(), main.optionsUsed.get(op), main);
                j++;
            }
        }
        name[j] = main.getConfig().getString("manage-gui.reload-config-file").replaceAll("&","§");

        for (int i=0; i<cell.length; i++) {
            it = new ItemStack(Material.getMaterial(material[i]));
            im = it.getItemMeta();
            im.setDisplayName(name[i]);
            it.setItemMeta(im);
            inv.setItem(cell[i], it);
        }

        it = Utils.playerHead("MHF_TNT2", getConfigName("tnt_break_spawner", main.optionsUsed.get(Option.TNT_BREAK_SPAWNER), main), "TNT", main);
        inv.setItem(12, it);

        it = Utils.playerHead("MHF_Exclamation", getConfigName("player_head_in_menu", main.optionsUsed.get(Option.PLAYER_HEAD_IN_MENU), main), main.getPlayerHeadItem().toString(), main);
        inv.setItem(13, it);

        it = Utils.playerHead("MHF_Exclamation", main.getConfig().getString("manage-gui.leave-item").replaceAll("&","§"), "BARRIER", main);
        inv.setItem(27, it);

        it = Utils.playerHead("MHF_Question",  main.getConfig().getString("manage-gui.help-item.name").replaceAll("&","§"), "PAPER", main);
        im = it.getItemMeta();
        List<String> lore = main.getConfig().getStringList("manage-gui.help-item.lore");
        for (int i=0; i<lore.size(); i++) {
            lore.set(i, lore.get(i).replaceAll("&", "§"));
        }
        im.setLore(lore);
        it.setItemMeta(im);
        inv.setItem(31, it);

        return inv;
    }

    public static String getConfigName(String configname, boolean bool, Main main) {
        String name;
        if (bool) {
            name = main.getConfig().getString("manage-gui."+configname).replaceAll("&","§").replaceAll("%status%", main.getConfig().getString("manage-gui.true").replaceAll("&","§"));
        } else {
            name = main.getConfig().getString("manage-gui."+configname).replaceAll("&","§").replaceAll("%status%", main.getConfig().getString("manage-gui.false").replaceAll("&","§"));
        }
        return name;
    }
}
