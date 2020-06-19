package fr.hegsis.spawnerpickaxe.commands.tabcompletion;

import fr.hegsis.spawnerpickaxe.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Arrays;
import java.util.List;

public class SpawnerPickaxeTabCompleter implements TabCompleter {

    private Main main;
    public SpawnerPickaxeTabCompleter(Main main) {
        this.main = main;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cms, String label, String[] args) {

        if (args.length == 1) {
            return Arrays.asList("shop", "fusion", "gui", "manage");
        }

        return null;
    }
}
