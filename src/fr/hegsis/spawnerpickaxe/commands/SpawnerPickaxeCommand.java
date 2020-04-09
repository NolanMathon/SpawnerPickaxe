package fr.hegsis.spawnerpickaxe.commands;

import fr.hegsis.spawnerpickaxe.Main;
import fr.hegsis.spawnerpickaxe.SpawnerPickaxe;
import fr.hegsis.spawnerpickaxe.manager.ManagerMain;
import fr.hegsis.spawnerpickaxe.manager.Option;
import fr.hegsis.spawnerpickaxe.utils.GiveItems;
import fr.hegsis.spawnerpickaxe.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.text.NumberFormat;


public class SpawnerPickaxeCommand implements CommandExecutor {

    private Main main;

    public SpawnerPickaxeCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //Si celui qui execute la commande est un joueur (et non la console)
        if (sender instanceof Player) {
            Player p = (Player) sender;

            //Commande /sp - Donne une pioche de durabilité 1 au joueur qui à fait la commande
            if (args.length == 0) {
                //Si le joueur n'a pas la permission
                if (!Utils.hasPermission(p, "pickaxe-give", main)) {
                    Utils.sendMessage(p, "no-permission", main);
                    return false;
                }

                GiveItems.givePickaxe(p, 1, main);
                p.sendMessage(Utils.getConfigMessage("give-pickaxe", main).replaceAll("%durability%", "1"));
                return true;
            }

            //Commande /sp fusion - Permet de fusionner 1 ou plusieurs plusieurs pioches
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
                        if (item.getType() == main.getPickaxe().getType() && item.getItemMeta().hasDisplayName() && item.getItemMeta().hasLore()) {
                            if (item.getItemMeta().getDisplayName().equalsIgnoreCase(main.getPickaxe().getItemMeta().getDisplayName())) {
                                SpawnerPickaxe sp = new SpawnerPickaxe(item);
                                durability += sp.getDurability(main);
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
                GiveItems.givePickaxe(p, durability, main);
                Utils.playSound(p, "on-fusion-spickaxe", main);
                return true;
            }

            //Commande /sp shop - Ouvre un inventaire pour acheter des pioches
            if (args.length == 1 && args[0].equalsIgnoreCase("shop")) {

                //Si le joueur n'a pas la permission
                if (!Utils.hasPermission(p, "pickaxe-shop", main)) {
                    Utils.sendMessage(p, "no-permission", main);
                    return false;
                }

                OfflinePlayer of = Bukkit.getOfflinePlayer(p.getUniqueId());
                Double money = main.economy.getBalance(of);
                NumberFormat nf = new DecimalFormat("0.##");
                String s = nf.format(money);
                Inventory inv = Bukkit.createInventory(null, 27, main.getConfig().getString("shop-inventory.name").replaceAll("&", "§"));
                inv.setContents(main.shopInventory.getContents().clone());

                ItemStack skull = Utils.playerHead(p.getName(), main.getConfig().getString("shop-inventory.money-item").replace("&", "§").replaceAll("%money%", ""+s), main.getPlayerHeadItem().toString(), main);
                inv.setItem(4, skull);
                p.openInventory(inv);
                Utils.playSound(p, "on-open-spickaxe-shop", main);
                return true;
            }

            if (args.length == 1 && (args[0].equalsIgnoreCase("manage") || args[0].equalsIgnoreCase("gui"))) {
                Inventory inv = Bukkit.createInventory(null, 36, main.manageInventory.getTitle());
                inv.setContents(main.manageInventory.getContents().clone());
                inv.setItem(13, Utils.playerHead(p.getName(), ManagerMain.getConfigName("player-head-in-menu", main.optionsUsed.get(Option.PLAYER_HEAD_IN_MENU), main), main.getPlayerHeadItem().toString(), main));
                p.openInventory(inv);
                return true;
            }

        } else {
            //Si la console fait la commande /sp fusion ou /sp shop
            if (args.length == 0 || (args.length == 1 && (args[0].equalsIgnoreCase("fusion") || args[0].equalsIgnoreCase("shop") || args[0].equalsIgnoreCase("manage") || args[0].equalsIgnoreCase("gui")))) {
                sender.sendMessage(Utils.getConfigMessage("only-player", main));
            }
        }

        //Commande /sp [player] (durability) - Permet de donner à un joueur une pioche avec 1 ou plus de durabilité
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

            GiveItems.givePickaxe(target, dura, main);
            target.sendMessage(Utils.getConfigMessage("give-pickaxe", main).replaceAll("%durability%", "1"));
            return true;
        }


        if (sender instanceof Player) {
            Player p = (Player) sender;

            //Si le joueur n'a pas la permission
            if (!Utils.hasPermission(p, "spawnerpickaxe-help", main)) {
                Utils.sendMessage(p, "no-permission", main);
                return false;
            }
        }

        sender.sendMessage("§7§m---------§6 SpawnerPickaxe Help §7§m---------");
        sender.sendMessage("");
        sender.sendMessage("§8• §e/sp §7(player) (durability) §f→ §eGive a Spawner Pickaxe");
        sender.sendMessage("§8• §e/sp shop §f→ §eOpen Spawner Pickaxe Shop");
        sender.sendMessage("§8• §e/sp reload §f→ §eReload config file");
        sender.sendMessage("§8• §e/spawner list §f→ §eList of spawners");
        sender.sendMessage("§8• §e/spawner [entity] (player) §7(amount) §f→ §eGive a spawner");
        sender.sendMessage("");
        sender.sendMessage("§7§m---------§6 SpawnerPickaxe Help §7§m---------");

        return true;
    }
}
