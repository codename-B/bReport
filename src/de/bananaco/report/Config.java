package de.bananaco.report;

import java.io.File;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {
	
	private final File file = new File("plugins/bReport/config.yml");
	private final YamlConfiguration config = new YamlConfiguration();
	
	private int levenshtein = 6;
	private int ticketDisplay = 7;
	private boolean showMessage = false;
	public String mailHost = "localhost";
	public boolean useMail = false;
	public String to = "test@test.com";
	
	/**
	 * The "distance" required between Strings
	 * 
	 * used in the spam filter
	 * @return int
	 */
	public int getLevenshtein() {
		return levenshtein;
	}
	
	/**
	 * Max number of tickets displayed by /read
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
	
	/**
	 * Loads the config.yml
	 */
	public void load() {
		try {
			fileCheck();
			config.load(file);
			// Set the values
			this.levenshtein = config.getInt("levenshtein", this.levenshtein);
			this.ticketDisplay = config.getInt("ticket-display", this.ticketDisplay);			
			this.showMessage = config.getBoolean("show-message", showMessage);
			// Set the new values
			this.useMail = config.getBoolean("use-mail", useMail);
			this.mailHost = config.getString("mail-host", mailHost);
			this.to = config.getString("mail-to", to);
			// Write the values
			config.set("levenshtein", this.levenshtein);
			config.set("ticket-display", this.ticketDisplay);
			config.set("show-message", showMessage);
			// Write the new values
			config.set("use-mail", useMail);
			config.set("mail-host", mailHost);
			config.set("mail-to", to);
			// Save the file
			config.save(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Internal method, creates the file if it doesn't exist
	 * @throws Exception
	 */
	protected void fileCheck() throws Exception {
		if(!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
	}

	public boolean showMessage() {
		return showMessage;
	}

}
