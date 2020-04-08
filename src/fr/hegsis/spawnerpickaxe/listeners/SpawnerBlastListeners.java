package fr.hegsis.spawnerpickaxe.listeners;

import fr.hegsis.spawnerpickaxe.Main;
import fr.hegsis.spawnerpickaxe.manager.Option;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.ArrayList;
import java.util.List;

public class SpawnerBlastListeners implements Listener {

    private Main main;

    public SpawnerBlastListeners(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onTNTExplode(EntityExplodeEvent e) {

        // Si l'option de casser les spawner à la tnt est activée !
        if (main.optionsUsed.get(Option.TNT_BREAK_SPAWNER)) return;

        List<Block> spawnerList = new ArrayList<>();
        for (Block b : e.blockList()) {
            if (b.getType() == main.getSpawnerItem()) {
                spawnerList.add(b);
            }
        }

        if (spawnerList.size() > 0) {
            for (Block b : spawnerList) {
                e.blockList().remove(b);
            }
        }

    }
}
