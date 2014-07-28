package network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.nio.ByteOrder;
import org.apache.mina.core.buffer.IoBuffer;

import protocol.ProtocolHandler;

public class NetworkDispatch {
	
	private ProtocolHandler handler;
	private boolean isZone;
	private ExecutorService threadPool = Executors.newCachedThreadPool();
	private List<Service> services = new ArrayList<Service>();
	private ExecutorService eventThreadPool;
	private Map<Integer, PacketHandler> packetHandlers = new HashMap<Integer, PacketHandler>();
	private Map<Integer, PacketHandler> objControllerHandlers = new HashMap<Integer, PacketHandler>();
	private UDPServer server;

	public NetworkDispatch(ProtocolHandler handler, boolean isZone, UDPServer server) {
		this.handler = handler;
		this.isZone = isZone;
		this.server = server;
	}

	public ProtocolHandler getHandler() {
		return handler;
	}

	public void setHandler(ProtocolHandler handler) {
		this.handler = handler;
	}
	
	public void addService(Service service) {
		service.handlePackets(packetHandlers);
		service.handleObjControllerPackets(objControllerHandlers);
		services.add(service);
	}
	
	public void onRecieve(Client client, IoBuffer packet) {
		if(packet == null)
			return;
		threadPool.execute(() -> {
				packet.position(0);		
				if(!packet.hasRemaining())
					return;
				packet.order(ByteOrder.LITTLE_ENDIAN);
				int opcode = packet.getInt(2);
				PacketHandler handler = null;
				if(opcode == 0x80CE5E46) {
					handler = objControllerHandlers.get(opcode);
					if(handler == null) {
						//System.out.println("Unhandled Obj Controller Opcode: " + opcode);
					}
				} else {
					handler = packetHandlers.get(opcode);
					if(handler == null) {
						//System.out.println("Unhandled Opcode: " + opcode);
					}
				}
				packet.position(0);
				if(handler != null) {
					try {
						handler.handlePacket(client, packet);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				packet.free();
		});
	}

	public boolean isZone() {
		return isZone;
	}

	public void setZone(boolean isZone) {
		this.isZone = isZone;
	}

	public List<Service> getServices() {
		return services;
	}

	public void setServices(List<Service> services) {
		this.services = services;
	}

	public ExecutorService getEventThreadPool() {
		return eventThreadPool;
	}

	public void setEventThreadPool(ExecutorService eventThreadPool) {
		this.eventThreadPool = eventThreadPool;
	}

	public Map<Integer, PacketHandler> getPacketHandlers() {
		return packetHandlers;
	}

	public void setPacketHandlers(Map<Integer, PacketHandler> packetHandlers) {
		this.packetHandlers = packetHandlers;
	}

	public Map<Integer, PacketHandler> getObjControllerHandlers() {
		return objControllerHandlers;
	}

	public void setObjControllerHandlers(Map<Integer, PacketHandler> objControllerHandlers) {
		this.objControllerHandlers = objControllerHandlers;
	}

	public UDPServer getServer() {
		return server;
	}

	public void setServer(UDPServer server) {
		this.server = server;
	}

}
