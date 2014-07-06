package network;

import java.util.Map;

public interface Service {
	
	public void handlePackets(Map<Integer, PacketHandler> handlers);
	public void handleObjControllerPackets(Map<Integer, PacketHandler> handlers);

}
