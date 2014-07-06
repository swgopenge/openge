package network;

import org.apache.mina.core.buffer.IoBuffer;

public interface PacketHandler {
	
	public void handlePacket(Client client, IoBuffer packet);
	public void handleObjControllerPacket(Client client, IoBuffer packet);

}
