package fr.hegsis.spawnerpickaxe.listeners;

import fr.hegsis.spawnerpickaxe.Main;
import fr.hegsis.spawnerpickaxe.utils.GiveItems;
import fr.hegsis.spawnerpickaxe.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
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
        /*if (e.getView().getTitle().equalsIgnoreCase(main.getConfig().getString("shop.inventory_name").replace("&", "§"))) {
            if (e.getClickedInventory() != null && e.getClickedInventory() != p.getInventory()) {
                e.setCancelled(true);

                String pickaxe_description = main.getPickaxeDescription();
                int durability, price, new_durability, new_price;
                if (click_item == null) { return; }
                if (click_item.getType() == Material.GREEN_STAINED_GLASS_PANE || click_item.getType() == Material.RED_STAINED_GLASS_PANE) {
                    main.playSound(p, "on_inventory_click");

                    durability = main.getPickaxeDurability(e.getInventory().getItem(13));
                    price = main.getPickaxePrice();

                    new_durability = durability + main.getInt(ChatColor.stripColor(click_item.getItemMeta().getDisplayName()));
                    if (new_durability < 1) {
                        new_durability = 1;
                        main.sendMessage(p, "inferior_zero");
                    }

                    new_price = new_durability * price;

                    //On update la pioche du shop
                    ItemStack is = new ItemStack(main.getPickaxeItem());
                    ItemMeta im = is.getItemMeta();
                    im.setDisplayName(main.getPickaxeName());
                    im.addEnchant(Enchantment.DURABILITY, 3, true);
                    im.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                    im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    List<String> lore = new ArrayList<>();
                    lore.add(pickaxe_description.replaceAll("%durability%", ""+new_durability));
                    im.setLore(lore);
                    is.setItemMeta(im);
                    e.getInventory().setItem(13, is);

                    //On update le papier qui indique le prix
                    is = main.playerHead("MHF_QUESTION", main.getConfig().getString("shop.price_item").replace("&", "§").replaceAll("%price%",""+new_price), "PAPER");
                    e.getInventory().setItem(22, is);

                    p.updateInventory();
                }

                if (click_item.getType() == Material.GREEN_TERRACOTTA || click_item.getType() == Material.RED_TERRACOTTA) {
                    main.playSound(p, "on_inventory_click");

                    //Si le joueur clique sur quitter
                    if (click_item.getItemMeta().getDisplayName().equalsIgnoreCase(main.getConfig().getString("shop.quit_item").replace("&", "§"))) {
                        p.closeInventory();
                    }

                    if (click_item.getItemMeta().getDisplayName().equalsIgnoreCase(main.getConfig().getString("shop.accept_item").replace("&", "§"))) {

                        try {
                            price = main.getInt(main.getValue(e.getInventory().getItem(22).getItemMeta().getDisplayName(), "shop.price_item", true));
                            durability = main.getPickaxeDurability(e.getInventory().getItem(13));
                        } catch (NumberFormatException nfe) {
                            main.getLogger().severe(nfe.toString());
                            p.closeInventory();
                            return;
                        }

                        if (p.getInventory().firstEmpty() != -1) {
                            if (main.economy.getBalance(p.getName()) >= (double)price) {
                                main.givePickaxe(p, ""+durability);
                                p.updateInventory();
                                p.closeInventory();
                                main.economy.withdrawPlayer(p.getName(), (double)price);
                                main.sendMessage(p, "buy_pickaxe");
                            } else {
                                main.sendMessage(p, "buy_pickaxe_fail");
                            }
                        } else {
                            main.sendMessage(p, "full_inventory");
                        }
                    }
                }
            }

            if (e.getClickedInventory() == p.getInventory()) {
                e.setCancelled(true);
            }
        }*/

    }

}
