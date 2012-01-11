package de.bananaco.report;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

public class ReportPlugin extends JavaPlugin {

	private ReportManager rm;

	@Override
	public void onDisable() {
		rm.save();
		log("Disabled");
	}

	@Override
	public void onEnable() {
		registerPermissions();
		rm = ReportManager.getInstance();
		rm.load();
		log("Enabled");
	}

	public void log(String message) {
		System.out.println("[bReport] "+message);
	}

	public void registerPermissions() {
		Map<String, Boolean> children = new HashMap<String, Boolean>();
		// Add all the permission nodes we'll be using
		children.put("breport.report", true);
		children.put("breport.read", true);
		children.put("breport.gotoreport", true);
		children.put("breport.resolve", true);
		children.put("breport.unresolve", true);
		children.put("breport.modchat", true);
		// Put them under a parent
		Permission perm = new Permission("breport.*", PermissionDefault.OP, children);
		getServer().getPluginManager().addPermission(perm);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		// Get the command name
		String cname = command.getName().toLowerCase();
		// Permission check for players
		if(sender instanceof Player) {
			if(!sender.hasPermission("breport."+cname)) {
				sender.sendMessage(ChatColor.RED+"[bReport] You don't have permission to use the '/"+cname+"' command.");
				return true;
			}
		}
		// Only players can use report
		if(cname.equals("report")) {
			if(sender instanceof Player) {
				// Outline our variables
				String reporter = sender.getName();
				String[] report = args;
				// And file the report
				Report r = rm.createReport(reporter, report, ((Player) sender).getLocation());
				sender.sendMessage(ChatColor.GREEN+"[bReport] A report has been filed for you - ID: "+r.getID());
				// Inform all online admins
				for(Player player : getServer().getOnlinePlayers()) {
					if(player.hasPermission("breport.read")) {
						player.sendMessage(ChatColor.GREEN+"[bReport] A new report has been filed by "+reporter+" - ID: "+r.getID());
					}
				}
				// Log to console too
				log("A new report has been filed by "+reporter+" - ID: "+r.getID());
				return true;
			} else {
				sender.sendMessage(ChatColor.RED+"[bReport] Only players can use the '/report' command!");
				return true;
			}
		}
		else if(cname.equals("read")) {
			if(args.length == 0) {
				List<Report> reports = rm.getUnresolvedReports();
				// How many shown?
				int shown = 0;
				StringBuilder sb = new StringBuilder();
				for(int i=0; i<reports.size() && i<6; i++) {
					Report r = reports.get(i);
					// Build the String
					if(i==reports.size()-1) {
						// End with a full stop
						sb.append(r.getReporter()+" - ID: "+r.getID()+".");
					} else {
						// Comma in the middle
						sb.append(r.getReporter()+" - ID: "+r.getID()+", ");
					}
					shown++;
				}
				sender.sendMessage(ChatColor.GREEN+"[bReport] Showing "+shown+"/"+reports.size()+" unread reports");
				if(reports.size() > 0)
					sender.sendMessage(sb.toString());
				else
					sender.sendMessage(ChatColor.RED+"** NOTHING TO REPORT **");
				return true;
			} else {
				String id = args[0];
				if(rm.getReport(id) == null) {
					sender.sendMessage(ChatColor.RED+"[bReport] No report with that id, use '/read' to see all unresolved reports.");
					return true;
				} else {
					Report report = rm.getReport(id);
					sender.sendMessage(ChatColor.GREEN+"[bReport] ID:"+report.getID());
					sender.sendMessage("Report by: "+report.getReporter());
					sender.sendMessage(report.getReport());
					if(!report.getResolved()) {
						sender.sendMessage("Use '/gotoreport "+id+"' to go to where this report was made.");
						sender.sendMessage("Use '/resolve "+id+"' to resolve this report.");
					}
					return true;
				}
			}
		} else if(cname.equals("resolve") && args.length > 0) {
			String id = args[0];
			if(rm.getReport(id) == null) {
				sender.sendMessage(ChatColor.RED+"[bReport] No report with that id, use '/read' to see all unresolved reports.");
				return true;
			} else {
				Report report = rm.getReport(id);
				report.setResolved(true);
				sender.sendMessage(ChatColor.GREEN+"[bReport] Report resolved.");
				// Inform the player that their report has been resolved if they are online
				if(getServer().getPlayerExact(report.getReporter()) != null) {
					Player player = getServer().getPlayerExact(report.getReporter());
					player.sendMessage(ChatColor.GREEN+"[bReport] A report you filed has been resolved - ID: "+report.getID());
					player.sendMessage(report.getReport());
				}
				return true;
			}
		} else if(cname.equals("unresolve") && args.length > 0) {
			String id = args[0];
			if(rm.getReport(id) == null) {
				sender.sendMessage(ChatColor.RED+"[bReport] No report with that id, use '/read' to see all unresolved reports.");
				return true;
			} else {
				Report report = rm.getReport(id);
				report.setResolved(false);
				sender.sendMessage(ChatColor.GREEN+"[bReport] Report unresolved.");
				return true;
			}
		} else if(cname.equals("gotoreport") && args.length > 0) {
			// Again, only players can do this one
			if(!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED+"[bReport] Only players can use the '/gotoreport' command!");
				return true;
			}
			String id = args[0];
			if(rm.getReport(id) == null) {
				sender.sendMessage(ChatColor.RED+"[bReport] No report with that id, use '/read' to see all unresolved reports.");
				return true;
			} else {
				Player player = (Player) sender;
				Location loc = rm.getLocation(rm.getReport(id).getLocation());
				player.teleport(loc);
				sender.sendMessage(ChatColor.GREEN+"[bReport] Teleported to the location of the report.");
				return true;
			}
		} else if(cname.equals("modchat")) {
			String name = "";
			if(!(sender instanceof Player)) {
				name = "CONSOLE";
			} else {
				name = sender.getName();
			}
			StringBuilder sb = new StringBuilder();
			for(int i=0; i<args.length; i++)
				sb.append(args[i]).append(" ");
			String message = sb.toString();
			// Send the message to online mods/admins
			for(Player player : getServer().getOnlinePlayers()) {
				if(player.hasPermission("breport.modchat")) {
					player.sendMessage(ChatColor.GREEN+"[bReport] "+name+" - "+message);
				}
			}
			// Also log it to the console
			log(name+" - "+message);
			return true;
		}
		return false;
	}

}
