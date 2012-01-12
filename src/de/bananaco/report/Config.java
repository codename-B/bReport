package de.bananaco.report;

import java.io.File;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {
	
	private final File file = new File("plugins/bReport/config.yml");
	private final YamlConfiguration config = new YamlConfiguration();
	
	/**
	 * Loads the config.yml
	 */
	public void load() {
		try {
			fileCheck();
			config.load(file);
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
