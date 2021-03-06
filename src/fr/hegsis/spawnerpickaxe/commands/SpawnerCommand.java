package fr.hegsis.spawnerpickaxe.commands;

import fr.hegsis.spawnerpickaxe.Main;
import fr.hegsis.spawnerpickaxe.manager.Option;
import fr.hegsis.spawnerpickaxe.utils.GiveItems;
import fr.hegsis.spawnerpickaxe.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class SpawnerCommand implements CommandExecutor {

    private Main main;
    public SpawnerCommand(Main main) {
        this.main = main;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        // Commande /spawner ou /spawner list
        if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("list"))) {
            if (sender instanceof Player) { // Si celui qui fait la commande est un joeuur
                Player p = (Player) sender;
                if (!Utils.hasPermission(p, "spawner-list", main)) {
                    Utils.sendMessage(p, "no-permission", main);
                    return false;
                }

                if (main.optionsUsed.get(Option.SPAWNERLIST_MENU)) {
                    p.openInventory(main.spawnerInventory);
                    return true;
                }
            }

            sender.sendMessage(main.entityListString);
            return true;
        }

        if (args.length <= 3) {

            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (!Utils.hasPermission(p, "spawner-give", main)) {
                    Utils.sendMessage(p, "no-permission", main);
                    return false;
                }
            }

            // On check si l'entitée écrit par le joueur existe
            try {
                EntityType.valueOf(args[0].toUpperCase());
            } catch (Exception e) {
                sender.sendMessage(Utils.getConfigMessage("spawner-not-found", main).replaceAll("%entity%", args[0].toUpperCase()));
                return false;
            }

            // On check si l'entité n'est pas désactivé
            if (!main.entityList.contains(EntityType.valueOf(args[0].toUpperCase()))) {
                sender.sendMessage(Utils.getConfigMessage("entity-disable", main).replaceAll("%entity%", args[0].toUpperCase()));
                return false;
            }

            if (args.length >= 2) { // Si il y deux arguments | /spawner <entité> [joueur]
                if (Bukkit.getPlayer(args[1]) != null) { // Si le second argument (le joueur) existe | /spawner <entité> [joueur]
                    Player target = Bukkit.getPlayer(args[1]);
                    if (args.length == 3) {
                        int amount = Utils.isNumber(args[2]);
                        if (amount >= 1) {
                            GiveItems.giveSpawner(target, EntityType.valueOf(args[0].toUpperCase()), amount, false, main);
                            return true;
                        }

                        // Si le nombre est erroné
                        sender.sendMessage(Utils.getConfigMessage("invalid-number", main).replaceAll("%number%", args[2]));
                        return false;
                    }

                    // Si il n'y a pas de nombre de spawners
                    GiveItems.giveSpawner(target, EntityType.valueOf(args[0].toUpperCase()), 1, false, main);
                    return true;
                }

                // Si le joueur appelé n'existe pas ou n'est pas connecté
                sender.sendMessage(Utils.getConfigMessage("invalid-player", main).replaceAll("%player%", args[1]));
                return false;
            }


            if (sender instanceof Player) {
                // Si il y a qu'un seul argument on donne le spawner à l'utilisateur qui a fait la commande
                GiveItems.giveSpawner((Player) sender, EntityType.valueOf(args[0].toUpperCase()), 1, false, main);
                return true;
            } else {
                sender.sendMessage(Utils.getConfigMessage("only-player", main));
                return false;
            }
        }

        // Si il y a plus de 3 arguments
        Utils.sendHelpMessage(sender);
        return false;
    }


}
