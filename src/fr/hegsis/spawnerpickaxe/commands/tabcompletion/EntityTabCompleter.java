package fr.hegsis.spawnerpickaxe.commands.tabcompletion;

import fr.hegsis.spawnerpickaxe.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityTabCompleter implements TabCompleter {

    private Main main;
    public EntityTabCompleter(Main main) {
        this.main = main;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cms, String label, String[] args) {

        if (args.length == 1) {
            return Arrays.asList("unlocklist", "locklist", "lock", "unlock", "setname", "reset");
        }

        String subCommand = args[0];

        if (args.length == 2 && (subCommand.equalsIgnoreCase("lock") || subCommand.equalsIgnoreCase("setname"))) {
            List<String> entityListString = new ArrayList<>();
            for (EntityType et : main.entityList) {
                entityListString.add(et.toString());
            }
            return entityListString;
        }

        if (args.length == 2 && subCommand.equalsIgnoreCase("unlock")) {
            List<String> entityListString = new ArrayList<>();
            for (EntityType et : main.deleteEntities) {
                entityListString.add(et.toString());
            }
            return entityListString;
        }

        return null;
    }
}
