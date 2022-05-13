/**
 * Řadič, který plní funkci vytváření nového aktivačního intervalu
 *
 * @author Adam Bazel (xbazel00)
 * @since  2022-04-12
 */

package project;

import uml.SequenceDiagram;
import uml.UMLActivation;
import uml.UMLInstance;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;


/**
 * Řadič, který je vytvořen pomocí FXML a používá se k inicializaci prvků uživatelského rozhraní v úvodní scéně aplikace.
 */
public class ActivationController {
    private SequenceDiagram sequenceDiagram;
    public UMLActivation createdAct;
    public Integer indexOfInstance;

    @FXML
    private Button closeButton1;

    @FXML
    private ComboBox<UMLInstance> dropTime;

    @FXML
    private TextField fieldFrom, fieldTo;

    @FXML
    private Button createActButton;

    Callback<ListView<UMLInstance>, ListCell<UMLInstance>> cellFactory2 = new Callback<ListView<UMLInstance>, ListCell<UMLInstance>>() {

        @Override
        public ListCell<UMLInstance> call(ListView<UMLInstance> l) {
            return new ListCell<UMLInstance>() {
    
                @Override
                protected void updateItem(UMLInstance item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) 
                    {
                        setGraphic(null);
                    } 
                    else
                    {   
                        setText(item.instancename + ":" + item.asgclass.getName());
                    }
                }
            } ;
        }
    };

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
                if (!fieldFrom.getText().equals("") && !fieldTo.getText().equals("") && !dropTime.getSelectionModel().isEmpty())
                {
                    createActButton.setDisable(false);
                }
            }
        });

        fieldTo.textProperty().addListener(new ChangeListener<String>() 
        {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) 
            {
                if (!newValue.matches("\\d*")) {
                    fieldTo.setText(newValue.replaceAll("[^\\d]", ""));
                }
                else
                {
                    if (!fieldFrom.getText().equals("") && !fieldTo.getText().equals("") && !dropTime.getSelectionModel().isEmpty())
                    {
                        createActButton.setDisable(false);
                    }
                }
            }
        });

        dropTime.setButtonCell(cellFactory2.call(null));
        dropTime.setCellFactory(cellFactory2);
    }

    @FXML void enableInterval()
    {
        if (!dropTime.getSelectionModel().isEmpty())
        {
            fieldFrom.setDisable(false);
            fieldTo.setDisable(false);
        }
    }

    public void loadData(SequenceDiagram diagram)
    {
        sequenceDiagram = diagram;
        dropTime.getItems().addAll(sequenceDiagram.instances);
    }

    @FXML void createActData()
    {
        int fromIndex = Integer.parseInt(fieldFrom.getText());
        int toIndex = Integer.parseInt(fieldTo.getText());
        
        if (fromIndex > (sequenceDiagram.messages.size() - 1) || toIndex > (sequenceDiagram.messages.size() - 1))
        {
            System.out.println("1");
            return;
        }

        indexOfInstance = sequenceDiagram.instances.indexOf(dropTime.getValue());
        List<UMLActivation> actList = sequenceDiagram.instances.get(sequenceDiagram.instances.indexOf(dropTime.getValue())).activations;


        for (UMLActivation act : actList)
        {
            System.out.println("Interval nové aktivace: (" + fromIndex + ", " + toIndex + ") + čekuji hranice: (" + act.getStart() + ", " + act.getEnd() + ")");
            if (fromIndex >= act.getStart() && fromIndex <= act.getEnd())
            {
                System.out.println("2");
                return;
            }

            if (toIndex >= act.getStart() && toIndex <= act.getEnd())
            {
                System.out.println("3");
                return;
            }

            if (fromIndex <= act.getStart() && act.getEnd() >= toIndex)
            {
                System.out.println("4");
                return;
            }
        }

        createdAct = new UMLActivation(fromIndex, toIndex);
        closeWindow();
    }

    @FXML void closeWindow()
    {
        ((Stage)closeButton1.getScene().getWindow()).close();
    }
}