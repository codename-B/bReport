package de.bananaco.report.listeners;

import de.bananaco.report.Config;
import de.bananaco.report.report.Report;
import de.bananaco.report.report.ReportManager;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class ReportListener implements Listener {

    private Config config;
    private ReportManager rm = ReportManager.getInstance();

    public ReportListener(Config config) {
        this.config = config;
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("breport.read")) {
            return;
        }
        if (!config.showMessage()) {
            return;
        }
        List<Report> reports = getRm().getUnresolvedReports();
        List<String> prints = new ArrayList<String>();
        // How many shown?
        int shown = 0;
        int show = reports.size() - 1;
        if (show > getConfig().getTicketDisplay() - 1) {
            show = getConfig().getTicketDisplay() - 1;
        }
        for (int i = show; i >= 0; i--) {
            Report r = reports.get(i);
            // Build the String
            prints.add(ChatColor.AQUA + r.getReporter() + ChatColor.GRAY + " - ID: " + ChatColor.AQUA + r.getID());
            shown++;
        }
        player.sendMessage(ChatColor.AQUA + "[bR] " + ChatColor.GRAY + "Showing " + ChatColor.AQUA + shown + "/" + reports.size() + ChatColor.GRAY + " unread reports");
        // New line for each report
        if (reports.size() > 0) {
            for (int i = 0; i < prints.size(); i++) {
                player.sendMessage(prints.get(i));
            }
        } else {
            player.sendMessage(ChatColor.RED + "** NOTHING TO REPORT **");
        }
    }

    /**
     * @return the config
     */
    public Config getConfig() {
        return config;
    }

    /**
     * @return the rm
     */
    public ReportManager getRm() {
        return rm;
    }
}
