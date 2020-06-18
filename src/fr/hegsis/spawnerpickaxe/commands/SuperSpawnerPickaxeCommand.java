package fr.hegsis.spawnerpickaxe.commands;

import fr.hegsis.spawnerpickaxe.Main;
import fr.hegsis.spawnerpickaxe.manager.Option;
import fr.hegsis.spawnerpickaxe.objects.SuperSpawnerPickaxe;
import fr.hegsis.spawnerpickaxe.utils.GiveItems;
import fr.hegsis.spawnerpickaxe.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SuperSpawnerPickaxeCommand implements CommandExecutor {

    private Main main;
    public SuperSpawnerPickaxeCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!main.optionsUsed.get(Option.SUPERSPAWNERPICKAXE)) {
            sender.sendMessage(Utils.getConfigMessage("option-disable", main).replaceAll("&", "§").replaceAll("%option%", Option.SUPERSPAWNERPICKAXE.toString().toLowerCase().replaceAll("_", " ")));
            return false;
        }

        //Si celui qui execute la commande est un joueur (et non la console)
        if (sender instanceof Player) {
            Player p = (Player) sender;

            //Commande /ssp - Donne une pioche de durabilité 1 au joueur qui à fait la commande
            if (args.length == 0) {
                //Si le joueur n'a pas la permission
                if (!Utils.hasPermission(p, "pickaxe-give", main)) {
                    Utils.sendMessage(p, "no-permission", main);
                    return false;
                }

                GiveItems.giveSuperPickaxe(p, 1, main);
                p.sendMessage(Utils.getConfigMessage("give-superpickaxe", main).replaceAll("%durability%", "1"));
                return true;
            }

            //Commande /ssp fusion - Permet de fusionner 1 ou plusieurs plusieurs pioches
            if (args.length == 1 && args[0].equalsIgnoreCase("fusion")) {

                //Si le joueur n'a pas la permission
                if (!Utils.hasPermission(p, "pickaxe-fusion", main)) {
                    Utils.sendMessage(p, "no-permission", main);
                    return false;
                }

                Inventory inv = p.getInventory();
                int durability = 0;
                int nbPickaxes = 0;

                //On check tout l'inventaire du joueur
                for (int i = 0; i < p.getInventory().getSize(); i++) {
                    if (p.getInventory().getItem(i) != null) {
                        ItemStack item = p.getInventory().getItem(i);
                        if (item.getType() == main.getSuperPickaxe().getType() && item.getItemMeta().hasDisplayName() && item.getItemMeta().hasLore()) {
                            if (item.getItemMeta().getDisplayName().equalsIgnoreCase(main.getSuperPickaxe().getItemMeta().getDisplayName())) {
                                SuperSpawnerPickaxe ssp = new SuperSpawnerPickaxe(item);
                                durability += ssp.getDurability(main);
                                p.getInventory().setItem(i, new ItemStack(Material.AIR));
                                nbPickaxes++;
                            }
                        }
                    }
                }

                if (nbPickaxes == 0) {
                    Utils.sendMessage(p, "no-pickaxe-on-inventory", main);
                    return false;
                }

                p.sendMessage(Utils.getConfigMessage("pickaxe-fusion", main).replaceAll("%pickaxes%", "" + nbPickaxes).replaceAll("%durability%", "" + durability));
                GiveItems.giveSuperPickaxe(p, durability, main);
                Utils.playSound(p, "on-fusion-spickaxe", main);
                return true;
            }

        } else {
            //Si la console fait la commande /ssp fusion
            if (args.length == 0 || (args.length == 1 && (args[0].equalsIgnoreCase("fusion")))) {
                sender.sendMessage(Utils.getConfigMessage("only-player", main));
            }
        }

        //Commande /ssp [player] (durability) - Permet de donner à un joueur une pioche avec 1 ou plus de durabilité
        if (args.length >= 1 && args.length <= 2 && Bukkit.getPlayer(args[0]) != null) {

            if (sender instanceof Player) {
                Player p = (Player) sender;

                //Si le joueur n'a pas la permission
                if (!Utils.hasPermission(p, "pickaxe-give", main)) {
                    Utils.sendMessage(p, "no-permission", main);
                    return false;
                }
            }

            Player target = Bukkit.getPlayer(args[0]);
            int dura = 1;
            if (args.length == 2) {
                dura = Utils.isNumber(args[1]);
                if (dura < 1) {
                    sender.sendMessage(Utils.getConfigMessage("inferior-zero", main));
                    return false;
                }
            }

            GiveItems.giveSuperPickaxe(target, dura, main);
            target.sendMessage(Utils.getConfigMessage("give-superpickaxe", main).replaceAll("%durability%", ""+dura));
            return true;
        }

        Utils.sendHelpMessage(sender);
        return false;
    }
}
