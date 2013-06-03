package de.bananaco.report.listeners;

import de.bananaco.report.Config;
import de.bananaco.report.ReportPlugin;
import de.bananaco.report.msg.MessageManager;
import de.bananaco.report.msg.Msg;
import de.bananaco.report.report.Report;
import de.bananaco.report.report.ReportManager;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ReportListener implements Listener {

    private Config config;
    private ReportManager rm;
    private MessageManager mm;

    public ReportListener(ReportPlugin plugin) {
        this.config = plugin.getConf();
        this.rm = plugin.getReportManager();
        this.mm = plugin.getMsgManager();
    }

    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent event) {
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
            //player.sendMessage(ChatColor.AQUA + "[bR] " + ChatColor.GRAY + "There are " + ChatColor.AQUA + reports.size() + ChatColor.GRAY + " unread reports");
            mm.msg(player, Msg.ON_JOIN, reports.size());
            
        } else {
            player.sendMessage(mm.getMsg(Msg.NO_REPORTS));
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
