package fr.hegsis.spawnerpickaxe.utils;

import fr.hegsis.spawnerpickaxe.Main;
import org.bukkit.Bukkit;

public class NBT {

    private static final String version = "net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    private static final String cbVersion = "org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    private static Class<?> tagCompoundClass;
    private static Class<?> nbtBaseClass;
    private static Class<?> nmsItemstackClass;
    private static Class<?> craftItemstackClass;

    private final Object tagCompund;

    static{
        try{
            tagCompoundClass = Class.forName(version + ".NBTTagCompound");
            nbtBaseClass = Class.forName(version + ".NBTBase");
            nmsItemstackClass = Class.forName(version + ".ItemStack");
            craftItemstackClass = Class.forName(cbVersion + ".inventory.CraftItemStack");
        }
        catch(Exception ex){
            ex.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(Main.getInstance());
        }
    }

    public NBT(){
        this(null);
    }

    public NBT(Object tagCompound){
        Object toSet = tagCompound;
        if(tagCompound == null){
            try{
                toSet = tagCompoundClass.newInstance();
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
        }
        this.tagCompund = toSet;
    }

    public Object getTagCompund(){
        return tagCompund;
    }
}
