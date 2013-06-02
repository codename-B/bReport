package de.bananaco.report;

import de.bananaco.report.commands.Commands;
import de.bananaco.report.listeners.ModChat;
import de.bananaco.report.listeners.ReportListener;
import de.bananaco.report.report.ReportManager;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

public class ReportPlugin extends JavaPlugin {

    private ReportManager rm;
    private Config config;
    private ReportListener listener;
    private ModChat modchat;
    private Commands command;

    public ReportPlugin() {
        this.rm = ReportManager.getInstance();
        this.config = new Config();
        this.listener = new ReportListener(config);
        this.modchat = new ModChat();
        this.command = new Commands(this);
    }

    @Override
    public void onDisable() {
        rm.save();
        log("Disabled");
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(listener, this);
        getServer().getPluginManager().registerEvents(modchat, this);
        getCommand("report").setExecutor(command);
        getCommand("read").setExecutor(command);
        getCommand("gotoreport").setExecutor(command);
        getCommand("resolve").setExecutor(command);
        getCommand("unresolve").setExecutor(command);
        getCommand("modchat").setExecutor(command);
        registerPermissions();
        rm.load();
        config.load();
        log("Enabled");
    }

    public void log(String message) {
        this.getLogger().log(Level.INFO, "[bR] {0}", message);
    }

    public void log(String message, Level level) {
        this.getLogger().log(level, "[bR] {0}", message);
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

    public String getName(CommandSender sender) {
        String name;
        if (!(sender instanceof Player)) {
            name = "CONSOLE";
        } else {
            name = sender.getName();
        }
        return name;
    }

    public ReportManager getReportManager() {
        return this.rm;
    }

    public Config getConf() {
        return this.config;
    }
}
