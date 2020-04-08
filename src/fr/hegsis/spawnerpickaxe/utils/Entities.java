package fr.hegsis.spawnerpickaxe.utils;

import fr.hegsis.spawnerpickaxe.Main;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

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
}
