package fr.hegsis.spawnerpickaxe;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class SpawnerPickaxe {
    private ItemStack pickaxe;
    private ItemMeta pickaxeMeta;

    public SpawnerPickaxe(ItemStack pickaxe) {
        this.pickaxe = pickaxe;
        this.pickaxeMeta = pickaxe.getItemMeta();
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

    public int getDurability() {
        int durability = 1;

        return durability;
    }

    public void setDurability(int durability) {

    }

    private void create() {
        pickaxe.setItemMeta(pickaxeMeta);
    }
}
