package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class MainApp extends Application {

	@Override
    public void start(Stage stage) throws Exception {
		
        try {
			Parent root = FXMLLoader.load(getClass().getResource("/view/pingpong.fxml"));
			Scene scene = new Scene(root);
			stage.initStyle(StageStyle.DECORATED);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			System.out.print(e);
			e.printStackTrace();
		}
        
        stage.setOnCloseRequest(windowEvent -> {
        	Platform.exit();
        	System.exit(0);
	    });
        
    }

    public static void main(String[] args) {
        launch(args);
    }
}