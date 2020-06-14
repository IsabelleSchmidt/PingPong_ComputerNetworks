package application;

import client_server.Client;
import client_server.Server;
import client_server.EndpointInfo;
import javafx.application.Application;
import javafx.stage.Stage;


public class Main extends Application {

	@Override
	public void start(Stage primaryStage)  {
		// Erzeuge 2 Server und einen Client
		Server server = new Server(1717, "Weitz Server");
		Server server2 = new Server(1234, "Krechel Server");
		Client client = new Client();
		
		//Starte Server
		new Thread(()-> {
			try {
				System.out.println("Hallo");
				server.start();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}).start();
		
		new Thread(()-> {
			try {
				server2.start();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
		
		// Starte die Paketuebergabe. Client bekommt eine Liste mit Server IP und Ports
		client.startCommunication(new EndpointInfo[] {server.getServerInfo(), server2.getServerInfo()});
	}
	
	@Override
	public void init() {
		System.out.println("Ping Pong initialisiert");
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
