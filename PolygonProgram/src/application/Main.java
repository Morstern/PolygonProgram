package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(this.getClass().getResource("BorderPaneWindow.fxml"));
		BorderPane borderPane = loader.load();
		Scene scene = new Scene(borderPane);
		primaryStage.setResizable(false);
		primaryStage.setTitle("PolygonProgram");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

}
