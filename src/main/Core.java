package main;

import protocol.ProtocolHandler;
import protocol.SoeProtocolHandler;
import network.NetworkDispatch;
import network.UDPServer;
import utils.*;

public class Core {
	
	public static Core instance;
	private GalaxyStatus galaxyStatus = GalaxyStatus.Offline;
	private Config config;
	private long galacticTime = System.currentTimeMillis();
	private UDPServer loginServer;
	private UDPServer pingServer;
	private UDPServer zoneServer;
	private NetworkDispatch loginDispatch;
	private NetworkDispatch zoneDispatch;
	
	public enum GalaxyStatus {
		Offline,
		Loading,
		Online,
		Locked
	}
	
	public Core() {
		instance = this;
		config = new Config();
		config.setFilePath("config.cfg");
		if (!(config.loadConfigFile())) {
			config = DefaultConfig.getConfig();
		}
	}
	
	public void start() {
		setGalaxyStatus(GalaxyStatus.Loading);
		pingServer = new UDPServer(config.getInt("PING.PORT"), 0);
		loginServer = new UDPServer(config.getInt("LOGIN.PORT"), 5);
		zoneServer = new UDPServer(config.getInt("ZONE.PORT"), 5);
		loginDispatch = new NetworkDispatch(new SoeProtocolHandler(), false, loginServer);
		zoneDispatch = new NetworkDispatch(new SoeProtocolHandler(), true, zoneServer);
		loginServer.setDispatch(loginDispatch);
		zoneServer.setDispatch(zoneDispatch);
		pingServer.start();
		loginServer.start();
		zoneServer.start();
		setGalaxyStatus(GalaxyStatus.Online);
	}
	
	public static void main(String args[]) throws Exception {
		Core core = new Core();
		core.start();
		while(core.getGalaxyStatus() != GalaxyStatus.Offline) {
			Thread.sleep(1000);
		}
	}
	
	public void stop() {
		setGalaxyStatus(GalaxyStatus.Offline);		
	}
	
	public static Core getInstance() {
		return instance;
	}

	public GalaxyStatus getGalaxyStatus() {
		return galaxyStatus;
	}

	public void setGalaxyStatus(GalaxyStatus galaxyStatus) {
		this.galaxyStatus = galaxyStatus;
	}
	
	public void lockGalaxy() {
		setGalaxyStatus(GalaxyStatus.Locked);
	}

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}

	public long getGalacticTime() {
		return galacticTime;
	}

	public void setGalacticTime(long galacticTime) {
		this.galacticTime = galacticTime;
	}

	public UDPServer getLoginServer() {
		return loginServer;
	}

	public void setLoginServer(UDPServer loginServer) {
		this.loginServer = loginServer;
	}

	public UDPServer getPingServer() {
		return pingServer;
	}

	public void setPingServer(UDPServer pingServer) {
		this.pingServer = pingServer;
	}

	public UDPServer getZoneServer() {
		return zoneServer;
	}

	public void setZoneServer(UDPServer zoneServer) {
		this.zoneServer = zoneServer;
	}

	public NetworkDispatch getLoginDispatch() {
		return loginDispatch;
	}

	public void setLoginDispatch(NetworkDispatch loginDispatch) {
		this.loginDispatch = loginDispatch;
	}

	public NetworkDispatch getZoneDispatch() {
		return zoneDispatch;
	}

	public void setZoneDispatch(NetworkDispatch zoneDispatch) {
		this.zoneDispatch = zoneDispatch;
	}

}
