package fr.hegsis.spawnerpickaxe.listeners;

import fr.hegsis.spawnerpickaxe.Main;
import fr.hegsis.spawnerpickaxe.SpawnerPickaxe;
import fr.hegsis.spawnerpickaxe.utils.GiveItems;
import fr.hegsis.spawnerpickaxe.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;


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

}
