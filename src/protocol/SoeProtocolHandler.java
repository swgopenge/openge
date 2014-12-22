package protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import network.Client;

import org.apache.mina.core.buffer.IoBuffer;

import protocol.soe.FragmentedChannelA;
import protocol.soe.MultiProtocol;
import protocol.soe.DataChannelA;
import protocol.soe.NetStatsClient;
import protocol.soe.NetStatsServer;
import protocol.soe.Ping;
import protocol.soe.SessionResponse;
import protocol.soe.SessionResponse.EncryptionType;
import protocol.swg.LoginServerId;
import protocol.swg.LoginServerString;
import utils.Utilities;

public class SoeProtocolHandler implements ProtocolHandler {
	
	public enum DisconnectReason {
		None,
		ICMPError,
		Timeout,
		OtherSideTerminated,
		ManagerDeleted,
		ConnectFail,
		Application,
		UnreachableConnection,
		UnacknowledgedTimeout,
		NewConnectionAttempt,
		ConnectionRefused,
		MutualConnectError,
		ConnectToSelf,
		ReliableOverflow
	}
	
	private MessageCRC messageCRC = new MessageCRC();
	private MessageEncryption messageEncryption = new MessageEncryption();
	private MessageCompression messageCompression = new MessageCompression();
	private Random crcGenerator = new Random();
	
	@Override
	public List<IoBuffer> decode(Client client, IoBuffer buffer) {
		
		short opcode = buffer.getShort();
		buffer.position(0);
		
		if((opcode == 1 && client.getCrc() != 0) || (opcode != 1 && client.getCrc() == 0)) {
			return null;
		} else if(opcode != 1 && client.getCrc() != 0) {
			int length = Utilities.getActiveLengthOfBuffer(buffer);
			byte [] data = new byte[length];
			System.arraycopy(buffer.array(), 0, data, 0, length);
			data = messageCompression.decompress(
						messageEncryption.decrypt(
							messageCRC.validate(data, client.getCrc()), client.getCrc()));
			buffer.clear();
			buffer.setAutoExpand(true);
			buffer.position(0);
			buffer.put(data);
			buffer.flip();
			buffer.position(0);
			if(!buffer.hasRemaining() || !buffer.hasArray() || Utilities.getActiveLengthOfBuffer(buffer) < 4)
				return null;
		}
		
		switch(opcode) {
		
			case 1:
				handleSessionRequest(client, buffer);
				break;
			case 3:
				return handleMulti(client, buffer);
			case 5:
				handleDisconnect(client, buffer);
				break;
			case 6:
				handlePing(client, buffer);
				break;
			case 7:
				handleNetStatsClient(client, buffer);
				break;
			case 9:
				return handleDataA(client, buffer);
			case 13:
				return handleFragmentedA(client, buffer);
			case 17:
				handleOutOfOrder(client, buffer);
				break;
			case 21:
				handleAcknowledgementA(client, buffer);
				break;
				
			default:
				break;
		
		}
		
		return null;
	}
	
	private void handleAcknowledgementA(Client client, IoBuffer buffer) {
		
		short sequence = buffer.getShort(2);
		if(sequence < client.getLastACKSequence())
			return;
		client.setOutOfOrder(false);
		client.setLastACKSequence(sequence);
		client.removeSentPackets(sequence);
		client.removeResentPackets(sequence);
	}

	private void handleOutOfOrder(Client client, IoBuffer buffer) {
		
		short sequence = buffer.getShort(2);
		short lastACKSequence = client.getLastACKSequence();
		System.out.println("OutOfOrder recieved for sequence: " + sequence + " last ACK sequence: " + lastACKSequence);
		if(!client.isOutOfOrder()) {
			client.setOutOfOrder(true);
			client.setOutOfOrderTimestamp(System.currentTimeMillis());
		}

		Map<Short, IoBuffer> resentPackets = client.getResentPackets();
		
		if(System.currentTimeMillis() - client.getOutOfOrderTimestamp() < 5000)
			resentPackets.clear();
		
		client.resendPackets(sequence);
	}

	private List<IoBuffer> handleFragmentedA(Client client, IoBuffer buffer) {
		buffer.skip(2);
		short sequence = buffer.getShort();
		sendAcknowledgement(client, sequence);
		if(!buffer.hasRemaining())
			return null;
		List<IoBuffer> currentFrags = client.getCurrentFragmentedPackets();
		int remainingFragSize = client.getCurrentFragRemainingSize();
		
		if(currentFrags.size() == 0) {
			remainingFragSize = buffer.getInt();
			client.setCurrentFragTotalSize(remainingFragSize);
		}
		
		remainingFragSize -= buffer.remaining();
		currentFrags.add(buffer.getSlice(buffer.remaining()));
		
		if(remainingFragSize <= 0) {
			int totalSize = client.getCurrentFragTotalSize();
			IoBuffer finalData = client.getBufferPool().allocate(totalSize, false);
			for(IoBuffer fragment : currentFrags) {
				finalData.put(fragment);
			}
			currentFrags.clear();
			client.setCurrentFragRemainingSize(0);
			client.setCurrentFragTotalSize(0);
			List<IoBuffer> packets = new ArrayList<IoBuffer>();
			packets.add(finalData);
			return packets;
		}
		
		
		client.setCurrentFragRemainingSize(remainingFragSize);
		return null;
	}

	private void sendAcknowledgement(Client client, short sequence) {
		IoBuffer packet = client.getBufferPool().allocate(4, false);
		packet.putShort((short) 21);
		packet.putShort(sequence);
		packet.flip();
		client.sendPacket(packet);
	}

	private List<IoBuffer> handleDataA(Client client, IoBuffer buffer) {
		short sequence = buffer.getShort(2);
		sendAcknowledgement(client, sequence);
		if(buffer.remaining() <= 4)
			return null;
		List<IoBuffer> packets = new ArrayList<IoBuffer>();
		buffer.position(0);
		DataChannelA dataA = new DataChannelA(buffer, client.getBufferPool());
		for (IoBuffer messageData : dataA.getMessages()) {
			if (messageData != null) {
				messageData.flip();
				packets.add(messageData);
			}
		}		
		return packets;
	}

	private void handleNetStatsClient(Client client, IoBuffer buffer) {
		NetStatsClient netStatsClient = new NetStatsClient();
		netStatsClient.deserialize(buffer);
		NetStatsServer netStatsServer = new NetStatsServer(netStatsClient.getClientTickCount(), 0, netStatsClient.getClientPacketsSent(), netStatsClient.getClientPacketsReceived(), client.getSent(), client.getRecieved());
		client.sendPacket(netStatsServer.serialize());
	}

	private void handlePing(Client client, IoBuffer buffer) {
		client.sendPacket(new Ping().serialize()); 
	}

	private void handleDisconnect(Client client, IoBuffer buffer) {
		buffer.skip(6);
		short reasonId = buffer.getShort();
		if(reasonId < 0 || reasonId > 13) {
			System.err.println("Unknown disconnect reason ID: " + reasonId);
			return;
		}
		System.out.println("Client disconnecting for reason: " + DisconnectReason.values()[reasonId].toString());
		client.getDispatch().getServer().removeClient(client);
	}

	private List<IoBuffer> handleMulti(Client client, IoBuffer buffer) {
		MultiProtocol multi = new MultiProtocol(buffer);
		List<IoBuffer> packets = new ArrayList<IoBuffer>();
		for (IoBuffer data : multi.getMessages()) {
			if (data != null && data.hasArray() && data.array().length >= 2) {
				// SOE Packet
				if ((data.get(0) == 0x00 && data.get(1) > 0x00 && data.get(1) < 0x1E)) {
					decode(client, data);
				}
				// SWG Packet
				else {
					packets.add(data);
				}
			}
		}	
		return packets;
	}

	private void handleSessionRequest(Client client, IoBuffer buffer) {
		buffer.skip(2);
		int crcLength = buffer.getInt();
		int connectionId = buffer.getInt();
		int clientUDPSize = buffer.getInt();
		client.setConnectionId(connectionId);
		client.setCrc(crcGenerator.nextInt());
		SessionResponse response = new SessionResponse(connectionId, client.getCrc(), crcLength, true, EncryptionType.XOR, clientUDPSize);
		client.sendPacket(response.serialize());
	}

	@Override
	public List<IoBuffer> encode(Client client, List<IoBuffer> packets) {
		
		if(packets == null || packets.isEmpty())
			return null;
		
		int crcSeed = client.getCrc();
		
		List<IoBuffer> packed = new ArrayList<IoBuffer>();
		for(IoBuffer packet : packets) {
			if(Utilities.IsSOETypeMessage(packet.array()))
				packed.add(packet);
		}

		DataChannelA dataChannelA = new DataChannelA(client.getBufferPool());
		for(IoBuffer packet : packets) {
			if (packet == null || packed.contains(packet)) continue;
			if (packet.array().length < 6 || packet.limit() < 6) continue;
			int opcode = packet.getInt(2);
			if(opcode == 0x1B24F808 || opcode == 0xC867AB5A) // send movement packets as unreliable packets
				continue;
			if (!dataChannelA.addMessage(packet) && packet.array().length <= 487) {
				packed.add(dataChannelA.serialize());
				dataChannelA = new DataChannelA(client.getBufferPool());
				dataChannelA.addMessage(packet);
			} else if(packet.array().length > 487) {
				if (dataChannelA.hasMessages()) {
					packed.add(dataChannelA.serialize());
					dataChannelA = new DataChannelA(client.getBufferPool());
				}
				FragmentedChannelA fragChanA = new FragmentedChannelA(client.getBufferPool());
				for (FragmentedChannelA fragChanASection : fragChanA.create(packet.array())) {
					packed.add(fragChanASection.serialize());
				}
			}
		}
		
		if (dataChannelA.hasMessages()) 
			packed.add(dataChannelA.serialize());
		
		MultiProtocol multiProtocol = new MultiProtocol(client.getBufferPool());
		for (IoBuffer packet : packets) {
			if (packet == null || packed.contains(packet)) continue;
			if (packet.array().length < 6 || packet.limit() < 6) continue;
			int opcode = packet.getInt(2);
			if(opcode != 0x1B24F808 && opcode != 0xC867AB5A)
				continue;
			if(packet.array().length > 255)
				continue;
			if(!multiProtocol.addSWGMessage(packet)) {
				packed.add(multiProtocol.serialize());
				 multiProtocol = new MultiProtocol(client.getBufferPool());
				 multiProtocol.addSWGMessage(packet); 
			}
			
		}
		
		if (multiProtocol.hasMessages())
			packed.add(multiProtocol.serialize());

		List<IoBuffer> out = new ArrayList<IoBuffer>(packed.size());
		for(IoBuffer packedPacket : packed) {
			
			short opcode = packedPacket.getShort(0);
			if(opcode == 2) { // dont encrypt/compress session response
				out.add(packedPacket);
				continue;
			}
			if(opcode == 9 || opcode == 13) {
				short nextSequence = client.getNextSequence();
				packedPacket.putShort(2, nextSequence);
				client.setNextSequence((short) (nextSequence + 1));
				byte[] packet = messageCRC.append(
									messageEncryption.encrypt(
											messageCompression.compress(packedPacket.array()), crcSeed), crcSeed);
				IoBuffer outPacket = client.getBufferPool().allocate(packet.length, false).put(packet).flip();
				out.add(outPacket);
				client.addSentPacket(nextSequence, outPacket);
			} else {
				byte[] packet = null;
				packet = messageCRC.append(
							messageEncryption.encrypt(
									messageCompression.compress(packedPacket.array()), crcSeed), crcSeed);

				IoBuffer outPacket = client.getBufferPool().allocate(packet.length, false).put(packet).flip();				
				out.add(outPacket);
			}
			
		}
		
		return out;
	}

	public MessageCRC getMessageCRC() {
		return messageCRC;
	}

	public void setMessageCRC(MessageCRC messageCRC) {
		this.messageCRC = messageCRC;
	}

	public MessageEncryption getMessageEncryption() {
		return messageEncryption;
	}

	public void setMessageEncryption(MessageEncryption messageEncryption) {
		this.messageEncryption = messageEncryption;
	}

	public MessageCompression getMessageCompression() {
		return messageCompression;
	}

	public void setMessageCompression(MessageCompression messageCompression) {
		this.messageCompression = messageCompression;
	}
	

}
