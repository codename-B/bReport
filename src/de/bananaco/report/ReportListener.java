package de.bananaco.report;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class ReportListener implements Listener {
	
	Config config;
	ReportManager rm = ReportManager.getInstance();
	
	ReportListener(Config config) {
		this.config = config;
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		if(!player.hasPermission("breport.read"))
			return;
		if(!config.showMessage())
			return;
		List<Report> reports = rm.getUnresolvedReports();
		List<String> prints = new ArrayList<String>();
		// How many shown?
		int shown = 0;
		int show = reports.size()-1;
		if(show > config.getTicketDisplay()-1)
			show = config.getTicketDisplay()-1;
		for(int i=show; i>=0; i--) {
			Report r = reports.get(i);
			// Build the String
			prints.add(ChatColor.AQUA+r.getReporter()+ChatColor.GRAY+" - ID: "+ChatColor.AQUA+r.getID());
			shown++;
		}
		player.sendMessage(ChatColor.AQUA+"[bR] "+ChatColor.GRAY+"Showing "+ChatColor.AQUA+shown+"/"+reports.size()+ChatColor.GRAY+" unread reports");
		// New line for each report
		if(reports.size() > 0)
			for(int i=0; i<prints.size(); i++)
				player.sendMessage(prints.get(i));
		else
			player.sendMessage(ChatColor.RED+"** NOTHING TO REPORT **");	
	}

	@EventHandler
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
