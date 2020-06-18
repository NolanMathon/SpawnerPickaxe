package fr.hegsis.spawnerpickaxe.utils.file.yaml;

import fr.hegsis.spawnerpickaxe.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

public class EntityFileUtils {

    public static void addAllEntitiesOnYaml() {
        Main main = Main.getInstance();
        if (main.entityList.size() > 0) {
            FileConfiguration fc = YamlFileUtils.getFileConfiguration("entity");
            for (EntityType et : main.entityList) {
                fc.set(et.toString(), "&5" + et.toString().substring(0, 1) + et.toString().substring(1).toLowerCase()); // On met en majuscule uniquement le premier caractère
            }
            YamlFileUtils.saveFile(fc, "entity");
        }
    }

    public static Map<EntityType, String> setEntityMapName() {
        Main main = Main.getInstance();
        Map<EntityType, String> entityListName = new HashMap<>();
        if (main.entityList.size() > 0) {
            FileConfiguration fc = YamlFileUtils.getFileConfiguration("entity");
            for (EntityType et : main.entityList) {
                if (fc.contains(et.toString())) {
                    entityListName.put(et, fc.getString(et.toString()).replaceAll("&", "§"));
                }
            }
        }
        return entityListName;
    }

    public static void addEntity(EntityType entityType) {
        FileConfiguration fc = YamlFileUtils.getFileConfiguration("entity");
        if (!fc.contains(entityType.toString())) {
            // On met en majuscule uniquement le premier caractère
            fc.set(entityType.toString(), "&5" + entityType.toString().substring(0, 1) + entityType.toString().substring(1).toLowerCase());
        }
        YamlFileUtils.saveFile(fc, "entity");
    }

    public static void removeEntity(EntityType entityType) {
        FileConfiguration fc = YamlFileUtils.getFileConfiguration("entity");
        if (fc.contains(entityType.toString())) {
            fc.set(entityType.toString(), null);
        }
        YamlFileUtils.saveFile(fc, "entity");
    }

    public static void setEntityName(EntityType entityType, String name) {
        FileConfiguration fc = YamlFileUtils.getFileConfiguration("entity");
        fc.set(entityType.toString(), name);
        YamlFileUtils.saveFile(fc, "entity");
    }
}
