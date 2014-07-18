package network;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.buffer.SimpleBufferAllocator;

import utils.collections.NonBlockingHashMap;

public class Client {
	
	private SocketAddress address;
	private Queue<IoBuffer> packetQueue = new LinkedBlockingQueue<IoBuffer>();
	private volatile boolean isOutOfOrder = false;
	private int recieved = 0;
	private int sent = 0;
	private Map<Short, IoBuffer> sentPackets = Collections.synchronizedMap(new TreeMap<Short, IoBuffer>());
	private Map<Short, IoBuffer> resentPackets = new NonBlockingHashMap<Short, IoBuffer>();
	private List<IoBuffer> currentFragmentedPackets = new ArrayList<IoBuffer>();
	private int currentFragTotalSize;
	private int currentFragRemainingSize;
	private short sequence;
	private short lastACKSequence;
	private long outOfOrderTimestamp;
	private SimpleBufferAllocator bufferPool = new SimpleBufferAllocator();
	private int crc;
	private int connectionId;
	private NetworkDispatch dispatch;
	private short nextSequence = 0;
	
	public Client(SocketAddress address, NetworkDispatch dispatch) {
		setAddress(address);
		setDispatch(dispatch);
	}
	
	public void sendPacket(IoBuffer packet) {
		packetQueue.add(packet);
	}

	public SocketAddress getAddress() {
		return address;
	}

	public void setAddress(SocketAddress address) {
		this.address = address;
	}

	public Queue<IoBuffer> getPacketQueue() {
		return packetQueue;
	}

	public void setPacketQueue(Queue<IoBuffer> packetQueue) {
		this.packetQueue = packetQueue;
	}

	public boolean isOutOfOrder() {
		return isOutOfOrder;
	}

	public void setOutOfOrder(boolean isOutOfOrder) {
		this.isOutOfOrder = isOutOfOrder;
	}

	public int getRecieved() {
		return recieved;
	}

	public void setRecieved(int recieved) {
		this.recieved = recieved;
	}

	public int getSent() {
		return sent;
	}

	public void setSent(int sent) {
		this.sent = sent;
	}

	public Map<Short, IoBuffer> getSentPackets() {
		return sentPackets;
	}

	public void setSentPackets(Map<Short, IoBuffer> sentPackets) {
		this.sentPackets = sentPackets;
	}

	public Map<Short, IoBuffer> getResentPackets() {
		return resentPackets;
	}

	public void setResentPackets(Map<Short, IoBuffer> resentPackets) {
		this.resentPackets = resentPackets;
	}
	
	public void addSentPacket(short sequence, IoBuffer packet) {
		sentPackets.put(sequence, packet);
	}
	
	public void removeSentPackets(short sequence) {
		Iterator<Entry<Short, IoBuffer>> it = sentPackets.entrySet().iterator();
		synchronized(sentPackets) {
			while(it.hasNext()) {
				Entry<Short, IoBuffer> e = it.next();
				if(e.getKey() > sequence)
					return;
				else
					it.remove();
			}
		}
	}
	
	public void addResentPacket(short sequence, IoBuffer packet) {
		resentPackets.put(sequence, packet);
	}
	
	public void removeResentPackets(short sequence) {
		Iterator<Entry<Short, IoBuffer>> it = resentPackets.entrySet().iterator();
		synchronized(resentPackets) {
			while(it.hasNext()) {
				Entry<Short, IoBuffer> e = it.next();
				if(e.getKey() > sequence)
					return;
				else
					it.remove();
			}
		}
	}

	
	public void incRecieved() {
		recieved++;
	}
	
	public void incSent() {
		sent++;
	}

	public short getSequence() {
		return sequence;
	}

	public void setSequence(short sequence) {
		this.sequence = sequence;
	}

	public short getLastACKSequence() {
		return lastACKSequence;
	}

	public void setLastACKSequence(short lastACKSequence) {
		this.lastACKSequence = lastACKSequence;
	}

	public long getOutOfOrderTimestamp() {
		return outOfOrderTimestamp;
	}

	public void setOutOfOrderTimestamp(long outOfOrderTimestamp) {
		this.outOfOrderTimestamp = outOfOrderTimestamp;
	}

	public SimpleBufferAllocator getBufferPool() {
		return bufferPool;
	}

	public void setBufferPool(SimpleBufferAllocator bufferPool) {
		this.bufferPool = bufferPool;
	}

	public int getCrc() {
		return crc;
	}

	public void setCrc(int crc) {
		this.crc = crc;
	}

	public NetworkDispatch getDispatch() {
		return dispatch;
	}

	public void setDispatch(NetworkDispatch dispatch) {
		this.dispatch = dispatch;
	}

	public void resendPackets(short sequence2) {
		Iterator<Short> it = sentPackets.keySet().iterator();
		synchronized(sentPackets) {
			while(it.hasNext()) {
				Short e = it.next();
				if(e == sequence) {
					it.remove();
					continue;
				}
				if(e < lastACKSequence)
					continue;
				if(resentPackets.containsKey(e))
					continue;
				if(e <= sequence) {
					dispatch.getServer().sendPacket(this, sentPackets.get(e));
					resentPackets.put(e, sentPackets.get(e));
				}
			}
		}
		
	}

	public List<IoBuffer> getCurrentFragmentedPackets() {
		return currentFragmentedPackets;
	}

	public void setCurrentFragmentedPackets(List<IoBuffer> currentFragmentedPackets) {
		this.currentFragmentedPackets = currentFragmentedPackets;
	}

	public int getCurrentFragTotalSize() {
		return currentFragTotalSize;
	}

	public void setCurrentFragTotalSize(int currentFragTotalSize) {
		this.currentFragTotalSize = currentFragTotalSize;
	}

	public int getCurrentFragRemainingSize() {
		return currentFragRemainingSize;
	}

	public void setCurrentFragRemainingSize(int currentFragRemainingSize) {
		this.currentFragRemainingSize = currentFragRemainingSize;
	}

	public int getConnectionId() {
		return connectionId;
	}

	public void setConnectionId(int connectionId) {
		this.connectionId = connectionId;
	}

	public short getNextSequence() {
		return nextSequence;
	}

	public void setNextSequence(short nextSequence) {
		this.nextSequence = nextSequence;
	}

}
