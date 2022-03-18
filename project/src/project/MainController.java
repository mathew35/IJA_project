package project;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.awt.*;

public class MainController {
    @FXML
    private Label welcomeText;

    @FXML
    private TextField UMLname;

    @FXML
    protected void onCreateUMLButtonClick() {
        if(UMLname.getText().length() == 0){
            welcomeText.setText("Wrong input");
        }
        else{
            welcomeText.setText("New Diagram: "+UMLname.getText());
        }
    }

    @FXML
    protected void onOpenUMLButtonClick(){

    };
}