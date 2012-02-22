package de.bananaco.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

public class ReportPlugin extends JavaPlugin {

	private ReportManager rm;
	private Config config = new Config();
	private ReportListener listener;
	
	@Override
	public void onDisable() {
		rm.save();
		log("Disabled");
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEnable() {
		listener = new ReportListener(config);
		getServer().getPluginManager().registerEvents(listener, this);
		registerPermissions();
		rm = ReportManager.getInstance();
		rm.load();
		config.load();
		log("Enabled");
	}

	public void log(String message) {
		System.out.println("[bR] "+message);
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
				sender.sendMessage(ChatColor.RED+"[bR] You don't have permission to use the '/"+cname+"' command.");
				return true;
			}
		}
		// Only players can use report
		if(cname.equals("report")) {
			if(sender instanceof Player) {
				// Outline our variables
				String reporter = sender.getName();
				String[] report = args;
				// How many words?
				if(report.length < 3) {
					sender.sendMessage(ChatColor.RED+"[bR] Reports must be at least 3 words long.");
					return true;
				}
				// How many reports has the user filed before?
				String message = rm.getString(report);
				int num = 0;
				// Calculate how many unresolved reports the user has
				for(Report rp : rm.getUnresolvedReports()) {
					if(rp.getReporter().equalsIgnoreCase(reporter) && !rp.getResolved()) {
						int distance = LevenshteinImpl.distance(rp.getReport().toLowerCase(), message.toLowerCase());
						if(distance <= config.getLevenshtein())
							num++;
					}
				}
				// And shout at them if there's too many similar reports
				if(num > 0) {
					sender.sendMessage(ChatColor.RED+"[bR] You already have a ticket about that.");
					return true;
				}
				// And file the report
				Report r = rm.createReport(reporter, report, ((Player) sender).getLocation());
				sender.sendMessage(ChatColor.AQUA+"[bR] "+ChatColor.GRAY+"A report has been filed for you - ID: "+ChatColor.AQUA+r.getID());
				// Inform all online admins
				for(Player player : getServer().getOnlinePlayers()) {
					if(player.hasPermission("breport.read")) {
						player.sendMessage(ChatColor.AQUA+"[bR] "+ChatColor.GRAY+"A new report has been filed by "+ChatColor.AQUA+reporter+ChatColor.GRAY+" - ID: "+ChatColor.AQUA+r.getID());
					}
				}
				// Log to console too
				log("A new report has been filed by "+reporter+" - ID: "+r.getID());
				return true;
			} else {
				sender.sendMessage(ChatColor.RED+"[bR] Only players can use the '/report' command!");
				return true;
			}
		}
		else if(cname.equals("read")) {
			if(args.length == 0) {
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
				sender.sendMessage(ChatColor.AQUA+"[bR] "+ChatColor.GRAY+"Showing "+ChatColor.AQUA+shown+"/"+reports.size()+ChatColor.GRAY+" unread reports");
				// New line for each report
				if(reports.size() > 0)
					for(int i=0; i<prints.size(); i++)
						sender.sendMessage(prints.get(i));
				else
					sender.sendMessage(ChatColor.RED+"** NOTHING TO REPORT **");
				return true;
			} else {
				String id = args[0];
				if(rm.getReport(id) == null) {
					sender.sendMessage(ChatColor.RED+"[bR] No report with that id, use '/read' to see all unresolved reports.");
					return true;
				} else {
					Report report = rm.getReport(id);
					// Send the report data
					sender.sendMessage(ChatColor.AQUA+"[bR] "+ChatColor.GRAY+"Reporter: "+ChatColor.AQUA+report.getReporter()+ChatColor.GRAY+" ID: "+ChatColor.AQUA+report.getID());
					sender.sendMessage(ChatColor.AQUA+"[bR] "+ChatColor.GRAY+"Status: "+ChatColor.AQUA+(report.getResolved()?"resolved":"open"));
					sender.sendMessage(ChatColor.GRAY+"** "+report.getReport());
					return true;
				}
			}
		} else if(cname.equals("resolve") && args.length > 0) {
			String id = args[0];
			if(rm.getReport(id) == null) {
				sender.sendMessage(ChatColor.RED+"[bR] No report with that id, use '/read' to see all unresolved reports.");
				return true;
			} else {
				Report report = rm.getReport(id);
				if(report.getResolved()) {
					sender.sendMessage(ChatColor.RED+"[bR] That report is already resolved!");
					return true;
				}
				report.setResolved(true);
				sender.sendMessage(ChatColor.AQUA+"[bR] "+ChatColor.GRAY+"Report resolved.");
				// Inform the player that their report has been resolved if they are online
				if(getServer().getPlayerExact(report.getReporter()) != null) {
					Player player = getServer().getPlayerExact(report.getReporter());
					player.sendMessage(ChatColor.AQUA+"[bR] "+ChatColor.GRAY+"A report you filed has been resolved by "+ChatColor.AQUA+getName(sender));
					player.sendMessage(ChatColor.GRAY+"** "+report.getReport());
				}
				return true;
			}
		} else if(cname.equals("unresolve") && args.length > 0) {
			String id = args[0];
			if(rm.getReport(id) == null) {
				sender.sendMessage(ChatColor.RED+"[bR] No report with that id, use '/read' to see all unresolved reports.");
				return true;
			} else {
				Report report = rm.getReport(id);
				if(!report.getResolved()) {
					sender.sendMessage(ChatColor.RED+"[bR] That report is not yet resolved!");
					return true;
				}
				report.setResolved(false);
				sender.sendMessage(ChatColor.AQUA+"[bR] "+ChatColor.GRAY+"Report unresolved.");
				return true;
			}
		} else if(cname.equals("gotoreport") && args.length > 0) {
			// Again, only players can do this one
			if(!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED+"[bR] Only players can use the '/gotoreport' command!");
				return true;
			}
			String id = args[0];
			if(rm.getReport(id) == null) {
				sender.sendMessage(ChatColor.RED+"[bR] No report with that id, use '/read' to see all unresolved reports.");
				return true;
			} else {
				Player player = (Player) sender;
				Report r = rm.getReport(id);
				Location loc = rm.getLocation(r.getLocation());
				player.teleport(loc);
				sender.sendMessage(ChatColor.AQUA+"[bR] "+ChatColor.GRAY+"Teleported to the location of the report - ID: "+ChatColor.AQUA+r.getID());
				return true;
			}
		} else if(cname.equals("modchat") && args.length > 0) {
			String name = getName(sender);

			StringBuilder sb = new StringBuilder();
			for(int i=0; i<args.length; i++)
				sb.append(args[i]).append(" ");
			String message = sb.toString();
			// Send the message to online mods/admins
			for(Player player : getServer().getOnlinePlayers()) {
				if(player.hasPermission("breport.modchat")) {
					player.sendMessage(ChatColor.AQUA+"[bR] "+ChatColor.GRAY+name+ChatColor.AQUA+": "+message);
				}
			}
			// Also log it to the console
			log(name+": "+message);
			return true;
		}
		return false;
	}
	
	public String getName(CommandSender sender) {
		String name;
		if(!(sender instanceof Player)) {
			name = "CONSOLE";
		} else {
			name = sender.getName();
		}
		return name;
	}

}
