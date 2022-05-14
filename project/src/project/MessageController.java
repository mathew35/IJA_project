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
import uml.UMLInstance;
import uml.UMLMessage;
import uml.UMLOperation;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;


/**
 * Řadič, který je vytvořen pomocí FXML a používá se k inicializaci prvků uživatelského rozhraní v úvodní scéně aplikace.
 */
public class MessageController {
    public int msgType;
    private SequenceDiagram sequenceDiagram;
    public UMLMessage createdMessage;
    public UMLInstance createdInstance;

    final UMLOperation placeHolder = new UMLOperation("None", new UMLClassifier());

    @FXML
    private Button closeButton;

    @FXML
    private Label messageLabel;

    @FXML
    private RadioButton rCreate, rReply, rSync, rAsync;

    @FXML
    private ToggleGroup msgType1;

    @FXML
    private ComboBox<UMLInstance> dropSender, dropReceiver;

    @FXML
    private ComboBox<UMLOperation> dropOperation;

    @FXML
    private ComboBox<UMLClass> dropClass;

    @FXML
    private TextField fieldReply, fieldPosition, fieldInst;

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

    Callback<ListView<UMLClass>, ListCell<UMLClass>> cellFactory3 = new Callback<ListView<UMLClass>, ListCell<UMLClass>>() {

        @Override
        public ListCell<UMLClass> call(ListView<UMLClass> l) {
            return new ListCell<UMLClass>() {
    
                @Override
                protected void updateItem(UMLClass item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) 
                    {
                        setGraphic(null);
                    } 
                    else
                    {   
                        setText(item.getName());
                    }
                }
            } ;
        }
    };

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

        dropClass.setButtonCell(cellFactory3.call(null));
        dropClass.setCellFactory(cellFactory3);

        dropSender.setButtonCell(cellFactory2.call(null));
        dropSender.setCellFactory(cellFactory2);
        dropReceiver.setButtonCell(cellFactory2.call(null));
        dropReceiver.setCellFactory(cellFactory2);

        dropOperation.setButtonCell(cellFactory.call(null));
        dropOperation.setCellFactory(cellFactory);
        dropOperation.getItems().add(placeHolder);
        dropOperation.getSelectionModel().select(placeHolder);
    }

    public void loadData(SequenceDiagram diagram)
    {
        sequenceDiagram = diagram;
        dropSender.getItems().addAll(sequenceDiagram.instances);
        dropReceiver.getItems().addAll(sequenceDiagram.instances);
    }

    // TODO
    @FXML
    public void loadOperations()
    {
        getMsgType();

        if (!dropSender.getSelectionModel().isEmpty() && msgType == 0)
        {
            dropOperation.getItems().clear();
            dropOperation.getItems().addAll(dropSender.getValue().asgclass.getOperations());
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
    public void loadMessage()
    {
        if (!dropOperation.getSelectionModel().isEmpty() && dropOperation.getValue() != placeHolder)
        {
            fieldReply.setText(dropOperation.getValue().getName());
        }
    }

    @FXML
    public void getMsgType()
    {
        if (rCreate.isSelected())
        {
            msgType = 2;
            fieldReply.setDisable(false);
            dropOperation.setDisable(true);
            dropOperation.getItems().clear();
            messageLabel.setText("Message:");
            dropClass.setDisable(false);
            fieldInst.setDisable(false);
            dropClass.getItems().clear();
            dropClass.getItems().addAll(sequenceDiagram.getClasses());
            fieldReply.setText("<<create>>");
            dropReceiver.setDisable(true);

        }
        else if (rReply.isSelected())
        {
            msgType = 3;
            fieldReply.setDisable(false);
            dropOperation.setDisable(true);
            dropOperation.getItems().clear();
            messageLabel.setText("Message:");
            dropClass.setDisable(true);
            fieldInst.setDisable(true);
            dropClass.getItems().clear();
        }
        
        if (rSync.isSelected())
        {
            msgType = 0;
            fieldReply.setDisable(false);
            dropOperation.setDisable(false);
            dropOperation.getItems().clear();
            messageLabel.setText("Arguments:");
            dropClass.setDisable(true);
            fieldInst.setDisable(true);
            dropClass.getItems().clear();
        }
        else if (rAsync.isSelected())
        {
            msgType = 1;
            fieldReply.setDisable(false);
            dropOperation.setDisable(true);
            dropOperation.getItems().clear();
            messageLabel.setText("Arguments:");
            dropClass.setDisable(true);
            fieldInst.setDisable(true);
            dropClass.getItems().clear();
        }

        if (!dropSender.getSelectionModel().isEmpty() && msgType == 0)
        {
            dropOperation.getItems().clear();
            dropOperation.getItems().addAll(dropSender.getValue().asgclass.getOperations());
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
        if (!rCreate.isSelected())
        {
            if (!dropSender.getSelectionModel().isEmpty() && !dropReceiver.getSelectionModel().isEmpty())
            {
                createMessageButton.setDisable(false);
            }
            else
            {
                createMessageButton.setDisable(true);
            }
        }
        else
        {
            if (!dropClass.getSelectionModel().isEmpty())
            {
                createMessageButton.setDisable(false);
            }
            else
            {
                createMessageButton.setDisable(true);
            }
        }
    }

    @FXML
    public void createMessageData()
    {
        int position = -1;

        if (!fieldPosition.getText().isEmpty())
        {
            position = Integer.parseInt(fieldPosition.getText());
        }

        if (fieldReply.getText().isEmpty())
        {
            fieldReply.requestFocus();
            return;
        }

        if (msgType == 2)
        {
            if (fieldInst.getText().isEmpty())
            {
                fieldInst.requestFocus();
                return;
            }

            createdInstance = new UMLInstance(fieldInst.getText(), dropClass.getValue());
            createdMessage = new UMLMessage(fieldReply.getText(), null, dropSender.getValue(), createdInstance, msgType, position);

        }
        else
        {
            if (dropOperation.getValue() != placeHolder)
            {
                createdMessage = new UMLMessage(fieldReply.getText(), dropOperation.getValue(), dropSender.getValue(), dropReceiver.getValue(), msgType, position);
            }
            else
            {
                createdMessage = new UMLMessage(fieldReply.getText(), null, dropSender.getValue(), dropReceiver.getValue(), msgType, position);
            }
        }
        
        closeWindow();
    }

    @FXML void closeWindow()
    {
        ((Stage)closeButton.getScene().getWindow()).close();
    }
}