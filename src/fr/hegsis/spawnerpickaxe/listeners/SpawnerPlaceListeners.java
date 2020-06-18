package fr.hegsis.spawnerpickaxe.listeners;

import fr.hegsis.spawnerpickaxe.Main;
import fr.hegsis.spawnerpickaxe.manager.Option;
import fr.hegsis.spawnerpickaxe.utils.NBT;
import fr.hegsis.spawnerpickaxe.utils.Utils;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

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

        NBT spawnerNBT = NBT.get(spawner);
        // Si le spawner n'a pas de tag
        if (spawnerNBT == null) {
            Utils.sendMessage(p, "error", main);
            e.setCancelled(true);
            return;
        }

        // Si le spawner n'a pas le tag "spawnerEntity"
        if (!spawnerNBT.hasKey("spawnerEntity")) {
            Utils.sendMessage(p, "error", main);
            e.setCancelled(true);
            return;
        }

        String entity = spawnerNBT.getString("spawnerEntity");

        /*net.minecraft.server.v1_8_R3.ItemStack nmsSpawner = CraftItemStack.asNMSCopy(spawner);
        // Si le spawner n'a pas de tag
        if (!nmsSpawner.hasTag()) {
            Utils.sendMessage(p, "error", main);
            e.setCancelled(true);
            return;
        }

        NBTTagCompound spawnerCompound = nmsSpawner.getTag();
        // Si le spawner n'a pas le tag "spawnerEntity"
        if (!spawnerCompound.hasKey("spawnerEntity")) {
            Utils.sendMessage(p, "error", main);
            e.setCancelled(true);
            return;
        }

        String entity = spawnerCompound.getString("spawnerEntity");*/

        EntityType spawnerEntity = EntityType.valueOf(entity);
        CreatureSpawner spawnerBlock = (CreatureSpawner) e.getBlockPlaced().getState();
        spawnerBlock.setSpawnedType(spawnerEntity);
        spawnerBlock.update();
        Utils.playSound(p, "on-spawner-place", main);
        p.sendMessage(Utils.getConfigMessage("spawner-place", main).replaceAll("%spawner%", entity));
    }
}
