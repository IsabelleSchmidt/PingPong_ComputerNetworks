package client_server;

import java.io.*;
import java.net.*;
import java.util.*;

import javafx.beans.property.SimpleBooleanProperty;
import message.Message;
import message.Status;

public class Server {
	private static final double LOSS_RATE = 0.3;
	private static final int AVERAGE_DELAY = 100; // milliseconds
	private int port;
	private InetAddress IPAddress;
	DatagramSocket socket;
	
	private String name;
	private Status[] packets;
	private EndpointInfo serverInfo;
	
	public SimpleBooleanProperty serverOn = new SimpleBooleanProperty();
	private final String pong = "PONG";
	
	public Server(int port, String name) {
		try {
			this.IPAddress = InetAddress.getByName("localhost");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		this.port = port;
		this.name = name;
		this.packets = new Status[10];
		this.serverInfo = new EndpointInfo(IPAddress, port);
	}

	public void start() throws Exception {
		Random random = new Random();
		Message clientMessage;
		
		//Sende- und Empfangpunkt fuer das verarbeiten von Paketen
		socket = new DatagramSocket(port);
		serverOn.set(true);

		while (serverOn.get()) {
			DatagramPacket request = new DatagramPacket(new byte[1024], 1024);

			// Es passiert nichts, bis ein Paket empfangen wird
			try {
				socket.receive(request);
				clientMessage = new Message(request);
				packets[clientMessage.getNr()] = Status.RECEIVED;
				
				// Die enthaltene Nachricht wird dann geprintet
				//printData(request);
				System.out.println(this.name + ": Received from " + request.getAddress().getHostAddress() + ": " + clientMessage.toString());
	
				// Hier wird dann entschieden, ob ein Paketverlust simuliert wird, oder
				// tatsaechlich eine positive Antwort zu senden
				if (random.nextDouble() < LOSS_RATE) {
					System.out.println(this.name + ": " + "Simuliere Verzoegerung fuer PING " + clientMessage.getNr());
					packets[clientMessage.getNr()] = Status.LOST;
					continue;
				} else {
					Message message = new Message(pong, clientMessage.getNr(), clientMessage.getEndpoint());
					socket.send(message.getPacket());
					packets[clientMessage.getNr()] = Status.ACKED;
				}
	
				// Hier wird bei positivem Empfang die Verzoegerung des Programms simuliert
				Thread.sleep((int) (random.nextDouble() * 2 * AVERAGE_DELAY));
				
			} catch (IOException e) {
				if (!serverOn.get()) {
					System.out.println(this.name + ": server socket is closed.");
				} else {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public void stopServer() {
		serverOn.set(false);
		socket.close();
		System.out.println(this.name + ": server socket is closed.");
	}
	
	public int getPort() {
		return port;
	}

	public String getName() {
		return name;
	}

	public EndpointInfo getServerInfo() {
		return serverInfo;
	}
	
}
