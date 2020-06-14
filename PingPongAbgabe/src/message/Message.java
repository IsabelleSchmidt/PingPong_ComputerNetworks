package message;

import java.net.DatagramPacket;
import java.net.InetAddress;

import client_server.EndpointInfo;

public class Message {
	
	private int messageNr;
	private DatagramPacket packet;
	private InetAddress host; 
	private int port;
	private String messageText;
	private EndpointInfo endpoint;

	public Message(DatagramPacket packet) {
		String messageText = new String(packet.getData());
		
		this.packet = packet;
		this.messageText = messageText.split("\\s")[0];
		this.messageNr = Integer.parseInt(messageText.split("\\s")[1]);
		this.host = packet.getAddress();
		this.port = packet.getPort();
		this.endpoint = new EndpointInfo(host, port);
	}
	
	public Message(String message, int messageNr, EndpointInfo endpoint) {
		String messageText = message + " " + messageNr + "\n";
		this.messageText = message;
		this.messageNr = messageNr;
		this.host = endpoint.getAddress();
		this.port = endpoint.getPort();
		this.packet = new DatagramPacket(messageText.getBytes(), messageText.length(), host, port);
		this.endpoint = endpoint;
	}
	

	public EndpointInfo getEndpoint() {
		return endpoint;
	}

	public int getNr() {
		return messageNr;
	}
	
	@Override
	public String toString() {
		return messageText + " " + messageNr;
	}

	public DatagramPacket getPacket() {
		return packet;
	}

	public InetAddress getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getMessageText() {
		return messageText;
	}


}
