package fr.hegsis.spawnerpickaxe.listeners;

import fr.hegsis.spawnerpickaxe.Main;
import fr.hegsis.spawnerpickaxe.manager.Option;
import fr.hegsis.spawnerpickaxe.utils.GiveItems;
import fr.hegsis.spawnerpickaxe.utils.Utils;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class SignListeners implements Listener {

    private Main main;

    public SignListeners(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onSign(SignChangeEvent e) {
        Player p = e.getPlayer();

        if (!e.getLine(0).equalsIgnoreCase("[SPICKAXE]")) return;

        if (!Utils.hasPermission(p, "pickaxe-create-sign", main)) {
            Utils.sendMessage(p, "no-permission", main);
            e.getBlock().breakNaturally();
            return;
        }

        if (!main.optionsUsed.get(Option.SPAWNERPICKAXE_SIGN)) {
            p.sendMessage(Utils.getConfigMessage("option-disable", main).replaceAll("&", "§").replaceAll("%option%", Option.SPAWNERPICKAXE_SIGN.toString().toLowerCase().replaceAll("_", " ")));
            e.getBlock().breakNaturally();
            return;
        }

        List<String> configSignLine =  main.getConfig().getStringList("sign.shop");
        String configLine, signLine, price, durability;
        price = e.getLine(1);
        durability = e.getLine(2);

        if (Utils.isNumber(durability) < 1 || Utils.isNumber(price) < 1) {
            Utils.sendMessage(p, "price-durabiltiy-no-less-one", main);
            e.getBlock().breakNaturally();
            return;
        }

        for (int i=0; i<4; i++) {
            configLine = configSignLine.get(i).replaceAll("&", "§");
            if (!configLine.equalsIgnoreCase("")) {
                try {
                    e.setLine(i, configLine.replaceAll("%price%",price).replaceAll("%durability%", durability));
                } catch (Exception ex) {
                    e.getBlock().breakNaturally();
                    Utils.sendMessage(p, "error-sign-creation", main);
                }
            } else {
                e.setLine(i, "");
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        //Si le joueur fait un clique droit sur le panneau
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock().getState() instanceof Sign) {
            Sign sign = (Sign)e.getClickedBlock().getState();
            if(sign.getLine(0).equalsIgnoreCase(main.getConfig().getStringList("sign.shop").get(0).replaceAll("&", "§"))) {

                if (!Utils.hasPermission(p, "pickaxe-use-sign", main)) {
                    Utils.sendMessage(p, "no-permission", main);
                    return;
                }

                if (!main.optionsUsed.get(Option.SPAWNERPICKAXE_SIGN)) {
                    p.sendMessage(Utils.getConfigMessage("option-disable", main).replaceAll("&", "§").replaceAll("%option%", Option.SPAWNERPICKAXE_SIGN.toString().toLowerCase().replaceAll("_", " ")));
                    return;
                }

                int durability = 0, price = 0;
                List<String> configSignLine =  main.getConfig().getStringList("sign.shop");
                String configLine;
                for (int i=1; i<4; i++) {
                    configLine = configSignLine.get(i).replaceAll("&", "§");

                    if(configLine.contains("%durability%")) {
                        durability = Utils.isNumber(Utils.getValue(sign.getLine(i), configLine, false, main));
                    }

                    if(configLine.contains("%price%")) {
                        price = Utils.isNumber(Utils.getValue(sign.getLine(i), configLine, false, main));
                    }
                }
                GiveItems.payAndGiveSpawnerPickaxe(p, price, durability, main);
                return;
            }
        }
    }
}
