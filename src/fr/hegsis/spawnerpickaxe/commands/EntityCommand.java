package fr.hegsis.spawnerpickaxe.commands;

import fr.hegsis.spawnerpickaxe.Main;
import fr.hegsis.spawnerpickaxe.utils.Entities;
import fr.hegsis.spawnerpickaxe.utils.Inventories;
import fr.hegsis.spawnerpickaxe.utils.Utils;
import fr.hegsis.spawnerpickaxe.utils.file.yaml.EntityFileUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class EntityCommand implements CommandExecutor {

    private Main main;
    public EntityCommand(Main main) {
        this.main = main;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        // Commande /spawner ou /spawner list
        if (args.length == 1) {

            if (sender instanceof Player) { // Si celui qui fait la commande est un joueur
                Player p = (Player) sender;
                if (!Utils.hasPermission(p, "spawner-list", main)) {
                    Utils.sendMessage(p, "no-permission", main);
                    return false;
                }
            }

            // Si la commande est /entity unlocklist
            if (args[0].equalsIgnoreCase("unlocklist")) {
                sender.sendMessage(main.entityListString);
                return true;
            }

            // Si la commande est /entity locklist
            if (args[0].equalsIgnoreCase("locklist")) {
                sender.sendMessage(main.deleteEntitiesListString);
                return true;
            }

            // Si la commande est /entity reset
            if (args[0].equalsIgnoreCase(("reset"))) {
                EntityFileUtils.addAllEntitiesOnYaml();
                return true;
            }
        }

        if (args.length > 1) {
            EntityType entity;

            // On check si l'entitée écrit par le joueur existe
            try {
                entity = EntityType.valueOf(args[1].toUpperCase());
            } catch (Exception e) {
                sender.sendMessage(Utils.getConfigMessage("spawner-not-found", main).replaceAll("%entity%", args[0].toUpperCase()));
                return false;
            }

            // Si l'entité n'est dans aucune des deux listes
            if (!main.entityList.contains(entity) && !main.deleteEntities.contains(entity)) {
                sender.sendMessage(Utils.getConfigMessage("spawner-not-found", main).replaceAll("%entity%", entity.toString()));
                return false;
            }

            if (args.length == 2) {
                // Si la commande est /entity lock [entity]
                if (args[0].equalsIgnoreCase("lock")) {
                    if (!main.entityList.contains(entity)) {
                        sender.sendMessage(Utils.getConfigMessage("entity-already-lock", main).replaceAll("%entity%", entity.toString()));
                        return false;
                    }

                    main.entityList.remove(entity);
                    main.deleteEntities.add(entity);
                    EntityFileUtils.removeEntity(entity);
                    resetMenuAndList();
                    sender.sendMessage(Utils.getConfigMessage("entity-lock", main).replaceAll("%entity%", entity.toString()));
                    return true;
                }

                // Si la commande est /entity unlock [entity]
                if (args[0].equalsIgnoreCase("unlock")) {
                    if (!main.deleteEntities.contains(entity)) {
                        sender.sendMessage(Utils.getConfigMessage("entity-already-unlock", main).replaceAll("%entity%", entity.toString()));
                        return false;
                    }

                    main.deleteEntities.remove(entity);
                    main.entityList.add(entity);
                    EntityFileUtils.addEntity(entity);
                    resetMenuAndList();
                    sender.sendMessage(Utils.getConfigMessage("entity-unlock", main).replaceAll("%entity%", entity.toString()));
                    return true;
                }
            }

            // Si la coommande est /entity setname [entity] [name]
            if (args[0].equalsIgnoreCase("setname")) {
                if (args.length == 2) {
                    sender.sendMessage(Utils.getConfigMessage("entity-name-needed", main));
                    return false;
                }

                StringBuilder newName = new StringBuilder();
                for (int i=2; i<args.length; i++) {
                    newName.append(args[i]).append(" ");
                }
                EntityFileUtils.setEntityName(entity, newName.toString().substring(0, newName.length()-1));
                sender.sendMessage(Utils.getConfigMessage("entity-new-name", main).replaceAll("%entity%", entity.toString()).replaceAll("%name%", newName.toString().replaceAll("&", "§").substring(0, newName.length()-1)));
                resetMenuAndList();
                return true;
            }
        }

        Utils.sendHelpMessage(sender);
        return false;
    }

    private void resetMenuAndList() {
        main.deleteEntitiesListString = Entities.convertEntityListToString(main.deleteEntities);
        main.entityListString = Entities.convertEntityListToString(main.entityList);
        Inventories.setEntityInventoryList(main);
        main.entityMapName = EntityFileUtils.setEntityMapName();
        List<String> entities = new ArrayList<>();
        for (EntityType et : main.deleteEntities) {
            entities.add(et.toString());
        }
        main.getConfig().set("disabled-mob", entities);
        main.saveConfig();
        main.reloadConfig();
    }
}
