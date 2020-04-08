package fr.hegsis.spawnerpickaxe.manager;

import fr.hegsis.spawnerpickaxe.Main;

import java.util.HashMap;
import java.util.Map;

public class ManagerMain {

    public static void setOptions(Main main) {
        Map<Option, Boolean> optionMap = new HashMap<>();
        for (Option op : Option.values()) {
            main.optionsUsed.put(op, main.getConfig().getBoolean("use."+op.toString().toLowerCase()));
        }
    }
}
