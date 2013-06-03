package de.bananaco.report;

import java.io.File;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {

    private File file;
    private final YamlConfiguration config;
    private int levenshtein;
    private int ticketDisplay;
    private int reportLength;
    private boolean showMessage;
    private String mailHost;
    private boolean useMail;
    private String to;
    private ReportPlugin rp;
    
    public Config(ReportPlugin rp) {
        this.file = new File("plugins/bReport/config.yml");
        this.config = new YamlConfiguration();
        this.levenshtein = 6;
        this.ticketDisplay = 7;
        this.reportLength = 3;
        this.showMessage = false;
        this.mailHost = "localhost";
        this.useMail = false;
        this.to = "test@test.com";
        this.rp = rp;
    }

    /**
     * The "distance" required between Strings
     *
     * used in the spam filter
     *
     * @return int
     */
    public int getLevenshtein() {
        return levenshtein;
    }

    /**
     * Max number of tickets displayed by /read
     *
     * @return int
     */
    public int getTicketDisplay() {
        return ticketDisplay;
    }

    public String getHost() {
        return mailHost;
    }

    public boolean useMail() {
        return useMail;
    }

    public String getMailTo() {
        return to;
    }
    
    public Integer getReportLength() {
        return this.reportLength;
    }

    /**
     * Loads the config.yml
     */
    public void load() {
        try {
            file = rp.getDirectory("config.yml");
            fileCheck();
            config.load(file);
            // Set the values
            this.levenshtein = config.getInt("levenshtein", this.levenshtein);
            this.ticketDisplay = config.getInt("ticket-display", this.ticketDisplay);
            this.showMessage = config.getBoolean("join-message", this.showMessage);
            this.reportLength =  config.getInt("report-length", this.reportLength);
            // Set the new values
            this.useMail = config.getBoolean("Mail.use-mail", useMail);
            this.mailHost = config.getString("Mail.mail-host", mailHost);
            this.to = config.getString("Mail.mail-to", to);
            // Write the values
            config.set("Report-Length", this.reportLength);
            config.set("levenshtein", this.levenshtein);
            config.set("ticket-display", this.ticketDisplay);
            config.set("join-message", this.showMessage);
            // Write the new values
            config.set("Mail.use-mail", useMail);
            config.set("Mail.mail-host", mailHost);
            config.set("Mail.mail-to", to);
            // Save the file
            config.save(file);
        } catch (Exception e) {
        }
    }

    /**
     * Internal method, creates the file if it doesn't exist
     *
     * @throws Exception
     */
    protected void fileCheck() throws Exception {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
    }

    public boolean showMessage() {
        return showMessage;
    }
}
