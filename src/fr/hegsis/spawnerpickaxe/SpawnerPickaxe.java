package fr.hegsis.spawnerpickaxe;

import fr.hegsis.spawnerpickaxe.utils.Utils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class SpawnerPickaxe {
    private ItemStack pickaxe;
    private ItemMeta pickaxeMeta;

    public SpawnerPickaxe(ItemStack pickaxe) {
        this.pickaxe = pickaxe;
        this.pickaxeMeta = pickaxe.getItemMeta();
        addEnchant();
    }

    public ItemStack getPickaxe() {
        return pickaxe;
    }

    public void setPickaxe(ItemStack pickaxe) {
        this.pickaxe = pickaxe;
        this.pickaxeMeta = pickaxe.getItemMeta();
    }

    public void setDisplayName(String name) {
        pickaxeMeta.setDisplayName(name);
        create();
    }

    public void setLore(List<String> lore) {
        pickaxeMeta.setLore(lore);
        create();
    }

    public String getDisplayName() {
        return pickaxeMeta.getDisplayName();
    }

    public List<String> getLore() {
        return pickaxeMeta.getLore();
    }

    // Permet de récupérer la durabilitée de la pioche
    public int getDurability(Main main) {
        int durability;
        int duraLine = 0;

        List<String> originalLore = Utils.convertListColorCode(main.getConfig().getStringList("pickaxe.description"));
        for (int i=0; i<originalLore.size(); i++) {
            if (originalLore.get(i).contains("%durability%")) {
                duraLine = i;
                break;
            }
        }

        durability = Utils.isNumber(Utils.getValue(getLore().get(duraLine), originalLore.get(duraLine), false, main));
        return durability;
    }

    // Permet d'ajouter de la durabilité à la pioche
    public void addDurability(int addDurability, Main main) {
        int durability = getDurability(main);
        List<String> originalLore = Utils.convertListColorCode(main.getConfig().getStringList("pickaxe.description"));
        setLore(originalLore);
        setDurability(durability + addDurability);
    }

    // Permet d'ajouter de la durabilité à la pioche
    public void removeDurability(int removeDurability, Main main) {
        int durability = getDurability(main);
        List<String> originalLore = Utils.convertListColorCode(main.getConfig().getStringList("pickaxe.description"));
        setLore(originalLore);
        pickaxeMeta.spigot().setUnbreakable(true);
        create();
        setDurability(durability - removeDurability);
    }

    public void setDurability(int durability) {
        List<String> lore = getLore();
        for (int i=0; i<lore.size(); i++) {
            lore.set(i, lore.get(i).replaceAll("%durability%", ""+durability));
        }
        setLore(lore);
    }

    private void create() {
        pickaxe.setItemMeta(pickaxeMeta);
    }

    public void give(Player p) {
        if (p.getInventory().firstEmpty() != -1) {
            p.getInventory().addItem(pickaxe);
        } else {
            p.getLocation().getWorld().dropItemNaturally(p.getLocation(), pickaxe);
        }
    }

    public void addEnchant() {
        pickaxeMeta.spigot().setUnbreakable(true);
        pickaxeMeta.addEnchant(Enchantment.DURABILITY, 3, true);
        pickaxeMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        pickaxeMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        pickaxeMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        create();
    }
}
