package view;

import client_server.Client;
import client_server.EndpointInfo;
import client_server.Server;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class pingpongController {
	
	@FXML
	private ListView<String> list1;
	
	@FXML
	private ListView<String> list2;

	@FXML
    void einServer(ActionEvent event) {
    	Server server = new Server(1717, "Weitz Server");
    	Client client = new Client();
    	
    	//Starte Server
    	new Thread(()-> {
    		try {
    			server.start();
    					
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}).start();
    	
    	client.startCommunication(new EndpointInfo[] {server.getServerInfo()});
    	list1.setItems(client.obsList1);
    	
    	client.obsList1.addListener(new ListChangeListener<String>() {

			@Override
			public void onChanged(Change<? extends String> arg0) {
				list1.setItems(client.obsList1);
				
			}
    		
    	}
    	);

    }

	@FXML
	void zweiServer(ActionEvent event) {
		Server server = new Server(1717, "Weitz Server");
		Server server2 = new Server(1234, "Schaible Server");
    	Client client = new Client();
    	
    	//Starte Server
    	new Thread(()-> {
    		try {
    			server.start();
    					
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}).start();
    	
    	//Starte Server
    	new Thread(()-> {
    		try {
    			server2.start();
    					
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}).start();
    	
    	client.startCommunication(new EndpointInfo[] {server.getServerInfo(), server2.getServerInfo()});
    	list1.setItems(client.getObsList1());
    	list2.setItems(client.getObsList2());
    	
    	client.obsList1.addListener(new ListChangeListener<String>() {

			@Override
			public void onChanged(Change<? extends String> arg0) {
				Platform.runLater(()->{
					list1.setItems(client.obsList1);
				});
				
				
			}
    		
    	});
    	
    	client.obsList2.addListener(new ListChangeListener<String>() {

			@Override
			public void onChanged(Change<? extends String> arg0) {
				Platform.runLater(() ->{ 
					list2.setItems(client.obsList2);
				});
				
			}
    		
    	});
	}
}

