package de.bananaco.report.listeners;

import de.bananaco.report.Config;
import de.bananaco.report.report.Report;
import de.bananaco.report.report.ReportManager;
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

        // New line for each report
        if (reports.size() > 0) {
            player.sendMessage(ChatColor.AQUA + "[bR] " + ChatColor.GRAY + "There are " + ChatColor.AQUA + reports.size() + ChatColor.GRAY + " unread reports");
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
