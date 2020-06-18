package fr.hegsis.spawnerpickaxe.utils;

import fr.hegsis.spawnerpickaxe.Main;
import fr.hegsis.spawnerpickaxe.manager.Option;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Utils {

    // Retourne si le joueur à une permission passé en paramètre
    public static boolean hasPermission(@NotNull Player p, String path, @NotNull Main main) {
        return (p.hasPermission(main.getConfig().getString("permissions.all")) || p.hasPermission(main.getConfig().getString("permissions."+path)));
    }

    // Fonction permetttant d'envoyer un message (contenu dans le fichier config.yml) à un joueur
    public static void sendMessage(@NotNull Player p, String path, @NotNull Main main) {
        p.sendMessage(main.getConfig().getString("messages."+path).replaceAll("&", "§"));
    }

    // Fonction qui retourne un des messages du fichier de configuration
    @NotNull
    public static String getConfigMessage(String path, @NotNull Main main) {
        return main.getConfig().getString("messages."+path).replaceAll("&", "§");
    }

    //Fonction qui permet de jouer un son
    public static void playSound(Player p, String sound, @NotNull Main main) {
        String[] use = main.getConfig().getString("sounds."+sound).split(";");
        Sound s = null;
        try {
            s = Sound.valueOf(use[0]);
        } catch (Exception e) {
            if (hasPermission(p, "show-errors", main)) {
                p.sendMessage(getConfigMessage("incorrect-sound", main).replaceAll("%sound%", sound));
            }
        }

        if (use[1].equalsIgnoreCase("true") && s != null) {
            p.playSound(p.getLocation(), s, 1F, 1F);
        }
    }

    // Permet de retourner un nombre
    public static int isNumber(String entier) {
        try {
            return Integer.parseInt(entier);
        } catch (Exception e) {
            return -1;
        }
    }

    // Permet de recupérer une tête
    public static ItemStack playerHead(String owner, String name, String otherItem, @NotNull Main main) {
        ItemStack it;
        if (main.optionsUsed.get(Option.PLAYER_HEAD_IN_MENU)) {
            it = new ItemStack(main.getPlayerHeadItem(), 1, (short) 3);
            SkullMeta sm = (SkullMeta) it.getItemMeta();
            sm.setOwner(owner);
            sm.setDisplayName(name.replaceAll("&","§"));
            it.setItemMeta(sm);
        } else {
            it = new ItemStack(Material.getMaterial(otherItem.toUpperCase()));
            ItemMeta im = it.getItemMeta();
            im.setDisplayName(name.replaceAll("&", "§"));
            it.setItemMeta(im);
        }
        return it;
    }

    // Fonction qui retourne une liste de String qui ne contient plus de caractères &
    @Contract("_ -> param1")
    public static List<String> convertListColorCode(@NotNull List<String> lore) {
        for (int i=0; i<lore.size(); i++) {
            lore.set(i, lore.get(i).replaceAll("&", "§"));
        }
        return lore;
    }

    //Fonction qui permet de récupérer le prix, la durabilité ou autre sur un objet
    public static String getValue(String search, String configVersion, Boolean getInConfig, Main main) {

        int start = 0, end = 0;

        if (getInConfig) {
            configVersion = main.getConfig().getString(configVersion).replaceAll("&", "§");
        }

        configVersion = ChatColor.stripColor(configVersion);
        search = ChatColor.stripColor(search);

        try {
            start = configVersion.indexOf('%');
            end = configVersion.indexOf('%', start + 1);

            if (configVersion.length() == end + 1) {
                end = search.length();
            } else {
                end = search.indexOf(configVersion.charAt(end + 1), start+1);
            }

            search = search.substring(start, end);
        } catch (Exception e) {
            main.getLogger().severe(search);
            main.getLogger().severe(e.toString());
            search = "";
        }

        return search;
    }

    // Fonction qui permet de vérifier si l'item est bien un item
    public static void isMaterial(String material, Material mat, Main main) {
        try {
            if (mat != null) return;
        } catch (NullPointerException e) { }
        main.getServer().getConsoleSender().sendMessage("§4Item §c" + material + " §4isn't valid !");
    }

    // Fonction qui permet d'envoyer un message à un utilisateur
    public static void sendHelpMessage(CommandSender sender) {
        Main main = Main.getInstance();

        if (sender instanceof Player) {
            Player p = (Player) sender;

            //Si le joueur n'a pas la permission
            if (!Utils.hasPermission(p, "spawnerpickaxe-help", main)) {
                Utils.sendMessage(p, "no-permission", main);
                return;
            }
        }

        sender.sendMessage("§7§m---------§6 SpawnerPickaxe Help §7§m---------");
        sender.sendMessage("");
        sender.sendMessage("§8• §6/ps §7(player) (durability) §f→ §eGive a Spawner Pickaxe");
        sender.sendMessage("§8• §6/ps shop §f→ §eOpen Spawner Pickaxe Shop");
        sender.sendMessage("§8• §6/ps fusion §f→ §eMerge pickaxes");
        sender.sendMessage("§8• §6/ps gui/manage §f→ §eOpen Spawner Pickaxe Manager");
        sender.sendMessage("§8• §6/ssp §7(player) (durability) §f→ §eGive a Super Spawner Pickaxe");
        sender.sendMessage("§8• §6/ssp fusion §f→ §eMerge pickaxes");
        sender.sendMessage("§8• §6/spawner list §f→ §eList of spawners");
        sender.sendMessage("§8• §6/spawner [entity] §7(player) (amount) §f→ §eGive a spawner");
        sender.sendMessage("§8• §6/entity unlocklist §f→ §eList of entities");
        sender.sendMessage("§8• §6/entity locklist [entity] §f→ §eList of locked entites");
        sender.sendMessage("§8• §6/entity lock [entity] §f→ §eLock an entity");
        sender.sendMessage("§8• §6/entity unlock [entity] §f→ §eUnlock an entity");
        sender.sendMessage("§8• §6/entity setname [entity] [name] §f→ §eChange an entity name");
        sender.sendMessage("§8• §6/entity reset §f→ §eReset all entities names");
        sender.sendMessage("");
        sender.sendMessage("§7§m---------§6 SpawnerPickaxe Help §7§m---------");
    }
}
