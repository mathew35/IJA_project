/**
 * Řadič, který řeší přiřazení a logiku metod k prvků UI v úvodní scéně aplikace.
 *
 * @author Adam Bazel (xbazel00)
 * @since  2022-04-12
 */

package project;

import uml.SequenceDiagram;
import uml.UMLClass;
import uml.UMLClassifier;
import uml.UMLMessage;
import uml.UMLOperation;

import java.util.Dictionary;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import javafx.util.Callback;


/**
 * Řadič, který je vytvořen pomocí FXML a používá se k inicializaci prvků uživatelského rozhraní v úvodní scéně aplikace.
 */
public class MessageController {
    private boolean msgType, msgTypeOcc;
    private SequenceDiagram sequenceDiagram;
    public UMLMessage createdMessage;

    final UMLOperation placeHolder = new UMLOperation("None", new UMLClassifier());

    @FXML
    private Button closeButton;

    @FXML
    private RadioButton rCall, rReply, rSync, rAsync;

    @FXML
    private ToggleGroup msgType1, msgType2;

    @FXML
    private ComboBox<UMLClass> dropSender, dropReceiver;

    @FXML
    private ComboBox<UMLOperation> dropOperation;

    @FXML
    private TextField fieldReply, fieldPosition;

    @FXML
    private Button createMessageButton;

    Callback<ListView<UMLOperation>, ListCell<UMLOperation>> cellFactory = new Callback<ListView<UMLOperation>, ListCell<UMLOperation>>() {

        @Override
        public ListCell<UMLOperation> call(ListView<UMLOperation> l) {
            return new ListCell<UMLOperation>() {
    
                @Override
                protected void updateItem(UMLOperation item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) 
                    {
                        setGraphic(null);
                    } 
                    else if (item.equals(placeHolder))
                    {
                        setText("None");
                    }
                    else
                    {   
                        setText(item.getAlltoString());
                    }
                }
            } ;
        }
    };


    // TODO remove
    @FXML
    public void initialize() 
    {
        fieldPosition.textProperty().addListener(new ChangeListener<String>() 
        {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) 
            {
                if (!newValue.matches("\\d*")) {
                    fieldPosition.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        fieldReply.setDisable(false);
        createMessageButton.setDisable(true);
        dropOperation.setButtonCell(cellFactory.call(null));
        dropOperation.setCellFactory(cellFactory);
        dropOperation.getItems().add(placeHolder);
        dropOperation.getSelectionModel().select(placeHolder);
    }

    public void loadData(SequenceDiagram diagram)
    {
        sequenceDiagram = diagram;
        dropSender.getItems().addAll(sequenceDiagram.getClasses());
        dropReceiver.getItems().addAll(sequenceDiagram.getClasses());
    }

    // TODO
    @FXML
    public void loadOperations()
    {
        getMsgType();

        if (!dropSender.getSelectionModel().isEmpty() && msgType == true)
        {
            dropOperation.getItems().clear();

            for (UMLClass className : sequenceDiagram.getClasses()) 
            {
                if (className.getName().equals(dropSender.getValue().getName())) 
                {
                    dropOperation.getItems().addAll((className.getOperations()));
                }
            }
            dropOperation.getItems().add(placeHolder);
            dropOperation.getSelectionModel().select(placeHolder);
        }
        else
        {
            dropOperation.setDisable(true);
        }

        enableMessage();
    }

    @FXML
    public void getMsgType()
    {
        if (rCall.isSelected())
        {
            msgType = true;
            dropOperation.setDisable(false);

        }
        else if (rReply.isSelected())
        {
            msgType = false;
            fieldReply.setDisable(false);
            dropOperation.setDisable(true);
        }
        
        if (rSync.isSelected())
        {
            msgTypeOcc = true;
        }
        else if (rAsync.isSelected())
        {
            msgTypeOcc = false;
        }

        if (!dropSender.getSelectionModel().isEmpty() && msgType == true)
        {
            dropOperation.getItems().clear();

            for (UMLClass className : sequenceDiagram.getClasses()) 
            {
                if (className.getName().equals(dropSender.getValue().getName())) 
                {
                    dropOperation.getItems().addAll((className.getOperations()));
                }
            }
            dropOperation.getItems().add(placeHolder);
            dropOperation.getSelectionModel().select(placeHolder);
        }
        else
        {
            dropOperation.setDisable(true);
        }

        enableMessage();
    }

    public void enableMessage()
    {
        if (!dropSender.getSelectionModel().isEmpty() && !dropReceiver.getSelectionModel().isEmpty() && msgType1.getSelectedToggle() != null && msgType2.getSelectedToggle() != null)
        {
            createMessageButton.setDisable(false);
        }
        else
        {
            createMessageButton.setDisable(true);
        }
    }

    @FXML
    public void createMessageData()
    {
        int position = -1;

        if (fieldReply.getText().isEmpty())
        {
            fieldReply.requestFocus();
            return;
        }

        if (!fieldPosition.getText().isEmpty())
        {
            position = Integer.parseInt(fieldPosition.getText());
        }

        if (dropOperation.getValue() != placeHolder)
        {
            createdMessage = new UMLMessage(dropOperation.getValue(), dropSender.getValue(), dropReceiver.getValue(), msgType, msgTypeOcc, position);
        }
        else
        {
            createdMessage = new UMLMessage(fieldReply.getText(), dropSender.getValue(), dropReceiver.getValue(), msgType, msgTypeOcc, position);
        }

        closeWindow();
    }

    @FXML void closeWindow()
    {
        ((Stage)closeButton.getScene().getWindow()).close();
    }
}