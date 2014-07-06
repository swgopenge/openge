package utils;


public class DefaultConfig {
	
	static final String DB_NAME = "NGE";
	static final String DB_USER = "nge";
	static final String DB_PASS = "nge";
	static final String DB_URL = "localhost";
	
	static final String LOGIN_URL = "";
	static final int LOGIN_PORT = 44453;
	static final int LOGIN_SESSION_KEY_SIZE = 24;
	static final String ZONE_URL = "";
	static final int ZONE_PORT = 44463;
	static final int GALAXY_ID = 2;
	static final int PING_PORT = 44462;

	static final String FILE_PATH = "nge.cfg";
	
	public static final Config getConfig()
	{
		Config cfg = new Config();
		cfg.setFilePath(getFilePath());
		cfg.setProperty("DB.URL", getDbUrl());
		cfg.setProperty("DB.USER",getDbUser());
		cfg.setProperty("DB.NAME",getDbName());
		cfg.setProperty("DB.PASS",getDbPass());
		cfg.setProperty("ZONE.URL",getZoneUrl());
		cfg.setProperty("LOGIN.URL",getLoginUrl());
		cfg.setProperty("LOGIN.PORT",getLoginPort());
		cfg.setProperty("LOGIN.SESSION_KEY_SIZE",getLoginSessionKeySize());
		cfg.setProperty("ZONE.PORT",getZonePort());
		cfg.setProperty("GALAXY_ID",getGalaxyID());
		cfg.setProperty("PING.PORT",getPingPort());
		cfg.saveConfig();
		return cfg;
	}
	private static final int getPingPort() {
		return PING_PORT;
	}
	public static final String getDbName() {
		return DB_NAME;
	}

	public static final String getDbPass() {
		return DB_PASS;
	}
	
	
	public static final String getDbUrl() {
		return DB_URL;
	}
	public static final String getDbUser() {
		return DB_USER;
	}
	public static final String getFilePath() {
		return FILE_PATH;
	}
	public static final int getLoginPort() {
		return LOGIN_PORT;
	}
	public static final int getLoginSessionKeySize() {
		return LOGIN_SESSION_KEY_SIZE;
	}
	public static final String getLoginUrl() {
		return LOGIN_URL;
	}
	public static final int getZonePort() {
		return ZONE_PORT;
	}
	
	public static final String getZoneUrl() {
		return ZONE_URL;
	}
	
	public static final int getGalaxyID() {
		return GALAXY_ID;
	}

}
