package services.login;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import database.odb.ODBCursor;
import database.odb.ObjectDatabase;
import protocol.swg.CharacterCreationDisabled;
import protocol.swg.EnumerateCharacterId;
import protocol.swg.ErrorMessage;
import protocol.swg.LoginClientId;
import protocol.swg.LoginClientToken;
import protocol.swg.LoginClusterStatus;
import protocol.swg.LoginEnumCluster;
import protocol.swg.ServerNowEpochTime;
import protocol.swg.StationIdHasJediSlot;
import utils.Opcodes;
import main.Core;
import network.Client;
import network.PacketHandler;
import network.Service;

public class LoginService implements Service {
	
	public enum LoginType {
		Local,
		VBulletin,
		PHPBB
	}
	
	private Core core;
	private String requiredVersion;
	private LoginType loginType;
	private boolean autoRegistration;
    private Random rand = new Random();
	private ObjectDatabase accountODB;
	private ObjectDatabase characterODB;
    private AtomicLong highestAccountId;
    private ILoginProvider loginProvider;
    private String serverIP;
    private int maxCharacters;

	public LoginService(Core core) {
		this.core = core;
		requiredVersion = core.getConfig().getString("RequiredVersion");
		autoRegistration = core.getConfig().getBoolean("AutoRegistration");
		serverIP = core.getConfig().getString("ServerIP");
		maxCharacters = core.getConfig().getInt("MaxCharacters");
		byte index = core.getConfig().getByte("LoginType");
		if(index < 0 || index > 2) 
			index = 0;
		loginType = LoginType.values()[index];
        accountODB = core.getObjectDatabase(0);
		switch (loginType) {
		
			case Local:
				loginProvider = new LocalDbLoginProvider(accountODB);
				break;
			// TODO: implement these
			case VBulletin:
			case PHPBB:
			default:
				loginProvider = new LocalDbLoginProvider(accountODB);
				break;
					
			
		}
        characterODB = core.getObjectDatabase(1);
        highestAccountId = new AtomicLong(0);
        ODBCursor cursor = accountODB.getCursor();
        while(cursor.hasNext()) {
            Account acc = (Account) cursor.next();
            if(acc == null)
                continue;
            if(acc.getId() > highestAccountId.get())
                highestAccountId.set(acc.getId());
        }
        cursor.close();
    }

	@Override
	public void handlePackets(Map<Integer, PacketHandler> handlers) {
		handlers.put(Opcodes.LoginClientId, (client, packet) -> {
			
			LoginClientId clientId = new LoginClientId();
			clientId.deserialize(packet);
			
			String userName = clientId.getAccountName();
			String password = clientId.getPassword();
			String version = clientId.getVersion();
			
			if(!version.equals(requiredVersion)) {
				ErrorMessage errMsg = new ErrorMessage("Invalid Version", "You have an invalid Client version installed, please update to the correct version.");
				client.sendPacket(errMsg.serialize());
				return;
			}
			
			long accountID = loginProvider.getAccountId(userName, password, client.getAddress().toString());

			if(accountID == -1) {
				ErrorMessage errMsg = new ErrorMessage("DB Error", "The database has encountered a problem while trying to retrieve your account.");
				client.sendPacket(errMsg.serialize());
				return;				
			} else if(accountID == -2) {
				ErrorMessage errMsg = new ErrorMessage("Invalid account name", "An account with that name does not exist.");
				client.sendPacket(errMsg.serialize());
				return;								
			} else if(accountID == -3) {
				ErrorMessage errMsg = new ErrorMessage("Invalid password", "The given password is not valid.");
				client.sendPacket(errMsg.serialize());
				return;												
			} else if(accountID == -4) {
				ErrorMessage errMsg = new ErrorMessage("Account banned", "Your account is currently banned from playing on this server.");
				client.sendPacket(errMsg.serialize());
				return;												
			}
			
			client.setAccountID(accountID);
			// The session key will be send back to the client in LoginClientToken so the client can use it for connecting to the zone server
			client.setSessionKey(generateSessionKey());
			
			LoginClientToken clientToken = new LoginClientToken(client.getSessionKey(), (int) accountID, userName);
			LoginEnumCluster servers = getLoginCluster();
			LoginClusterStatus serverStatus = getLoginClusterStatus(client);
			EnumerateCharacterId characters = getEnumerateCharacterId(accountID);
			CharacterCreationDisabled charCreationDisabled = new CharacterCreationDisabled(0);
			StationIdHasJediSlot jediSlot = new StationIdHasJediSlot(false);
			ServerNowEpochTime time = new ServerNowEpochTime((int) (System.currentTimeMillis() / 1000));
			
			client.sendPacket(time.serialize());
			client.sendPacket(clientToken.serialize());
			client.sendPacket(servers.serialize());			
			client.sendPacket(charCreationDisabled.serialize());
			client.sendPacket(serverStatus.serialize());
			client.sendPacket(jediSlot.serialize());
			client.sendPacket(characters.serialize());
			
		});
	}

    private EnumerateCharacterId getEnumerateCharacterId(long accountID) {
    	Account account = (Account) accountODB.get(accountID);
    	EnumerateCharacterId enumCharacters = new EnumerateCharacterId();
    	if(account == null)
    		return enumCharacters;
    	for(long characterId : account.getCharacters()) {
    		Character character = (Character) characterODB.get(characterId);
    		if(character == null)
    			continue;
    		enumCharacters.addCharacter(character.getName(), character.getAppearanceCRC(), characterId, 1, character.getType());
    	}
		return enumCharacters;
	}

	private LoginClusterStatus getLoginClusterStatus(Client client) {
		LoginClusterStatus clusterStatus = new LoginClusterStatus();
		clusterStatus.addServer(1, serverIP, core.getZoneServer().getPort(), core.getPingServer().getPort(), maxCharacters, core.getGalaxyStatus().ordinal(), 0, core.getNumberOfConnectedCharacters());
		return clusterStatus;
	}

	private LoginEnumCluster getLoginCluster() {
		LoginEnumCluster enumCluster = new LoginEnumCluster(maxCharacters);
		enumCluster.addServer(1, core.getGalaxyName());
		return enumCluster;
	}

	public boolean isAutoRegistration() {
        return autoRegistration;
    }
    
    /**
     * Generates the next account ID.
     * @return the generated account ID.
     */
    public long generateAccountId() {
        return highestAccountId.incrementAndGet();
    }
    
	/**
	 * Generates random Session Key.
	 * @return random Session Key.
	 */
	private byte[] generateSessionKey() {
		byte[] bytes = new byte[24];
		rand.nextBytes(bytes);
		return bytes;
	}


	@Override
	public void handleObjControllerPackets(Map<Integer, PacketHandler> handlers) {

	}

}
