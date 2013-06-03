package de.bananaco.report.msg;

import de.bananaco.report.ReportPlugin;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Brandon Barker
 */
public class MessageManager {

    private static final Logger LOG = Logger.getLogger(MessageManager.class.getName());
    private String name;
    private ReportPlugin rp;
    private Map<String, String> msg;
    private File file;
    private final YamlConfiguration config;

    /**
     *
     * @param rp
     */
    public MessageManager(ReportPlugin rp) {
        this.rp = rp;
        this.msg = new HashMap<String, String>();
        this.file = new File("plugins/bReport/messages.yml");
        this.config = new YamlConfiguration();
    }

    /**
     *
     * @param player
     * @param format
     * @param args
     */
    public void msg(CommandSender player, Msg format, Object... args) {
        String newMsg = String.format(msg.get(format.getName()), args);
        String colorMsg = ChatColor.translateAlternateColorCodes('&', name + " " + newMsg);
        player.sendMessage(colorMsg);
    }

    /**
     *
     * @param player
     * @param format
     */
    public void msg(CommandSender player, Msg format) {
        String newMsg = msg.get(format.getName());
        String colorMsg = ChatColor.translateAlternateColorCodes('&', name + " " + newMsg);
        player.sendMessage(colorMsg);
    }

    /**
     *
     * @param format
     * @return
     */
    public String getMsg(Msg format) {
        String newMsg = msg.get(format.getName());
        String colorMsg = ChatColor.translateAlternateColorCodes('&', newMsg);
        return colorMsg;
    }

    /**
     *
     * @param format
     * @param args
     * @return
     */
    public String getMsg(Msg format, Object... args) {
        String newMsg = String.format(msg.get(format.getName()), args);
        String colorMsg = ChatColor.translateAlternateColorCodes('&', newMsg);
        return colorMsg;
    }

    /**
     *
     */
    public void load() {
        try {
            file = rp.getDirectory("messages.yml");
            fileCheck();
            config.load(file);

            name = config.getString("name", "&8[&bbR&r&8]&r");

            for (Msg key : Msg.values()) {
                String cmsg = config.getString("Messages." + key.getName(), key.getMsg());
                msg.put(key.getName(), cmsg);
            }
            save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public void save() {
        config.set("name", name);

        for (String key : msg.keySet()) {
            String keyMsg = msg.get(key);
            config.set("Messages." + key, keyMsg);
        }

        try {
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     *
     * @return @throws IOException
     * @throws IOException
     */
    protected void fileCheck() throws IOException {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
    }

    /**
     *
     * @return
     */
    public String getName() {
        return this.name;
    }
}