package project;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.ResourceBundle;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import javafx.fxml.*;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import uml.*;

public class SequenceController
{
    SequenceDiagram sequenceDiagram = new SequenceDiagram();
    GridPane seqGrid = new GridPane();
    GridPane seqGridMsgs = new GridPane();
    double msgWidth;

    @FXML
    private TabPane tabPane;

    @FXML
    private VBox addSeqObjFirst;

    @FXML
    private Button addSeqObjButton;

    @FXML
    private Button messageCreatorButton;

    @FXML
    private StackPane seqEditorBox;

    @FXML
    private VBox seqMsgBox;

    @FXML
    private VBox loadSeqFirst;

    @FXML
    private HBox optionSeqBox;

    @FXML 
    private HBox seqEditorBack;

    private static void configureFileChooser(final FileChooser fileChooser, String type) 
    {      
        fileChooser.setTitle("Open " + type + " File...");                
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON", "*.json"));
    }

   /**
     * Metoda, která načítá z JSON souboru sekvenční diagram do aplikace.
     *
     * @param objectMapper Objekt třídy, která poskytuje funkce pro čtení a zápis JSON.
     */
    SequenceDiagram loadSequence(ObjectMapper objectMapper, File file)
    {
        try {
            //SequenceDiagram diagram = objectMapper.readValue(new File("data/sequence.json"), SequenceDiagram.class);
            SequenceDiagram diagram = objectMapper.readValue(file, SequenceDiagram.class);
    
            return diagram;
        } catch (StreamReadException e) {
            e.printStackTrace();
        } catch (DatabindException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Metoda, která exportuje diagram tříd v aplikaci do souboru.
     *
     * @param objectMapper Objekt třídy, která poskytuje funkce pro čtení a zápis JSON.
     */
    void exportSequence(ObjectMapper objectMapper)
    {
        Window stage = Stage.getWindows().stream().filter(Window::isShowing).findFirst().orElse(null);
        //Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
 
        //Set extension filter for text files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                objectMapper.writeValue(file, sequenceDiagram);
            } catch (StreamWriteException e) {
                e.printStackTrace();
            } catch (DatabindException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void loadSeqDiagram()
    {
        FileChooser fileChooser = new FileChooser();

        Stage stage = (Stage) seqEditorBack.getScene().getWindow();

        configureFileChooser(fileChooser,"Sequence");
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) 
        {
            if (addSeqObjFirst != null)
            {
                seqEditorBox.getChildren().remove(optionSeqBox);
                addSeqObjFirst = null;
    
                initGridState();
                initGrid();
            };

            ObjectMapper objectMapper = new ObjectMapper();

            this.sequenceDiagram = loadSequence(objectMapper, file);
            displaySequence(sequenceDiagram);
        }
    }

    @FXML
    private void addSeqObj()
    {
        if (addSeqObjFirst != null)
        {
            seqEditorBox.getChildren().remove(optionSeqBox);
            addSeqObjFirst = null;

            initGridState();
            initGrid();
        };

        int colCount = sequenceDiagram.getNameClasses().size();
        VBox startObj = new VBox();
        
        Line startLine = new Line(0, 0, 0, seqEditorBox.getHeight() * 0.01);
        StackPane objRectangle = new StackPane();
        Rectangle rectangle = new Rectangle(150, 50);
        Effect effect = new DropShadow(BlurType.GAUSSIAN, Color.DODGERBLUE, 5, 0.75, 0, 0);
        TextField objField = new TextField();

        rectangle.setOnMouseClicked((MouseEvent event) -> 
        {
            rectangle.requestFocus();
            Node clickedNode = event.getPickResult().getIntersectedNode();
            System.out.println(clickedNode);
            if (clickedNode != seqGrid) 
            {
                // click on descendant node
                Node parent = clickedNode.getParent();
                while (parent != seqGrid) 
                {
                    clickedNode = parent;
                    parent = clickedNode.getParent();
                }
                Integer colIndex = GridPane.getColumnIndex(clickedNode);
                Integer rowIndex = GridPane.getRowIndex(clickedNode);
                System.out.println("Mouse clicked cell: " + colIndex + " And: " + rowIndex);

                rectangle.setStroke(Color.DODGERBLUE);
                rectangle.setEffect(rectangle.getEffect() == null ? effect : null);
            }
        });

        rectangle.focusedProperty().addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
            {
                if (!newPropertyValue)
                {
                    rectangle.setStroke(Color.BLACK);
                    rectangle.setEffect(null);
                }
            }
        });

        startObj.setAlignment(Pos.CENTER);

        rectangle.setStroke(Color.BLACK);
        rectangle.setFill(Color.TRANSPARENT);

        objRectangle.getChildren().add(rectangle);
        addSeqObjButton.setDisable(true);

        // Multiple Event Handler https://stackoverflow.com/questions/31794167/how-to-handle-multiple-event-types-from-one-class-in-javafx
        objField.setOnKeyPressed(new EventHandler<KeyEvent>() 
        {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) 
                {
                    sequenceDiagram.createClass(objField.getText());

                    // sequenceDiagrams.get(0).getNameClasses()+ seqDisplay.getChildren().size()
                    Text objName = new Text(objField.getText());
                    objRectangle.getChildren().remove(objField);
                    objRectangle.getChildren().add(objName);
                    //seqGrid.add(objName, objsCount, 0);

                    if (seqGrid.getColumnCount() >= 5)
                    {
                        addSeqObjButton.setDisable(true);
                    }
                    else
                    {
                        addSeqObjButton.setDisable(false);
                    }
                }
            }
        });

        /*objField.focusedProperty().addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
            {
                if (!newPropertyValue)
                {
                    if (objField.getText() == "" && objsCount != 0)
                    {
                        seqGrid.getChildren().remove(startObj);
                        seqGrid.getColumnConstraints().remove(objsCount);
                    }
                    /*else
                    {
                        sequenceDiagrams.get(tabPane.getSelectionModel().getSelectedIndex()-2).createClass(objField.getText());

                        Text objName = new Text(objField.getText());
                        seqGrid.getChildren().remove(objField);
                        seqGrid.add(objName, objsCount, 0);
                    }
                    addSeqObjButton.setDisable(false);
                }
            }
        });*/

        seqGrid.getColumnConstraints().add(new ColumnConstraints(-1, -1, -1, Priority.ALWAYS, HPos.CENTER, false));
        if (colCount == 0)
        {
            seqGrid.getRowConstraints().add(new RowConstraints(-1, -1, -1, Priority.ALWAYS, VPos.CENTER, false));
            seqGrid.getRowConstraints().add(new RowConstraints(-1, -1, -1, Priority.ALWAYS, VPos.CENTER, false));
        }
        
        objRectangle.getChildren().add(objField);

        startObj.getChildren().addAll(objRectangle, startLine);

        seqGrid.add(startObj, colCount, 0);
        objField.requestFocus();

        Line timeAxis = new Line(0, 0, 0, seqEditorBox.getHeight() * 0.85);
        timeAxis.getStrokeDashArray().addAll(25d, 10d);

        seqGrid.add(timeAxis, colCount, 1);
        updateGridMsg();
    }

    @FXML
    public void updateGridMsg()
    {
        if (seqGrid.getColumnCount() > 2)
        {
            ColumnConstraints updateColumn = new ColumnConstraints(seqEditorBox.getWidth()/seqGrid.getColumnCount());
            ColumnConstraints updateSpacerColumn = new ColumnConstraints((seqEditorBox.getWidth()/seqGrid.getColumnCount())/2);

            seqGridMsgs.getColumnConstraints().add(seqGridMsgs.getColumnCount()-2, updateColumn);

            // TODO if seqGridMsgs.getColumnCount() != seqGridgetColumnCount()

            for (int i = 0; i < seqGridMsgs.getColumnCount(); i++)
            {
                if (i == 0 || i == seqGridMsgs.getColumnCount() - 1)
                {
                    seqGridMsgs.getColumnConstraints().set(i, updateSpacerColumn);
                }
                else
                {
                    seqGridMsgs.getColumnConstraints().set(i, updateColumn);
                }
            }

            for (int i = 0; i < (seqGridMsgs.getChildren().size()); i++)
            {
                Node nodeMsg = seqGridMsgs.getChildren().get(i);
                
                if (nodeMsg instanceof UMLArrow)
                {
                    if (((UMLArrow)nodeMsg).getEndX() > 0)
                    {
                        ((UMLArrow)nodeMsg).setEndX(seqEditorBox.getWidth()/seqGrid.getColumnCount());
                    }
                    else
                    {
                        ((UMLArrow)nodeMsg).setEndX(-seqEditorBox.getWidth()/seqGrid.getColumnCount());
                    }
                    
                }

                if (nodeMsg instanceof UMLArrowReply)
                {
                    if (((UMLArrowReply)nodeMsg).getEndX() > 0)
                    {
                        ((UMLArrowReply)nodeMsg).setEndX(seqEditorBox.getWidth()/seqGrid.getColumnCount());
                    }
                    else
                    {
                        ((UMLArrowReply)nodeMsg).setEndX(-seqEditorBox.getWidth()/seqGrid.getColumnCount());
                    }
                    
                }

                if (nodeMsg instanceof Line)
                {
                    ((Line)nodeMsg).setEndX(seqEditorBox.getWidth()/seqGrid.getColumnCount());
                }
            }
        }
    }

    @FXML
    public void openMessageCreator() throws IOException
    {
        FXMLLoader loader = new FXMLLoader(Main.class.getClassLoader().getResource("message.fxml"));
        Parent root = (Parent)loader.load();

        MessageController msgController = loader.getController();
        msgController.loadData(sequenceDiagram);

        Stage popUp = new Stage();

        popUp.initModality(Modality.APPLICATION_MODAL);
        popUp.initOwner(Main.stage);
        popUp.setTitle("Create New Message...");
        popUp.setScene(new Scene(root));
        popUp.setResizable(false);
        popUp.showAndWait();

        if (msgController.createdMessage != null)
        {
            createMessage(msgController.createdMessage);
        }
    }

    @FXML
    public void displaySequence(SequenceDiagram diagram)
    {
        if (addSeqObjFirst != null)
        {
            seqEditorBox.getChildren().remove(addSeqObjFirst);
            addSeqObjFirst = null;

            initGrid();
            initGridState();
        }

        ArrayList<String> nameList = new ArrayList<String>();

        nameList = diagram.getNameClasses();
       
        for (int i = 0; i < nameList.size(); i++)
        {
            VBox startObj = new VBox();
            Line startLine = new Line(0, 0, 0, seqEditorBox.getHeight() * 0.01);
            StackPane objRectangle = new StackPane();
            Rectangle rectangle = new Rectangle(150, 50);
            Effect effect = new DropShadow(BlurType.GAUSSIAN, Color.DODGERBLUE, 5, 0.75, 0, 0);

            rectangle.setOnMouseClicked((MouseEvent event) -> 
            {
                rectangle.requestFocus();
                Node clickedNode = event.getPickResult().getIntersectedNode();
                System.out.println(clickedNode);
                if (clickedNode != seqGrid) 
                {
                    // click on descendant node
                    Node parent = clickedNode.getParent();
                    while (parent != seqGrid) 
                    {
                        clickedNode = parent;
                        parent = clickedNode.getParent();
                    }
                    Integer colIndex = GridPane.getColumnIndex(clickedNode);
                    Integer rowIndex = GridPane.getRowIndex(clickedNode);
                    System.out.println("Mouse clicked cell: " + colIndex + " And: " + rowIndex);

                    rectangle.setStroke(Color.DODGERBLUE);
                    rectangle.setEffect(rectangle.getEffect() == null ? effect : null);
                }
            });

            rectangle.focusedProperty().addListener(new ChangeListener<Boolean>()
            {
                @Override
                public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
                {
                    if (!newPropertyValue)
                    {
                        rectangle.setStroke(Color.BLACK);
                        rectangle.setEffect(null);
                    }
                }
            });

            startObj.setAlignment(Pos.CENTER);

            rectangle.setStroke(Color.BLACK);
            rectangle.setFill(Color.TRANSPARENT);

            //sequenceDiagrams.get(tabPane.getSelectionModel().getSelectedIndex()-2).createClass(nameList.get(i));

            objRectangle.getChildren().add(rectangle);

            seqGrid.getColumnConstraints().add(new ColumnConstraints(-1, -1, -1, Priority.ALWAYS, HPos.CENTER, false));
            if (i == 0)
            {
                seqGrid.getRowConstraints().add(new RowConstraints(-1, -1, -1, Priority.ALWAYS, VPos.CENTER, false));
                seqGrid.getRowConstraints().add(new RowConstraints(-1, -1, -1, Priority.ALWAYS, VPos.CENTER, false));
            }
            
            objRectangle.getChildren().add(new Text(nameList.get(i)));

            startObj.getChildren().addAll(objRectangle, startLine);

            seqGrid.add(startObj, i, 0);

            Line timeAxis = new Line(0, 0, 0, seqEditorBox.getHeight() * 0.85);
            timeAxis.getStrokeDashArray().addAll(25d, 10d);

            seqGrid.add(timeAxis, i, 1);
            updateGridMsg();
        }
    }

    @FXML
    public void createMessage(UMLMessage message)
    {
        int msgCount = sequenceDiagram.returnMessagesCount();
        int sendIndex = sequenceDiagram.getClasses().indexOf(message.sender);
        int recIndex = sequenceDiagram.getClasses().indexOf(message.receiver);
        Label messageLabel = new Label();

        GridPane.setValignment(messageLabel, VPos.TOP);
        GridPane.setHalignment(messageLabel, HPos.CENTER);

        if (message.operation != null)
        {    
            messageLabel.setText(message.operation.getAlltoString());
        }
        else
        {
            messageLabel.setText(message.message);
        }

        double arrowWidth = seqEditorBox.getWidth() / seqGrid.getColumnCount();
                
        if (sendIndex > recIndex)
        {
            arrowWidth = -arrowWidth;
        }

        seqGridMsgs.getRowConstraints().add(new RowConstraints(30));
        
        if (sendIndex < recIndex)
        {
            if (Math.abs(sendIndex - recIndex) != 1)
            {
                for (int i = sendIndex + 1; i < recIndex; i++)
                {
                    Line newLine = new Line(0, 0, seqEditorBox.getWidth() / seqGrid.getColumnCount(), 0);
                    if (message.transmition == false)
                    {
                        newLine.getStrokeDashArray().addAll(25d, 10d);
                    }
                    seqGridMsgs.add(newLine, i, msgCount);
                    GridPane.setValignment(newLine, VPos.CENTER); 
                }
            }

            if (message.transmition == true)
            {
                UMLArrow arrow = new UMLArrow();
                arrow.setStartX(0);
                arrow.setStartY(0);

                arrow.setEndX(arrowWidth);
                arrow.setEndY(0);
                seqGridMsgs.add(arrow, recIndex, msgCount);
            }
            else
            {
                UMLArrowReply arrow = new UMLArrowReply();
                arrow.setStartX(0);
                arrow.setStartY(0);

                arrow.setEndX(arrowWidth);
                arrow.setEndY(0);
                seqGridMsgs.add(arrow, recIndex, msgCount);
            }
            
            seqGridMsgs.add(messageLabel, recIndex, msgCount);
        }
        else
        {
            if (Math.abs(sendIndex - recIndex) != 1)
            {
                for (int i = sendIndex; i > recIndex + 1; i--)
                {
                    Line newLine = new Line(0, 0, seqEditorBox.getWidth() / seqGrid.getColumnCount(), 0);
                    seqGridMsgs.add(newLine, i, msgCount);
                    GridPane.setValignment(newLine, VPos.CENTER); 
                }
            }
            if (message.transmition == true)
            {
                UMLArrow arrow = new UMLArrow();
                arrow.setStartX(0);
                arrow.setStartY(0);

                arrow.setEndX(arrowWidth);
                arrow.setEndY(0);
                seqGridMsgs.add(arrow, recIndex + 1, msgCount);
            }
            else
            {
                UMLArrowReply arrow = new UMLArrowReply();
                arrow.setStartX(0);
                arrow.setStartY(0);

                arrow.setEndX(arrowWidth);
                arrow.setEndY(0);
                seqGridMsgs.add(arrow, recIndex + 1, msgCount);
            }
            seqGridMsgs.add(messageLabel, recIndex + 1, msgCount);
        }

        sequenceDiagram.addMessage(message);
        System.out.println("Sender index: " + sendIndex);
        System.out.println("Receiver index: " + recIndex);
    }

    @FXML
    public void initGrid()
    {
        seqGrid.setMinWidth(900);
        seqGrid.setMinHeight(460);
        seqGrid.setMaxWidth(900);
        seqGrid.setMaxHeight(460);
        seqGrid.setAlignment(Pos.CENTER);
        //seqGrid.setGridLinesVisible(true);
        seqEditorBox.getChildren().add(seqGrid);
    }

    @FXML
    public void initGridState()
    {
        //seqGridState.setMinWidth(900);
        //seqGridState.setMinHeight(460);
        seqEditorBox.setMaxWidth(900);
        seqGridMsgs.setAlignment(Pos.TOP_CENTER);
        seqGridMsgs.setGridLinesVisible(true);

        msgWidth = seqEditorBox.getWidth() / 2;

        ColumnConstraints columnSpacer = new ColumnConstraints(msgWidth / 2);
        ColumnConstraints column = new ColumnConstraints(msgWidth);
        //RowConstraints row = new RowConstraints(30);
        //seqGridMsgs.getRowConstraints().add(row);
        seqGridMsgs.getColumnConstraints().addAll(columnSpacer, column, columnSpacer);
        seqMsgBox.getChildren().add(seqGridMsgs);
    }
}
