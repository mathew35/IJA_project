/**
 * Řadič, který plní funkci vytváření nového aktivačního intervalu
 *
 * @author Adam Bazel (xbazel00)
 * @since  2022-04-12
 */

package project;

import uml.SequenceDiagram;
import uml.UMLActivation;
import uml.UMLClass;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;


/**
 * Řadič, který je vytvořen pomocí FXML a používá se k inicializaci prvků uživatelského rozhraní v úvodní scéně aplikace.
 */
public class ActivationController {
    private SequenceDiagram sequenceDiagram;
    public UMLActivation createdAct;

    @FXML
    private Button closeButton;

    @FXML
    private ComboBox<UMLClass> dropTime;

    @FXML
    private TextField fieldFrom, fieldTo;

    @FXML
    private Button createActButton;

    @FXML
    public void initialize() 
    {
        fieldFrom.textProperty().addListener(new ChangeListener<String>() 
        {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) 
            {
                if (!newValue.matches("\\d*")) {
                    fieldFrom.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        fieldTo.textProperty().addListener(new ChangeListener<String>() 
        {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) 
            {
                if (!newValue.matches("\\d*")) {
                    fieldFrom.setText(newValue.replaceAll("[^\\d]", ""));
                }
                else
                {
                    // TODO
                }
            }
        });
    }

    @FXML void enableInterval()
    {
        if (!dropTime.getSelectionModel().isEmpty())
        {
            fieldFrom.setDisable(false);
            fieldTo.setDisable(false);
        }
    }

    @FXML void createActButton()
    {

    }

    public void loadData(SequenceDiagram diagram)
    {
        sequenceDiagram = diagram;
        dropTime.getItems().addAll(sequenceDiagram.getClasses());
    }

    @FXML void createActData()
    {
        createdAct = new UMLActivation();
    }

    @FXML void closeWindow()
    {
        ((Stage)closeButton.getScene().getWindow()).close();
    }
}