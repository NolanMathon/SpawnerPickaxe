package fr.hegsis.spawnerpickaxe.listeners;

import fr.hegsis.spawnerpickaxe.Main;
import fr.hegsis.spawnerpickaxe.SpawnerPickaxe;
import fr.hegsis.spawnerpickaxe.manager.ManagerMain;
import fr.hegsis.spawnerpickaxe.manager.Option;
import fr.hegsis.spawnerpickaxe.utils.GiveItems;
import fr.hegsis.spawnerpickaxe.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


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

            // Si le joueur clique sur l'utilisation du plugin faction
            if (clickItem.getType() == manageMenu.getItem(10).getType() && clickItem.getItemMeta().getDisplayName().equalsIgnoreCase(manageMenu.getItem(10).getItemMeta().getDisplayName())) {
                switchOptionMode(Option.FACTION, e.getClick(), 10, e.getInventory(), p);
                return;
            }

            // Si le joueur clique sur l'utilisation de la superspawnerpickaxe
            if (clickItem.getType() == manageMenu.getItem(11).getType() && clickItem.getItemMeta().getDisplayName().equalsIgnoreCase(manageMenu.getItem(11).getItemMeta().getDisplayName())) {
                switchOptionMode(Option.SUPERSPAWNERPICKAXE, e.getClick(), 11, e.getInventory(), p);
                return;
            }

            // Si le joueur clique sur l'utilisation de la tnt qui casse les spawners
            if (clickItem.getType() == manageMenu.getItem(12).getType() && clickItem.getItemMeta().getDisplayName().equalsIgnoreCase(manageMenu.getItem(12).getItemMeta().getDisplayName())) {
                switchOptionMode(Option.TNT_BREAK_SPAWNER, e.getClick(), 12, e.getInventory(), p);
                return;
            }

            // Si le joueur clique sur l'utilisation des têtes de joueurs dans les menus
            if (clickItem.getType() == manageMenu.getItem(13).getType() && clickItem.getItemMeta().getDisplayName().equalsIgnoreCase(manageMenu.getItem(13).getItemMeta().getDisplayName())) {
                switchOptionMode(Option.PLAYER_HEAD_IN_MENU, e.getClick(), 13, e.getInventory(), p);
                return;
            }

            // Si le joueur clique sur l'utilisation d'un menu quand tu cliques sur un spawner
            if (clickItem.getType() == manageMenu.getItem(14).getType() && clickItem.getItemMeta().getDisplayName().equalsIgnoreCase(manageMenu.getItem(14).getItemMeta().getDisplayName())) {
                switchOptionMode(Option.RIGHT_CLICK_SPAWNER_MENU, e.getClick(), 14, e.getInventory(), p);
                return;
            }

            // Si le joueur clique sur l'utilisation du shop de spawnerpickaxe
            if (clickItem.getType() == manageMenu.getItem(15).getType() && clickItem.getItemMeta().getDisplayName().equalsIgnoreCase(manageMenu.getItem(15).getItemMeta().getDisplayName())) {
                switchOptionMode(Option.SPAWNERPICKAXE_SHOP, e.getClick(), 15, e.getInventory(), p);
                return;
            }

            // Si le joueur clique sur l'utilisation du shop via des panneaux
            if (clickItem.getType() == manageMenu.getItem(16).getType() && clickItem.getItemMeta().getDisplayName().equalsIgnoreCase(manageMenu.getItem(16).getItemMeta().getDisplayName())) {
                switchOptionMode(Option.SPAWNERPICKAXE_SIGN, e.getClick(), 16, e.getInventory(), p);
                return;
            }

            // Si le joueur clique sur l'utilisation du menu des spawners (/spawner)
            if (clickItem.getType() == manageMenu.getItem(21).getType() && clickItem.getItemMeta().getDisplayName().equalsIgnoreCase(manageMenu.getItem(21).getItemMeta().getDisplayName())) {
                switchOptionMode(Option.SPAWNERLIST_MENU, e.getClick(), 21, e.getInventory(), p);
                return;
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

        main.optionsUsed.replace(option, newStatus);
        main.getConfig().set("use."+option.toString().toLowerCase(), newStatus);
        main.saveConfig();

        if (option == Option.PLAYER_HEAD_IN_MENU) {
            p.closeInventory();
            main.setAllDefaultInventoriesAndEntities();
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
