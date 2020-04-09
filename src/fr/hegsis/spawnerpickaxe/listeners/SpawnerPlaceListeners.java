package fr.hegsis.spawnerpickaxe.listeners;

import fr.hegsis.spawnerpickaxe.Main;
import fr.hegsis.spawnerpickaxe.manager.Option;
import fr.hegsis.spawnerpickaxe.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SpawnerPlaceListeners implements Listener {

    private Main main;

    public SpawnerPlaceListeners(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onSpawnerPlace(BlockPlaceEvent e) {
        // Si l'option du faction est activée
        if (main.optionsUsed.get(Option.FACTION)) {
            if (e.isCancelled()) return;
        }

        if (!(e.getBlockPlaced().getType() == main.getSpawnerItem())) return; // Si ce n'est pas un spawner on fait rien

        Player p = e.getPlayer();
        if (!(p.getItemInHand().getType() == main.getSpawnerItem())) return; // Si l'item dans la main du joueur n'est pas un spawner on fait rien

        // Si le spawner n'a pas de description on fait rien
        ItemStack spawner = p.getItemInHand();
        if (!(spawner.getItemMeta().hasLore())) return;

        // Si le joueur n'a pas la permission de placer des spawners
        if (!Utils.hasPermission(p, "spawner-use", main)) {
            Utils.sendMessage(p, "no-permission", main);
            e.setCancelled(true);
            return;
        }

        List<String> lore = spawner.getItemMeta().getLore();
        EntityType spawnerEntity = EntityType.valueOf(ChatColor.stripColor(lore.get(0)));
        CreatureSpawner spawnerBlock = (CreatureSpawner) e.getBlockPlaced().getState();
        spawnerBlock.setSpawnedType(spawnerEntity);
        spawnerBlock.update();
        Utils.playSound(p, "on-spawner-place", main);
        p.sendMessage(Utils.getConfigMessage("spawner-place", main).replaceAll("%spawner%", spawnerEntity.toString()));
    }
}
