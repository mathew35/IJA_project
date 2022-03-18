package project;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import uml.ClassDiagram;

public class MainController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        ClassDiagram diag = new ClassDiagram("1st diag ever");
        welcomeText.setText("Welcome to JavaFX Application!" + diag.getName() + " has been created");
    }
}