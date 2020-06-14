package view;

import client_server.Client;
import client_server.EndpointInfo;
import client_server.Server;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

public class pingpongController {
	
	@FXML
	private ListView<String> list1;
	
	@FXML
	private ListView<String> list2;
	
	@FXML
	private Button button1;
	
	@FXML
	private Button button2;

	@FXML
    void einServer(ActionEvent event) {
		button1.setDisable(true);
		button2.setDisable(true);
		list1.getItems().clear();
		list2.getItems().clear();
		
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
    	
    	new Thread(()-> {
    		client.startCommunication(new EndpointInfo[] {server.getServerInfo()});
    	}).start();
    	
    	client.obsList1.addListener(new ListChangeListener<String>() {

			@Override
			public void onChanged(Change<? extends String> arg0) {
				Platform.runLater(()-> {
					list1.getItems().setAll(arg0.getList());
				});
			}
    		
    	});
    	
    	client.socketOn.addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldValue, Boolean newValue) {
				if (!newValue.booleanValue()) {
					server.stopServer();
					button1.setDisable(false);
					button2.setDisable(false);
				}
			}
		});

    }

	@FXML
	void zweiServer(ActionEvent event) {
		button1.setDisable(true);
		button2.setDisable(true);
		list1.getItems().clear();
		list2.getItems().clear();
		
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
    	
    	new Thread(()-> {
    		client.startCommunication(new EndpointInfo[] {server.getServerInfo(), server2.getServerInfo()});
    	}).start();
    	
    	client.obsList1.addListener(new ListChangeListener<String>() {

			@Override
			public void onChanged(Change<? extends String> arg0) {
				Platform.runLater(()->{
					list1.getItems().setAll(arg0.getList());
				});
				
				
			}
    		
    	});
    	
    	client.obsList2.addListener(new ListChangeListener<String>() {

			@Override
			public void onChanged(Change<? extends String> arg0) {
				Platform.runLater(() ->{ 
					list2.getItems().setAll(arg0.getList());
				});
				
			}
    		
    	});
    	
    	client.socketOn.addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldValue, Boolean newValue) {
				if (!newValue.booleanValue()) {
					server.stopServer();
					server2.stopServer();
					button1.setDisable(false);
					button2.setDisable(false);
				}
			}
		});
	}
}

