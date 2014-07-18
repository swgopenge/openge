package network;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.mina.core.buffer.IoBuffer;

import protocol.ProtocolHandler;

public class NetworkDispatch {
	
	private ProtocolHandler handler;
	private boolean isZone;
	private ExecutorService threadPool = Executors.newCachedThreadPool();
	private ArrayList<Service> services;
	private ExecutorService eventThreadPool;
	private Map<Integer, PacketHandler> packetHandlers;
	private Map<Integer, PacketHandler> objControllerHandlers;
	private UDPServer server;

	public NetworkDispatch(ProtocolHandler handler, boolean isZone, UDPServer server) {
		this.handler = handler;
		this.isZone = isZone;
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
				packet.skip(2); // skip priority code
				int opcode = packet.getInt();
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

	public ArrayList<Service> getServices() {
		return services;
	}

	public void setServices(ArrayList<Service> services) {
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
