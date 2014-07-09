package network;

import org.apache.mina.core.buffer.IoBuffer;

@FunctionalInterface
public interface PacketHandler {
	
	public void handlePacket(Client client, IoBuffer packet) throws Exception;

}
