package fr.hegsis.spawnerpickaxe.utils;

import fr.hegsis.spawnerpickaxe.Main;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

public class NBT {

    private static final String version = "net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    private static final String cbVersion = "org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    private static Class<?> tagCompoundClass;
    private static Class<?> nmsItemstackClass;
    private static Class<?> craftItemstackClass;

    private final Object tagCompund;

    static {
        try {
            tagCompoundClass = Class.forName(version + ".NBTTagCompound");
            nmsItemstackClass = Class.forName(version + ".ItemStack");
            craftItemstackClass = Class.forName(cbVersion + ".inventory.CraftItemStack");
        } catch(Exception ex) {
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
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
        this.tagCompund = toSet;
    }

    public Object getTagCompund(){
        return tagCompund;
    }

    public Integer getInt(String key){
        try{
            Method m = tagCompoundClass.getMethod("getInt", String.class);
            m.setAccessible(true);
            Object r = m.invoke(this.tagCompund, key);
            m.setAccessible(false);
            return r instanceof Integer ? (Integer) r : null;
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void setInt(String key, Integer value){
        try{
            Method m = tagCompoundClass.getMethod("setInt", String.class, int.class);
            m.setAccessible(true);
            m.invoke(this.tagCompund, key, value);
            m.setAccessible(false);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getString(String key){
        try {
            Method m = tagCompoundClass.getMethod("getString", String.class);
            m.setAccessible(true);
            Object r = m.invoke(this.tagCompund, key);
            m.setAccessible(false);
            return r instanceof String ? (String) r : null;
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void setString(String key, String value){
        try {
            Method m = tagCompoundClass.getMethod("setString", String.class, String.class);
            m.setAccessible(true);
            m.invoke(this.tagCompund, key, value);
            m.setAccessible(false);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean hasKey(String key){
        try {
            Method m = tagCompoundClass.getMethod("hasKey", String.class);
            m.setAccessible(true);
            Object o = m.invoke(this.tagCompund, key);
            m.setAccessible(false);

            return o instanceof Boolean && (Boolean) o;
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public ItemStack apply(ItemStack item){
        try{
            Method nmsGet = craftItemstackClass.getMethod("asNMSCopy", ItemStack.class);
            nmsGet.setAccessible(true);
            Object nmsStack = nmsGet.invoke(null, item);
            nmsGet.setAccessible(false);

            Method nbtSet = nmsItemstackClass.getMethod("setTag", tagCompoundClass);
            nbtSet.setAccessible(true);
            nbtSet.invoke(nmsStack, this.tagCompund);
            nbtSet.setAccessible(false);

            Method m = craftItemstackClass.getMethod("asBukkitCopy", nmsItemstackClass);
            m.setAccessible(true);
            Object o = m.invoke(null, nmsStack);
            m.setAccessible(false);

            return o instanceof ItemStack ? (ItemStack) o : null;
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static NBT get(ItemStack item){
        try{
            Method m = craftItemstackClass.getMethod("asNMSCopy", ItemStack.class);
            m.setAccessible(true);
            Object nmsStack = m.invoke(null, item);
            m.setAccessible(false);

            Method getCompound = nmsItemstackClass.getMethod("getTag");
            getCompound.setAccessible(true);
            Object nbtCompound = getCompound.invoke(nmsStack);
            getCompound.setAccessible(false);

            return new NBT(nbtCompound);
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
