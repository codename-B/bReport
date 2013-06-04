package de.bananaco.report.commands;

import de.bananaco.report.Config;
import de.bananaco.report.ReportPlugin;
import de.bananaco.report.msg.MessageManager;
import de.bananaco.report.msg.Msg;
import de.bananaco.report.report.Report;
import de.bananaco.report.report.ReportManager;
import de.bananaco.report.report.SendMail;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/*
 *
 * @author Brandon Barker
 */
public class Commands extends JavaPlugin {

    private ReportPlugin rp;
    private ReportManager rm;
    private Config config;
    private MessageManager mm;

    public Commands(ReportPlugin rp) {
        this.rp = rp;
        this.rm = rp.getReportManager();
        this.config = rp.getConf();
        this.mm = rp.getMsgManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Get the command name
        String cname = command.getName().toLowerCase(Locale.getDefault());
        // Permission check for players
        if (sender instanceof Player) {
            if (!sender.hasPermission("breport." + cname)) {
                //sender.sendMessage(ChatColor.RED + "[bR] You don't have permission to use the '/" + cname + "' command.");
                mm.msg(sender, Msg.PERMISSION, "'/" + cname + "' command.");
                return true;
            }
        }
        // Only players can use report
        if (cname.equals("report")) {
            if (sender instanceof Player) {
                // Outline our variables
                String reporter = sender.getName();
                String[] report = args;
                // How many words?
                if (report.length < config.getReportLength()) {
                    //sender.sendMessage(ChatColor.RED + "[bR] Reports must be at least 3 words long.");
                    mm.msg(sender, Msg.REPORT_LENGTH, config.getReportLength());
                    return true;
                }
                // How many reports has the user filed before?
                String message = rm.getString(report);
                int num = 0;
                // Calculate how many unresolved reports the user has
                for (Report rpU : rm.getUnresolvedReports()) {
                    if (rpU.getReporter().equalsIgnoreCase(reporter) && !rpU.getResolved()) {
                        int distance = LevenshteinImpl.distance(rpU.getReport().toLowerCase(), message.toLowerCase());
                        if (distance <= config.getLevenshtein()) {
                            num++;
                        }
                    }
                }
                // And shout at them if there's too many similar reports
                if (num > 0) {
                    //sender.sendMessage(ChatColor.RED + "[bR] You already have a ticket about that.");
                    mm.msg(sender, Msg.TICKET_CLONE);
                    return true;
                }
                // And file the report
                Report r = rm.createReport(reporter, report, ((Player) sender).getLocation());
                //sender.sendMessage(ChatColor.AQUA + "[bR] " + ChatColor.GRAY + "A report has been filed for you - ID: " + ChatColor.AQUA + r.getID());
                mm.msg(sender, Msg.REPORT_FILED_YOU, r.getID());
                if (config.useMail()) {
                    String to = config.getMailTo();
                    String host = config.getHost();
                    String from = "noreply@" + Bukkit.getServer().getIp();
                    SendMail.mail(host, to, from, "New Ticket!", r.getReporter() + " has filed ticket #" + r.getID() + ": " + r.getReport());
                }
                // Inform all online admins
                for (Player player : rp.getServer().getOnlinePlayers()) {
                    if (player.hasPermission("breport.read")) {
                        //player.sendMessage(ChatColor.AQUA + "[bR] " + ChatColor.GRAY + "A new report has been filed by " + ChatColor.AQUA + reporter + ChatColor.GRAY + " - ID: " + ChatColor.AQUA + r.getID());
                        if (!((Player) sender).getName().equals(player.getName())) {
                            mm.msg(sender, Msg.REPORT_FILED, reporter, r.getID());
                        }
                    }
                }
                // Log to console too
                rp.log(mm.getMsg(Msg.REPORT_FILED, reporter, r.getID()));
                return true;
            } else {
                //sender.sendMessage(ChatColor.RED + "[bR] Only players can use the '/report' command!");
                mm.msg(sender, Msg.INGAMECOMMAND, "/report");
                return true;
            }
        } else if (cname.equals("read")) {
            if (args.length == 0) {
                List<Report> reports = rm.getUnresolvedReports();
                List<String> prints = new ArrayList<String>();
                // How many shown?
                int shown = 0;
                int show = reports.size() - 1;
                if (show > config.getTicketDisplay() - 1) {
                    show = config.getTicketDisplay() - 1;
                }
                for (int i = show; i >= 0; i--) {
                    Report r = reports.get(i);
                    // Build the String
                    //prints.add(ChatColor.AQUA + r.getReporter() + ChatColor.GRAY + " - ID: " + ChatColor.AQUA + r.getID());
                    prints.add(mm.getMsg(Msg.REPORT_READ, r.getReporter(), r.getID()));
                    shown++;
                }
                //sender.sendMessage(ChatColor.AQUA + "[bR] " + ChatColor.GRAY + "Showing " + ChatColor.AQUA + shown + "/" + reports.size() + ChatColor.GRAY + " unread reports");
                mm.msg(sender, Msg.SHOWING_REPORT, shown, reports.size());
                // New line for each report
                if (reports.size() > 0) {
                    for (int i = 0; i < prints.size(); i++) {
                        sender.sendMessage(prints.get(i));
                    }
                } else {
                    //sender.sendMessage(ChatColor.RED + "** NOTHING TO REPORT **");
                    mm.msg(sender, Msg.NO_REPORTS);
                }
                return true;
            } else {
                String id = args[0];
                if (rm.getReport(id) == null) {
                    //sender.sendMessage(ChatColor.RED + "[bR] No report with that id, use '/read' to see all unresolved reports.");
                    mm.msg(sender, Msg.UNSOLVED_REPORTS, "read");
                    return true;
                } else {
                    Report report = rm.getReport(id);
                    // Send the report data
                    //sender.sendMessage(ChatColor.AQUA + "[bR] " + ChatColor.GRAY + "Reporter: " + ChatColor.AQUA + report.getReporter() + ChatColor.GRAY + " ID: " + ChatColor.AQUA + report.getID());
                    mm.msg(sender, Msg.READ_REPORTER, report.getReporter(), report.getID());
                    //sender.sendMessage(ChatColor.AQUA + "[bR] " + ChatColor.GRAY + "Status: " + ChatColor.AQUA + (report.getResolved() ? "resolved" : "open"));
                    mm.msg(sender, Msg.READ_STATUS, (report.getResolved() ? "resolved" : "open"));
                    //sender.sendMessage(ChatColor.GRAY + "** " + report.getReport());
                    mm.msg(sender, Msg.READ_REPORT, report.getReport());
                    return true;
                }
            }
        } else if (cname.equals("resolve") && args.length > 0) {
            String id = args[0];
            if (rm.getReport(id) == null) {
                //sender.sendMessage(ChatColor.RED + "[bR] No report with that id, use '/read' to see all unresolved reports.");
                mm.msg(sender, Msg.UNSOLVED_REPORTS, "read");
                return true;
            } else {
                Report report = rm.getReport(id);
                if (report.getResolved()) {
                    //sender.sendMessage(ChatColor.RED + "[bR] That report is already resolved!");
                    mm.msg(sender, Msg.REPORT_WAS_RESOLVED);
                    return true;
                }
                report.setResolved(true);
                //sender.sendMessage(ChatColor.AQUA + "[bR] " + ChatColor.GRAY + "Report resolved.");
                mm.msg(sender, Msg.REPORT_IS_RESOLVED);
                // Inform the player that their report has been resolved if they are online
                if (rp.getServer().getPlayerExact(report.getReporter()) != null) {
                    Player player = rp.getServer().getPlayerExact(report.getReporter());
                    //player.sendMessage(ChatColor.AQUA + "[bR] " + ChatColor.GRAY + "A report you filed has been resolved by " + ChatColor.AQUA + rp.getName(sender));
                    mm.msg(sender, Msg.REPORT_RESOLVED, rp.getName(sender));
                    //player.sendMessage(ChatColor.GRAY + "** " + report.getReport());
                    mm.msg(sender, Msg.READ_REPORT, report.getReport());
                }
                return true;
            }
        } else if (cname.equals("unresolve") && args.length > 0) {
            String id = args[0];
            if (rm.getReport(id) == null) {
                //sender.sendMessage(ChatColor.RED + "[bR] No report with that id, use '/read' to see all unresolved reports.");
                mm.msg(sender, Msg.UNSOLVED_REPORTS, "read");
                return true;
            } else {
                Report report = rm.getReport(id);
                if (!report.getResolved()) {
                    //sender.sendMessage(ChatColor.RED + "[bR] That report is not yet resolved!");
                    mm.msg(sender, Msg.REPORT_IS_UNRESOLVED);
                    return true;
                }
                report.setResolved(false);
                //sender.sendMessage(ChatColor.AQUA + "[bR] " + ChatColor.GRAY + "Report unresolved.");
                mm.msg(sender, Msg.REPORT_UNRESOLVED);
                return true;
            }
        } else if (cname.equals("gotoreport") && args.length > 0) {
            // Again, only players can do this one
            if (!(sender instanceof Player)) {
                //sender.sendMessage(ChatColor.RED + "[bR] Only players can use the '/gotoreport' command!");
                mm.msg(sender, Msg.INGAMECOMMAND, "/gotoreport");
                return true;
            }
            String id = args[0];
            if (rm.getReport(id) == null) {
                mm.msg(sender, Msg.UNSOLVED_REPORTS, "read");
                //sender.sendMessage(ChatColor.RED + "[bR] No report with that id, use '/read' to see all unresolved reports.");
                return true;
            } else {
                Player player = (Player) sender;
                Report r = rm.getReport(id);
                Location loc = r.getLocation();
                player.teleport(loc);
                player.getLocation().setYaw(r.getYaw());
                player.getLocation().setPitch(r.getPitch());
                //sender.sendMessage(ChatColor.AQUA + "[bR] " + ChatColor.GRAY + "Teleported to the location of the report - ID: " + ChatColor.AQUA + r.getID());
                mm.msg(sender, Msg.REPORT_TELEPORT, r.getID());
                return true;
            }
        } else if (cname.equals("comment")) {
            Report report;
            if (args.length >= 1) {
                report = rm.getReport(args[0]);
                if (report == null) {
                    mm.msg(sender, Msg.UNSOLVED_REPORTS, "read");
                    //sender.sendMessage(ChatColor.RED + "[bR] No report with that id, use '/read' to see all unresolved reports.");
                    return true;
                }
            } else {
                mm.msg(sender, Msg.UNSOLVED_REPORTS, "read");
                return true;
            }
            if (args.length > 1) {
                String message = rm.getString(args, 0);
                report.getComments().add("&b" + sender.getName() + "&7" + ": " + message);
                mm.msg(sender, Msg.COMMENT_ADD, report.getID());
            } else {
                if (report.getComments().size() > 0) {
                    mm.msg(sender, Msg.COMMENT, report.getComments().size(), report.getID());
                    for (String comment : report.getComments()) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " - " + comment));
                    }
                } else {
                    mm.msg(sender, Msg.NO_COMMENTS);
                }
            }
            return true;
        } else if (cname.equals("modchat") && args.length > 0) {
            String name = rp.getName(sender);

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                sb.append(args[i]).append(" ");
            }
            String message = sb.toString();
            // Send the message to online mods/admins
            for (Player player : rp.getServer().getOnlinePlayers()) {
                if (player.hasPermission("breport.modchat")) {
                    player.sendMessage(ChatColor.AQUA + mm.getName() + ChatColor.GRAY + name + ChatColor.AQUA + ": " + message);
                }
            }
            // Also log it to the console
            rp.log(name + ": " + message);
            return true;
        }

        return false;
    }

    /**
     * @return the rp
     */
    public ReportPlugin getRp() {
        return rp;
    }
}
