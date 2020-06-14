package client_server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import message.Message;
import message.Status;

public class Client {
	
	//public boolean socketOn;
	private DatagramSocket socket;
	private InetAddress IPAddress;
	private final String ping = "PING";
	private final int timeOut = 1000; // time of timeout in milliseconds
	
	Map<EndpointInfo, EndpointData> serverMap = new HashMap<>();
	
	public SimpleBooleanProperty socketOn = new SimpleBooleanProperty();
	public ObservableList<String> obsList1 = FXCollections.observableArrayList();
	public ObservableList<String> obsList2 = FXCollections.observableArrayList();
	
	public Client() {
		try {
			this.IPAddress = InetAddress.getByName("localhost"); //UDP sendet Nachrichten mit Hilfe von der IP Adresse
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Methode oeffnet den Socket
	 */
	public void openSocket() {
		try {
			//Sende- und Empfangpunkt fuer das verarbeiten von Paketen
			socket = new DatagramSocket();
			
		} catch (SocketException e) {
			e.printStackTrace();
		}
		socketOn.set(true);
	}
	
	public void startCommunication(EndpointInfo[] serverInfo) {
		openSocket();
		
		//Server in Map speichern
		for (int i = 0; i < serverInfo.length; i++) {
			EndpointData serverData = new EndpointData();
            serverMap.put(serverInfo[i], serverData);	
		}
		
		new Thread() {
			
			// Empfange Nachrichten solange Socket geoeffnet ist
			public void run() {
				while(socketOn.get()) {
					DatagramPacket reply = new DatagramPacket(new byte[1024], 1024);
					Message serverMessage;
					
					//Versuchen Nachrichten zu empfangen
					try {
						socket.receive(reply);
						serverMessage = new Message(reply);
						
						//Server in Map speichern
						EndpointInfo server = serverMessage.getEndpoint();
						EndpointData serverData = serverMap.get(server);
  
			            System.out.println("CLIENT: Message from " + reply.getPort() + ": " + new String(reply.getData()));
			            serverData.setPacketStatus(serverMessage.getNr(), Status.ACKED);
			            
			            // Print RTT
			            serverData.setAckTime(serverMessage.getNr());
			            System.out.println("RTT time (in Millisekunden) " + serverData.rtt(serverMessage.getNr()));
			            
			            if(server.getPort() == 1717){
			            	obsList1.add(serverMessage + ", RTT: " + serverData.rtt(serverMessage.getNr()));
			            }else {
			            	obsList2.add(serverMessage + ", RTT: " + serverData.rtt(serverMessage.getNr()));
			            }
						
					} catch (IOException e) {
						if (!socketOn.get()) {
							System.out.println("CLIENT: client socket is closed.");
						} else {
							e.printStackTrace();
						}
					}
				}
			}
		}.start();

		//Es sollen 10 Pakete, bzw 10 Nachrichten gesendet werden an jeden Server

		for (int i = 0; i < 10; i++) {
			for (EndpointInfo server : serverMap.keySet()) {
				EndpointData data = serverMap.get(server);
				Message message = new Message(ping, i, server);
				if(server.getPort() == 1717){
	            	obsList1.add("PING " + i);
	            	
	            }else {
	            	obsList2.add("PING " + i);
	            }
				
				sendMessage(message);
				data.setPacketStatus(i, Status.SENT);
			}
			//Das ganze soll dann immer eine Sekunde schlafen und nicht Schlag auf Schlag gesendet werden
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		//Es wird geprueft, ob alle ACKs vom Server angekommen sind. Wenn ja, wird Socket geschlossen
		new Thread() {
			boolean check = true;
			
			@Override
			public void run() {
				
				while (check) {
					if (transferCompleted()) {
						closeSocket();
						check = false;
						break;
					}
					
					// Wenn es noch Pakete gibt, die nicht bestaetigt wurden, warte 0.4 Sek und versuche wieder.
					try {
						Thread.sleep(400);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				// Hier werden alle Pakete f�r jeden Server ausgeprintet (mit Status)
				for (EndpointInfo server : serverMap.keySet()) {
					EndpointData data = serverMap.get(server);
					System.out.println("Pakete gesendete an: " + server.toString());
					System.out.println("Number of timeouts: " + data.getNumberOfTimeouts());		
				}
				
			}
		}.start();

	}
	
	/**
	 * Timer wird fuer ein bestimmtes Message gestartet. 
	 * Wenn Zeit abgelaufen ist und die Bestaetigung vom Server nicht erhalten wurde, sendet der Client das Message erneut.
	 * 
	 *
	 */
	private class PacketTimeout extends TimerTask {
        private Message message;
        private EndpointData data;

        public PacketTimeout(Message message) {
            this.message = message;
            this.data = serverMap.get(message.getEndpoint());
        }

        public void run() {
            try {
                if (!(data.getPacketStatus(message.getNr()) == Status.ACKED)) {
                    sendMessage(message);
                    obsList1.add("PING " + message.getNr() + ", " + Status.RESENT.name());
                    System.out.println("CLIENT: Resend " + message.toString());
                    data.setPacketStatus(message.getNr(), Status.RESENT);
                    data.setNumberOfTimeouts();
                    
                }
            } catch (Exception e) {
            	
            }
        }
    }
	
	/**
	 * Liefert true, wenn alle Pakete bestaetigt sind (ACKED)
	 * @return
	 */
	public boolean transferCompleted() {
		boolean acked = true;
		for (EndpointInfo server : serverMap.keySet()) {
			EndpointData data = serverMap.get(server);
			for (int i = 0; i < data.getPackets().length; i++) {
				acked = acked & (data.getPacketStatus(i) == Status.ACKED);
			}
		}
			
		return acked;
	}
	
	public void closeSocket() {
		socketOn.set(false);
		socket.close();
	}
	
	
	public void sendMessage(Message message) {
		EndpointData data = serverMap.get(message.getEndpoint());
		data.setSentTime(message.getNr());
		try {
			socket.send(message.getPacket());
		} catch (IOException e) {
			e.printStackTrace();
		}
		data.getTimeoutTimer().schedule(new PacketTimeout(message), timeOut); // Starte Timer beim Senden
	}
	
	public ObservableList<String> getObsList1() {
		System.out.println(obsList1);
		return obsList1;
	}

	public ObservableList<String> getObsList2() {
		return obsList2;
	}

}