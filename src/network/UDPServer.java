package network;

import java.util.List;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Map;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.buffer.SimpleBufferAllocator;

import protocol.Message;

public class UDPServer {
	
	public enum ServerState {
		Down,
		Up
	}
	
	private static int maxBytesPerTick = 30000;
	
	private int port;
	private DatagramSocket socket;
	private Thread sendThread;
	private Thread receiveThread;
	private ServerState state = ServerState.Down;
	private Map<SocketAddress, Client> clients = new ConcurrentHashMap<SocketAddress, Client>();
	private DatagramPacket recvPacket = new DatagramPacket(new byte[496], 496);
	private DatagramPacket sendPacket = new DatagramPacket(new byte[496], 496);
	private SimpleBufferAllocator bufferPool = new SimpleBufferAllocator();
	private NetworkDispatch dispatch;
	private int sendQueueDelay;
	
	public UDPServer(int port, int sendQueueDelay) {
		this.port = port;
		this.sendQueueDelay = sendQueueDelay;
	}
	
	public void start() {
		try {
			state = ServerState.Up;
			this.socket = new DatagramSocket(port);
			receiveThread = new Thread(() -> {
				while(state == ServerState.Up)
					receive();
			});
			if(dispatch != null) {
				sendThread = new Thread(() -> {
					while(state == ServerState.Up) {
						try {
							if(sendQueueDelay > 0)
								Thread.sleep(sendQueueDelay);
						} catch (Exception e) {
							e.printStackTrace();
						}
						send();
					}
				});
				sendThread.start();
			}
			receiveThread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("UDP Server listening on port " + port);
	}

	private void send() {
		for(Client client : clients.values()) {
			if(client.isOutOfOrder())
				continue;
			Queue<IoBuffer> packets = client.getPacketQueue();
			if(sendQueueDelay < 0) {
				for(IoBuffer packet : packets)
					sendPacket(client, packet);
				packets.clear();
				continue;
			}
			List<IoBuffer> packetsToEncode = new ArrayList<IoBuffer>();
			int bytes = 0;
			while(bytes < maxBytesPerTick || packets.isEmpty()) {
				IoBuffer packet = packets.poll();
				if(packet == null)
					break;
				packetsToEncode.add(packet);
				bytes += packet.array().length;
			}
			List<IoBuffer> encoded = dispatch.getHandler().encode(client, packetsToEncode);
			if(encoded == null)
				continue;
			for(IoBuffer buffer : encoded)
				sendPacket(client, buffer);
		}
	}

	private void receive() {
		try {
			socket.receive(recvPacket);
			IoBuffer buffer = bufferPool.allocate(recvPacket.getLength(), false).put(recvPacket.getData(), 0, recvPacket.getLength());
			buffer.position(0);
			SocketAddress address = recvPacket.getSocketAddress();
			Client client = clients.get(address);
			if(client == null) {
				client = new Client(address, dispatch);
				clients.put(address, client);
			}	
			if(dispatch != null) {
				for(IoBuffer packet : dispatch.getHandler().decode(client, buffer))
					dispatch.onRecieve(client, packet);
			}
			else
				sendPacket(client, buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendPacket(Client client, IoBuffer packet) {
		try {
			client.incSent();
			sendPacket.setSocketAddress(client.getAddress());
			sendPacket.setData(packet.array());
			socket.send(sendPacket);
			packet.free();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public DatagramSocket getSocket() {
		return socket;
	}

	public void setSocket(DatagramSocket socket) {
		this.socket = socket;
	}

	public Thread getSendThread() {
		return sendThread;
	}

	public void setSendThread(Thread sendThread) {
		this.sendThread = sendThread;
	}

	public Thread getRecieveThread() {
		return receiveThread;
	}

	public void setRecieveThread(Thread receiveThread) {
		this.receiveThread = receiveThread;
	}

	public Map<SocketAddress, Client> getClients() {
		return clients;
	}

	public void setClients(Map<SocketAddress, Client> clients) {
		this.clients = clients;
	}

	public NetworkDispatch getDispatch() {
		return dispatch;
	}

	public void setDispatch(NetworkDispatch dispatch) {
		this.dispatch = dispatch;
	}

	public int getSendQueueDelay() {
		return sendQueueDelay;
	}

	public void setSendQueueDelay(int sendQueueDelay) {
		this.sendQueueDelay = sendQueueDelay;
	}

	public DatagramPacket getSendPacket() {
		return sendPacket;
	}

	public void setSendPacket(DatagramPacket sendPacket) {
		this.sendPacket = sendPacket;
	}
	
	public void removeClient(Client client) {
		clients.remove(client.getAddress());
	}

}
