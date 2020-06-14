package client_server;

import java.io.*;
import java.net.*;
import java.util.*;

import message.Message;
import message.Status;

public class Server {
	private static final double LOSS_RATE = 0.3;
	private static final int AVERAGE_DELAY = 100; // milliseconds
	private int port;
	private InetAddress IPAddress;
	private String name;
	private Status[] packets;
	private EndpointInfo serverInfo;
	
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
		
		//Sende- und Empfangpunkt f�r das verarbeiten von Paketen
		DatagramSocket socket = new DatagramSocket(port);

		while (true) {
			DatagramPacket request = new DatagramPacket(new byte[1024], 1024);

			// Es passiert nichts, bis ein Paket empfangen wird
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
		}
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
	
	
	

	// um die Ping-Daten auszugeben im Standard Output-Stream
	/*private void printData(DatagramPacket request) throws Exception {
		byte[] bytee = request.getData();

		// Die Daten werden in ein Input-Array gespeichert, um diese auslesen zu koennen
		ByteArrayInputStream bais = new ByteArrayInputStream(bytee);

		// der zuvor erstelle Input Stream wird in deinen Stream Reader gespeichert,
		// damit man die Daten als String lesen kann
		InputStreamReader isr = new InputStreamReader(bais);

		// Das ganze wird dann in einen Buffered Reader gespeichert, damit man die Daten
		// zeilenweise auslesen
		BufferedReader br = new BufferedReader(isr);

		// Dann wird eine Line ausgelesen
		String line = br.readLine();

		// Daraus ausgelesen werden die Daten ausgegeben
		System.out.println(this.name + ": Received from " + request.getAddress().getHostAddress() + ": " + line);
	}*/


	
}
