package services.login;

import java.util.Map;

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
	private int loginType;
	private boolean autoRegistration;
	
	public LoginService(Core core) {
		this.core = core;
		requiredVersion = core.getConfig().getString("RequiredVersion");
		autoRegistration = core.getConfig().getInt("AutoRegistration") != 0;
		loginType = core.getConfig().getInt("LoginType");
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

	@Override
	public void handleObjControllerPackets(Map<Integer, PacketHandler> handlers) {

	}

}
