package fr.hegsis.spawnerpickaxe.objects;

import fr.hegsis.spawnerpickaxe.Main;
import fr.hegsis.spawnerpickaxe.utils.Utils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SuperSpawnerPickaxe {
    private ItemStack superPickaxe;
    private ItemMeta superPickaxeMeta;

    public SuperSpawnerPickaxe(@NotNull ItemStack pickaxe) {
        this.superPickaxe = pickaxe;
        this.superPickaxeMeta = pickaxe.getItemMeta();
        addEnchant();
    }

    public ItemStack getPickaxe() {
        return superPickaxe;
    }

    public void setPickaxe(ItemStack pickaxe) {
        this.superPickaxe = pickaxe;
        this.superPickaxeMeta = pickaxe.getItemMeta();
    }

    public void setDisplayName(String name) {
        superPickaxeMeta.setDisplayName(name);
        create();
    }

    public void setLore(List<String> lore) {
        superPickaxeMeta.setLore(lore);
        create();
    }

    public String getDisplayName() {
        return superPickaxeMeta.getDisplayName();
    }

    public List<String> getLore() {
        return superPickaxeMeta.getLore();
    }

    // Permet de récupérer la durabilitée de la pioche
    public int getDurability(Main main) {
        int durability;
        int duraLine = 0;

        List<String> originalLore = Utils.convertListColorCode(main.getConfig().getStringList("superpickaxe.description"));
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
        List<String> originalLore = Utils.convertListColorCode(main.getConfig().getStringList("superpickaxe.description"));
        setLore(originalLore);
        setDurability(durability + addDurability);
    }

    // Permet d'ajouter de la durabilité à la pioche
    public void removeDurability(int removeDurability, Main main) {
        int durability = getDurability(main);
        List<String> originalLore = Utils.convertListColorCode(main.getConfig().getStringList("superpickaxe.description"));
        setLore(originalLore);
        superPickaxeMeta.spigot().setUnbreakable(true);
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
        superPickaxe.setItemMeta(superPickaxeMeta);
    }

    public void give(Player p) {
        if (p.getInventory().firstEmpty() != -1) {
            p.getInventory().addItem(superPickaxe);
        } else {
            p.getLocation().getWorld().dropItemNaturally(p.getLocation(), superPickaxe);
        }
    }

    public void addEnchant() {
        //superPickaxeMeta.spigot().setUnbreakable(true);
        superPickaxeMeta.addEnchant(Enchantment.DURABILITY, 3, true);
        superPickaxeMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        superPickaxeMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        superPickaxeMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        create();
    }
}
