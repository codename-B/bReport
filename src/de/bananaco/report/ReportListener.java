package de.bananaco.report;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

public class ReportListener extends PlayerListener {
	
	@Override
	public void onPlayerChat(PlayerChatEvent event) {
		Player player = event.getPlayer();
		if(player.hasPermission("breport.modchat")) {
			String message = event.getMessage();
			if(message.length() > 0 && message.substring(0, 1).equals("#")) {
				message = message.replace("#", "/modchat ");
				event.setCancelled(true);
				player.chat(message);
			}
		}
	}

}
