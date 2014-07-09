package services;

import java.util.Map;

import utils.Opcodes;
import network.PacketHandler;
import network.Service;

public class LoginService implements Service {

	@Override
	public void handlePackets(Map<Integer, PacketHandler> handlers) {
		handlers.put(Opcodes.LoginClientId, (client, packet) -> {
			
		});
	}

	@Override
	public void handleObjControllerPackets(Map<Integer, PacketHandler> handlers) {

	}

}
