package de.bananaco.report.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 *
 * @author Brandon Barker
 */
public class ModChat implements Listener {

    public ModChat() {
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("breport.modchat")) {
            String message = "";
            String[] splitMSG = event.getMessage().split(" ");
            for (String m : splitMSG) {
                if (m == null ? splitMSG[0] == null : m.equals(splitMSG[0])) {
                    message += m;
                } else {
                    message += " " + m;
                }
            }
            if (message.length() > 0 && message.substring(0, 1).equals("#")) {
                message = message.replace("#", "/modchat ");
                event.setCancelled(true);
                player.chat(message);
            }
        }
    }
}
