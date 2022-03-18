package project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.awt.*;

public class Main extends Application{

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getClassLoader().getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2, Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2);
        stage.setTitle("Projekt - UML diagram handler");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}