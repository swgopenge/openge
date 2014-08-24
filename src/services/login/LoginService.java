package services.login;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import database.odb.ODBCursor;
import database.odb.ObjectDatabase;
import protocol.swg.ErrorMessage;
import protocol.swg.LoginClientId;
import utils.Opcodes;
import main.Core;
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
	private byte loginType;
	private boolean autoRegistration;
    private Random rand = new Random();
	private ObjectDatabase accountODB;
    private AtomicLong highestAccountId;

	public LoginService(Core core) {
		this.core = core;
		requiredVersion = core.getConfig().getString("RequiredVersion");
		autoRegistration = core.getConfig().getBoolean("AutoRegistration");
		loginType = core.getConfig().getByte("LoginType");
        accountODB = core.getObjectDatabase(0);
        highestAccountId = new AtomicLong(0);
        ODBCursor cursor = accountODB.getCursor();
        while(cursor.hasNext()) {
            Account acc = (Account) cursor.next();
            if(acc == null)
                continue;
            if(acc.getId() > highestAccountId.get())
                highestAccountId.set(acc.getId());
        }
    }

	@Override
	public void handlePackets(Map<Integer, PacketHandler> handlers) {
		handlers.put(Opcodes.LoginClientId, (client, packet) -> {
			
			LoginClientId clientId = new LoginClientId();
			clientId.deserialize(packet);
			
			String userName = clientId.getAccountName();
			String password = clientId.getPassword();
			String version = clientId.getVersion();
			System.out.println(version);
			
			if(!version.equals(requiredVersion)) {
				ErrorMessage errMsg = new ErrorMessage("Invalid Version", "You have an invalid Client version installed, please update to the correct version.");
				client.sendPacket(errMsg.serialize());
				return;
			}
			
			
			
		});
	}

    public boolean isAutoRegistration() {
        return autoRegistration;
    }

    public long generateAccountId() {
        return highestAccountId.incrementAndGet();
    }

	@Override
	public void handleObjControllerPackets(Map<Integer, PacketHandler> handlers) {

	}

}
