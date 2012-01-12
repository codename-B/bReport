package de.bananaco.report;

import java.io.File;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {
	
	private final File file = new File("plugins/bReport/config.yml");
	private final YamlConfiguration config = new YamlConfiguration();
	
	private int levenshtein = 6;
	private int ticketDisplay = 7;
	
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
			config.set("levenshtein", this.levenshtein);
			config.set("ticket-display", this.ticketDisplay);
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

}
