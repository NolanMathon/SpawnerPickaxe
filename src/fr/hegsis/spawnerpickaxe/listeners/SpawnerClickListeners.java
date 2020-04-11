package fr.hegsis.spawnerpickaxe.listeners;

import fr.hegsis.spawnerpickaxe.Main;
import fr.hegsis.spawnerpickaxe.manager.Option;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SpawnerClickListeners implements Listener {

    private Main main;
    public SpawnerClickListeners(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        if (e.isCancelled()) return;

        if (!main.optionsUsed.get(Option.RIGHT_CLICK_SPAWNER_MENU)) return;

        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return; // Si l'action n'est pas un click droit

        Block block = e.getClickedBlock();
        if (block.getType() != main.getSpawnerItem()) return; // Si le block n'est pas un spawner

        Player p = e.getPlayer();
        CreatureSpawner spawner = (CreatureSpawner) block.getState();
        EntityType entityType = spawner.getSpawnedType();

        ItemStack it = new ItemStack(main.getSpawnerItem());
        ItemMeta im = it.getItemMeta();
        im.setDisplayName(main.getConfig().getString("spawner-inventory.item-name").replaceAll("&", "§").replaceAll("%entity%", entityType.toString()));
        List<String> lore = new ArrayList<>();
        lore.add("§7"+block.getLocation().getBlockX());
        lore.add("§7"+block.getLocation().getBlockY());
        lore.add("§7"+block.getLocation().getBlockZ());
        im.setLore(lore);
        it.setItemMeta(im);

        Inventory inv = Bukkit.createInventory(null, 27, main.getConfig().getString("spawner-menu.name").replaceAll("&", "§"));
        inv.setContents(main.rightClickSpawnerInventory.getContents().clone());
        inv.setItem(4, it);
        p.openInventory(inv);
    }
}
