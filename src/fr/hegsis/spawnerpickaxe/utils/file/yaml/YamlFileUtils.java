package fr.hegsis.spawnerpickaxe.utils.file.yaml;

import fr.hegsis.spawnerpickaxe.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class YamlFileUtils {

    public static void createFile(String filename) {
        File file = getFile(filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean fileExist(String filename) {
        return getFile(filename).exists();
    }

    public static File getFile(String filename) {
        return new File(Main.getInstance().getDataFolder() + File.separator + filename + ".yml");
    }

    public static FileConfiguration getFileConfiguration(String filename) {
        return YamlConfiguration.loadConfiguration(getFile(filename));
    }

    public static void saveFile(FileConfiguration fileConfiguration, String filename) {
        try {
            fileConfiguration.save(getFile(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
