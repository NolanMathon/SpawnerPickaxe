package fr.hegsis.spawnerpickaxe.listeners;

import fr.hegsis.spawnerpickaxe.Main;
import fr.hegsis.spawnerpickaxe.objects.SpawnerPickaxe;
import fr.hegsis.spawnerpickaxe.manager.Option;
import fr.hegsis.spawnerpickaxe.utils.GiveItems;
import fr.hegsis.spawnerpickaxe.utils.Utils;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;


public class InventoriesListeners implements Listener {

    private Main main;

    public InventoriesListeners(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;

        Player p = (Player)e.getWhoClicked();
        ItemStack clickItem = e.getCurrentItem();

        /*****************************************************************
         *
         *                  RIGHT CLICK SPAWNER MENU
         *
         *****************************************************************/

        if (e.getView().getTitle().equalsIgnoreCase(main.getConfig().getString("spawner-menu.name").replaceAll("&", "§"))) {
            e.setCancelled(true);

            if (e.getClickedInventory() == p.getInventory()) return;
            if (clickItem == null) return;
            if (!clickItem.hasItemMeta()) return;

            // Si l'item cliqué est celui qui permet d'acheter une pioche à spawner
            if (clickItem.getType() == main.getPickaxe().getType() && clickItem.getItemMeta().getDisplayName().equalsIgnoreCase(main.rightClickSpawnerInventory.getItem(12).getItemMeta().getDisplayName())) {
                GiveItems.payAndGiveSpawnerPickaxe(p, main.getConfig().getInt("spawner-menu.get-spawner.price"), 1, main);
                p.closeInventory();
                return;
            }

            // Si l'item cliqué est celui qui permet de détruire le spawner
            if (clickItem.getType() == main.rightClickSpawnerInventory.getItem(14).getType() && clickItem.getItemMeta().getDisplayName().equalsIgnoreCase(main.rightClickSpawnerInventory.getItem(14).getItemMeta().getDisplayName())) {
                OfflinePlayer of = Bukkit.getOfflinePlayer(p.getUniqueId());
                double balance, price;
                balance = main.economy.getBalance(of);
                price = main.getConfig().getInt("spawner-menu.destroy-spawner.price");
                if (price > balance) {
                    Utils.sendMessage(p, "no-money-to-destroy", main);
                    p.closeInventory();
                    return;
                }

                main.economy.withdrawPlayer(of, price);
                Utils.sendMessage(p, "destroy-spawner", main);
                List<String> lore = e.getInventory().getItem(4).getItemMeta().getLore();
                Location loc = new Location(p.getWorld(), Utils.isNumber(lore.get(0)), Utils.isNumber(lore.get(1)), Utils.isNumber(lore.get(2)));
                loc.getBlock().setType(Material.AIR);
                p.closeInventory();
                return;
            }
        }


        /*****************************************************************
         *
         *                     SPAWNER LIST MENU
         *
         *****************************************************************/

        if (e.getView().getTitle().equalsIgnoreCase(main.getConfig().getString("spawner-inventory.name").replaceAll("&", "§"))) {
            e.setCancelled(true);

            if (e.getClickedInventory() == p.getInventory()) return;
            if (clickItem == null) return;

            if (clickItem.getType() == main.getSpawnerItem() || clickItem.getType() == main.getPlayerHeadItem() || clickItem.getType() == Material.BARRIER || clickItem.getType() == Material.ARROW) {

                if ((clickItem.getType() == main.getPlayerHeadItem() || clickItem.getType() == Material.BARRIER) && clickItem.getItemMeta().getDisplayName().equalsIgnoreCase(main.getConfig().getString("spawner-inventory.leave").replaceAll("&", "§"))) {
                    p.closeInventory();
                    return;
                }

                if (clickItem.getType() == main.getPlayerHeadItem() && clickItem.getItemMeta().getDisplayName().equalsIgnoreCase(main.spawnerInventory.getItem(53).getItemMeta().getDisplayName())) return;

                if ((clickItem.getType() == main.getPlayerHeadItem() || clickItem.getType() == Material.ARROW)
                        && (clickItem.getItemMeta().getDisplayName().equalsIgnoreCase(main.getConfig().getString("spawner-inventory.next-page").replaceAll("&", "§"))
                        || clickItem.getItemMeta().getDisplayName().equalsIgnoreCase(main.getConfig().getString("spawner-inventory.previous-page").replaceAll("&", "§")))) {
                    if (e.getInventory() == main.spawnerInventory) {
                        p.closeInventory();
                        p.openInventory(main.spawnerInventoryNext);
                    } else if (e.getInventory() == main.spawnerInventoryNext) {
                        p.closeInventory();
                        p.openInventory(main.spawnerInventory);
                    }
                } else {
                    Utils.playSound(p, "on-inventory-click", main);
                    GiveItems.giveSpawner(p, EntityType.valueOf(ChatColor.stripColor(clickItem.getItemMeta().getLore().get(0))), 1, false, main);
                    p.updateInventory();
                    return;
                }

            }
            return;
        }

        /*****************************************************************
         *
         *                     MANAGE MENU
         *
         *****************************************************************/

        if (e.getView().getTitle().equalsIgnoreCase(main.getConfig().getString("manage-gui.menu-name").replaceAll("&", "§"))) {
            e.setCancelled(true);

            if (e.getClickedInventory() == p.getInventory()) return;
            if (clickItem == null) return;

            Inventory manageMenu = main.manageInventory;

            // Si le joueur clique pour reload
            if (clickItem.getType() == manageMenu.getItem(23).getType() && clickItem.getItemMeta().getDisplayName().equalsIgnoreCase(manageMenu.getItem(23).getItemMeta().getDisplayName())) {
                main.reloadConfig();
                p.closeInventory();
                Utils.sendMessage(p, "reload-config-file", main);
                return;
            }

            // Si le joueur clique pour quitter
            if (clickItem.getType() == manageMenu.getItem(27).getType() && clickItem.getItemMeta().getDisplayName().equalsIgnoreCase(manageMenu.getItem(27).getItemMeta().getDisplayName())) {
                p.closeInventory();
                return;
            }

            if (e.getClick() != ClickType.LEFT && e.getClick() != ClickType.RIGHT) return;

            int[] emplacement = new int[] {10, 11, 12, 13, 14, 15, 16, 21};
            int i = 0;
            for (Option op : Option.values()) {
                if (clickItem.getType() == manageMenu.getItem(emplacement[i]).getType() && clickItem.getItemMeta().getDisplayName().equalsIgnoreCase(manageMenu.getItem(emplacement[i]).getItemMeta().getDisplayName())) {
                    switchOptionMode(op, e.getClick(), emplacement[i], e.getInventory(), p);
                    return;
                }
                i++;
            }

            return;
        }

        /*****************************************************************
        *
        *                   SPAWNER PICKAXE SHOP
        *
         ******************************************************************/

        if (e.getView().getTitle().equalsIgnoreCase(main.getConfig().getString("shop-inventory.name").replace("&", "§"))) {
            e.setCancelled(true);

            if (e.getClickedInventory() == p.getInventory()) return;
            if (clickItem == null) return;

            if (!clickItem.hasItemMeta()) return;

            int durability, price, newDurability, newPrice;

            if (clickItem.getType() == Material.getMaterial(main.getConfig().getString("shop-inventory.add-item.item")) || clickItem.getType() == Material.getMaterial(main.getConfig().getString("shop-inventory.remove-item.item"))) {
                Utils.playSound(p, "on-inventory-click", main);

                SpawnerPickaxe sp = new SpawnerPickaxe(e.getInventory().getItem(13));
                durability = sp.getDurability(main);
                price = main.getConfig().getInt("shop-inventory.price");

                newDurability = durability + Utils.isNumber(ChatColor.stripColor(clickItem.getItemMeta().getDisplayName()));
                if (newDurability < 1) {
                    newDurability = 1;
                    Utils.sendMessage(p, "inferior-zero", main);
                }

                newPrice = newDurability * price;

                //On update la pioche du shop
                SpawnerPickaxe newSp = new SpawnerPickaxe(main.getPickaxe().clone());
                newSp.setDurability(newDurability);
                e.getInventory().setItem(13, newSp.getPickaxe());

                //On update le papier qui indique le prix
                e.getInventory().setItem(22, Utils.playerHead("MHF_QUESTION", main.getConfig().getString("shop-inventory.price-item.name").replace("&", "§").replaceAll("%price%",""+newPrice), main.getConfig().getString("shop-inventory.price-item.item"), main));

                p.updateInventory();
            }

            if (clickItem.getType() == main.shopInventory.getItem(23).getType() || clickItem.getType() == main.shopInventory.getItem(21).getType()) {
                Utils.playSound(p, "on-inventory-click", main);

                // Si le joueur clique sur quitter
                if (clickItem.getItemMeta().getDisplayName().equalsIgnoreCase(main.getConfig().getString("shop-inventory.quit-item.name").replace("&", "§"))) {
                    p.closeInventory();
                    return;
                }

                // Si le joueur cliquer sur accepter
                if (clickItem.getItemMeta().getDisplayName().equalsIgnoreCase(main.getConfig().getString("shop-inventory.accept-item.name").replace("&", "§"))) {
                    try {
                        price = Utils.isNumber(Utils.getValue(e.getInventory().getItem(22).getItemMeta().getDisplayName(), "shop-inventory.price-item.name", true, main));
                        SpawnerPickaxe sp = new SpawnerPickaxe(e.getInventory().getItem(13));
                        durability = sp.getDurability(main);
                    } catch (NumberFormatException nfe) {
                        main.getLogger().severe(nfe.toString());
                        p.closeInventory();
                        return;
                    }

                    GiveItems.payAndGiveSpawnerPickaxe(p, price, durability, main);
                    p.closeInventory();
                    return;
                }
            }
        }
    }

    private void switchOptionMode(Option option, ClickType clickType, int emplacement, Inventory inv, Player p) {
        boolean newStatus = true;
        if (clickType == ClickType.LEFT) {
            newStatus = false;
        }

        // Si le status est le même que l'actuel
        if (main.optionsUsed.get(option) == newStatus) return;

        // Si on désactive l'option faction alors on désactive la super spawner pickaxe
        if (option == Option.FACTION && !newStatus) {
            if (main.optionsUsed.get(Option.SUPERSPAWNERPICKAXE)) {
                switchOptionMode (Option.SUPERSPAWNERPICKAXE, clickType, 11, inv, p);
            }
        }

        // Si on veut activé l'option super spawner pickaxe alors que le faction est désactivé
        if (option == Option.SUPERSPAWNERPICKAXE && newStatus && main.optionsUsed.get(Option.FACTION)) return;

        main.optionsUsed.replace(option, newStatus);
        main.getConfig().set("use."+option.toString().toLowerCase(), newStatus);
        main.saveConfig();
        main.reloadConfig();

        if (option == Option.PLAYER_HEAD_IN_MENU) {
            p.closeInventory();
            main.setDefaultInventories();
        } else {
            ItemStack it = inv.getItem(emplacement);
            ItemMeta im = it.getItemMeta();
            im.setDisplayName(main.getConfig().getString("manage-gui."+option.toString().toLowerCase()).replaceAll("&","§").replaceAll("%status%", main.getConfig().getString("manage-gui."+newStatus).replaceAll("&","§")));
            it.setItemMeta(im);
            inv.setItem(emplacement, it);
            main.manageInventory.setItem(emplacement, it);
            p.updateInventory();
        }

        p.sendMessage(Utils.getConfigMessage("option-set", main).replaceAll("&", "§").replaceAll("%option%", option.toString().toLowerCase().replaceAll("_", " ")).replaceAll("%status%", ""+newStatus));
    }

}
