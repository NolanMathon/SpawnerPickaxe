package fr.hegsis.spawnerpickaxe.utils;

import fr.hegsis.spawnerpickaxe.Main;
import fr.hegsis.spawnerpickaxe.utils.file.yaml.YamlFileUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Entities {

    private static List<EntityType> deleteEntities = new ArrayList<>();

    // Fonction qui permet de créer la liste des entités
    public static List<EntityType> setEntityList(Main main) {
        setDeleteEntities(main);
        List<EntityType> entityList = new ArrayList<>();
        for (EntityType entityType : EntityType.values()) {
            if (entityType.isSpawnable() && entityType.isAlive() && !deleteEntities.contains(entityType) && entityType != EntityType.ARMOR_STAND) { // Si l'entité peut apparaître et est vivante
                entityList.add(entityType);
            }
        }
        return entityList;
    }

    // Fonction qui permet de créer la liste des entités
    public static String setEntityListString(@NotNull List<EntityType> entityList) {
        String entityListString = "";
        for (int i = 0; i < entityList.size(); i++) {
            entityListString += "§7, §d" + entityList.get(i);
            // Pour le premier on ne met pas d'espace avant
            if (i==0) {
                entityListString = "§d" + entityList.get(i);
            }
        }

        return entityListString;
    }

    private static void setDeleteEntities(@NotNull Main main) throws EntityNotFoundException {
        for (String s : main.getConfig().getStringList("disabled-mob")) {
            deleteEntities.add(EntityType.valueOf(s));
        }
    }

    public static void addAllEntitiesOnYaml(Main main, String filename) {
        if (main.entityList.size() > 0) {
            FileConfiguration fc = YamlFileUtils.getFileConfiguration(filename);
            for (EntityType et : main.entityList) {
                fc.set(et.toString(), "&5" + et.toString().substring(0, 1) + et.toString().substring(1).toLowerCase()); // On met en majuscule uniquement le premier caractère
            }
            YamlFileUtils.saveFile(fc, filename);
        }
    }

    public static Map<EntityType, String> setEntityMapName(String filename) {
        Main main = Main.getInstance();
        Map<EntityType, String> entityListName = new HashMap<>();
        if (main.entityList.size() > 0) {
            FileConfiguration fc = YamlFileUtils.getFileConfiguration(filename);
            for (EntityType et : main.entityList) {
                if (fc.contains(et.toString())) {
                    entityListName.put(et, fc.getString(et.toString()).replaceAll("&", "§"));
                }
            }
        }
        return entityListName;
    }

    public static String getEntityName(EntityType entityType) {
        Main main = Main.getInstance();
        if (main.entityMapName.containsKey(entityType)) {
            return main.entityMapName.get(entityType);
        }
        return entityType.toString();
    }
}
