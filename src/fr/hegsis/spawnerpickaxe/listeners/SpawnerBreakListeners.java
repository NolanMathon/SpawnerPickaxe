package fr.hegsis.spawnerpickaxe.listeners;

import fr.hegsis.spawnerpickaxe.Main;
import fr.hegsis.spawnerpickaxe.SpawnerPickaxe;
import fr.hegsis.spawnerpickaxe.manager.Option;
import fr.hegsis.spawnerpickaxe.utils.GiveItems;
import fr.hegsis.spawnerpickaxe.utils.Utils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class SpawnerBreakListeners implements Listener {

    private Main main;

    public SpawnerBreakListeners(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onSpawnerBreak(BlockBreakEvent e) {
        // Si l'option du faction est activée
        if (main.optionsUsed.get(Option.FACTION)) {
            if (e.isCancelled()) return;
        }

        Player p = e.getPlayer();
        if (p.getItemInHand() == null) return;

        if (p.getItemInHand().getType() != main.getPickaxe().getType()) return;

        ItemStack itemInHand = p.getItemInHand();
        if (!itemInHand.hasItemMeta()) return; // Si pas d'item meta

        if (!itemInHand.getItemMeta().getDisplayName().equalsIgnoreCase(main.getPickaxe().getItemMeta().getDisplayName()))
            return; // Si pas le même nom que la pioche à spawner

        if (!itemInHand.getItemMeta().hasLore()) return; // Si pas de description

        if (!Utils.hasPermission(p, "pickaxe-use", main)) { // Si le joueur n'a pas la permission d'utiliser la pioche
            Utils.sendMessage(p, "no-permission", main);
            e.setCancelled(true);
            return;
        }

        if (e.getBlock().getType() != main.getSpawnerItem()) { // Si l'item cassé n'est pas un spawner
            Utils.sendMessage(p, "break-spawner-only", main);
            e.setCancelled(true);
            return;
        }

        // On récup le type de spawner
        Block block = e.getBlock();
        CreatureSpawner spawner = (CreatureSpawner) block.getState();
        EntityType entity = spawner.getSpawnedType();

        // On récup la dura de la pioche
        SpawnerPickaxe sp = new SpawnerPickaxe(itemInHand);
        int durability = sp.getDurability(main);

        durability--;
        if (durability < 0) {
            e.setCancelled(true);
            Utils.sendMessage(p, "durability-already-zero", main);
            p.getInventory().setItemInHand(new ItemStack(Material.AIR));
            return;
        }

        if (durability == 0) {
            Utils.sendMessage(p, "pickaxe-broke", main);
            p.getInventory().setItemInHand(new ItemStack(Material.AIR));
        } else {
            sp.removeDurability(1, main);
            p.sendMessage(Utils.getConfigMessage("pickaxe-less-one-durability", main).replaceAll("%durability%", "" + durability));
        }

        Utils.playSound(p, "on-spawner-break", main);
        GiveItems.giveSpawner(p, entity, 1, true, main);

    }
}
