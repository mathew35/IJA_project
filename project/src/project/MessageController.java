/**
 * Řadič, který řeší přiřazení a logiku metod k prvků UI v úvodní scéně aplikace.
 *
 * @author Adam Bazel (xbazel00)
 * @since  2022-04-12
 */

package project;

import uml.SequenceDiagram;
import uml.UMLClass;
import uml.UMLOperation;

import java.util.Dictionary;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;



/**
 * Řadič, který je vytvořen pomocí FXML a používá se k inicializaci prvků uživatelského rozhraní v úvodní scéně aplikace.
 */
public class MessageController {
    private boolean msgType;
    private SequenceDiagram sequenceDiagram;

    @FXML
    private RadioButton rCall, rReply;

    @FXML
    private ComboBox<UMLClass> dropSender, dropReceiver;

    @FXML
    private ComboBox<UMLOperation> dropOperation;

    @FXML
    private TextField fieldReply;

    public void loadData(SequenceDiagram diagram)
    {
        sequenceDiagram = diagram;
        dropSender.getItems().addAll(sequenceDiagram.getClasses());
        dropReceiver.getItems().addAll(sequenceDiagram.getClasses());
    }

    @FXML
    public void loadOperations()
    {
        if (!dropSender.getSelectionModel().isEmpty() && msgType == true)
        {
            dropOperation.setDisable(false);

            for (UMLClass className : sequenceDiagram.getClasses()) 
            {
                if (className.getName().equals(dropSender.getValue().getName())) 
                {
                    //dropOperation.getItems().addAll(className.getAttributes());
                    //return customer;
                }
            }
            sequenceDiagram.findClassifier(dropSender.getValue().getName());
        }
        else
        {
            dropOperation.setDisable(true);
        }
    }

    @FXML
    public void getMsgType(ActionEvent event)
    {
        if (rCall.isSelected())
        {
            msgType = true;
            fieldReply.setDisable(true);
            if (!dropSender.getSelectionModel().isEmpty())
            {
                dropOperation.setDisable(false);
            }
        }
        else if (rReply.isSelected())
        {
            msgType = false;
            fieldReply.setDisable(false);
            dropOperation.setDisable(true);
        }
    }

    public void createMessage()
    {

    }
}