package fr.hegsis.spawnerpickaxe.commands.tabcompletion;

import fr.hegsis.spawnerpickaxe.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class SpawnerTabCompleter implements TabCompleter {

    private Main main;
    public SpawnerTabCompleter(Main main) {
        this.main = main;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cms, String label, String[] args) {

        if (args.length == 1) {
            List<String> entityListString = new ArrayList<>();
            entityListString.add("list");
            for (EntityType et : main.entityList) {
                entityListString.add(et.toString());
            }
            return entityListString;
        }

        return null;
    }
}
