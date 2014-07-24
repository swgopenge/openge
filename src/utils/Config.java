package utils;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;

/*! Reads and stores configuration data from a file */
public class Config {

	Properties configData;

	String filePath;

	public Config() {
		initialize();
	};

	public Config(String filename) {
		initialize();
		setFilePath(filename);
		loadConfigFile(getFilePath());
	}

	/* ! Displays the current filePath and the key:value pairs of configData */
	public void displayData() {
		System.out.println("File Path: " + getFilePath());
		configData.list(System.out);
	}
	
	public boolean keyExists(String key) {
		return (configData.getProperty(key) != null);
	}

	public double getDouble(String key) {
		return Double.parseDouble(getString(key));
	}

	public String getFilePath() {
		return this.filePath;
	}

	public int getInt(String key) {
		return Integer.parseInt(getString(key));
	}

	public String getProperty(String key) {
		return configData.getProperty(key);
	}

	public String getString(String key) {
		return getProperty(key);
	}
	
	public boolean getBoolean(String key) {
		return Boolean.valueOf(getString(key));
	}

	public void initialize() {
		configData = new Properties();
	}

	public boolean loadConfigFile() {

		if (getFilePath() == null || getFilePath().isEmpty()) {
			System.err
					.println("Config.java loadConfigFile(): Attempted to load from a null or empty file path.");
			return false;
		} else
			return loadConfigFile(getFilePath());
	}

	public boolean loadConfigFile(String filename) {

		FileReader reader;

		try {

			reader = new FileReader(filename);
			synchronized (reader) {
				configData = new Properties();
				configData.load(reader);

				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;

		}
	}

	public boolean saveConfig() {
		if (getFilePath() == null || getFilePath().isEmpty()) {
			System.err
					.println("Config.java saveConfig(): Attempted to save configuration to a null or empty filepath.");
			return false;
		} else {
			return saveConfig(getFilePath());
		}
	}

	public boolean saveConfig(String filename) {
		FileWriter writer;
		try {
			writer = new FileWriter(filename);
			synchronized (writer) {
				configData.store(writer, null);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void setFilePath(String filename) {
		this.filePath = filename;
	}

	public void setProperty(String key, double value) {
		String v = "" + value;
		setProperty(key, v);
	}

	public void setProperty(String key, int value) {
		String v = "" + value;
		setProperty(key, v);
	}

	public void setProperty(String key, String value) {
		configData.setProperty(key, value);
		saveConfig();

	}

}