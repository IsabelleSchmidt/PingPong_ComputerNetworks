package client_server;

import java.util.Timer;
import message.Status;

/**
 * Klasse enthält Daten, die in der Kommunikation mit dem bestimmten Server entstanden sind.
 * @author 
 *
 */
public class EndpointData {

	private Status[] packets = new Status[10];
	private long[] timeSent = new long[10];
	private long[] timeAcked = new long[10];
	
	private int numberOfTimeouts = 0;
	private Timer timeoutTimer;
	
	public EndpointData() {
		timeoutTimer = new Timer(true);
	}
	
	public void setPacketStatus(int packetNr, Status status) {
		packets[packetNr] = status;
	}
	
	public Status getPacketStatus(int packetNr) {
		return packets[packetNr];
	}
	
	public void setAckTime(int packetNr) {
		timeAcked[packetNr] = System.currentTimeMillis();
	}
	
	public void setSentTime(int packetNr) {
		timeSent[packetNr] = System.currentTimeMillis();
	}
	
	public long rtt(int messageNr) {
		return (long)(timeAcked[messageNr] - timeSent[messageNr]);
	}

	public int getNumberOfTimeouts() {
		return numberOfTimeouts;
	}
	

	public void setNumberOfTimeouts() {
		this.numberOfTimeouts++;
	}

	public Status[] getPackets() {
		return packets;
	}

	public Timer getTimeoutTimer() {
		return timeoutTimer;
	}

}
