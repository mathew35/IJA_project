package project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.core.exc.*;
import com.fasterxml.jackson.databind.*;
import javafx.beans.value.*;

import javafx.fxml.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.stage.*;
import javafx.scene.input.*;

import javafx.scene.paint.Color;
import javafx.event.EventHandler;
import uml.*;

public class SequenceController
{
    SequenceDiagram sequenceDiagram = new SequenceDiagram();
    GridPane seqGrid = new GridPane();
    GridPane seqGridMsgs = new GridPane();
    GridPane seqGridAct = new GridPane();
    double msgWidth;

    int colFocus, rowFocus;

    @FXML
    private TabPane tabPane;

    @FXML
    private Button addSeqObjButton;

    @FXML
    private Button messageCreatorButton;

    @FXML
    private StackPane seqEditorBox;

    @FXML
    private VBox seqObjBox, seqMsgBox, seqActBox, addSeqObjFirst;

    @FXML
    private HBox optionSeqBox, seqEditorBack;

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
    
                initGridMsg();
                initGrid();
                initGridAct();
            };

            ObjectMapper objectMapper = new ObjectMapper();

            this.sequenceDiagram = loadSequence(objectMapper, file);
            displaySequence(sequenceDiagram);
            updateGrids();
        }
    }

    @FXML
    private void addSeqObj()
    {
        if (addSeqObjFirst != null)
        {
            seqEditorBox.getChildren().remove(optionSeqBox);
            addSeqObjFirst = null;

            initGridMsg();
            initGrid();
            initGridAct();
        };

        int colCount = sequenceDiagram.getNameClasses().size();
        VBox startObj = new VBox();
        
        Line startLine = new Line(0, 0, 0, seqEditorBox.getHeight() * 0.01);
        StackPane objRectangle = new StackPane();
        Rectangle rectangle = new Rectangle(125, 50);
        Effect effect = new DropShadow(BlurType.GAUSSIAN, Color.DODGERBLUE, 5, 0.75, 0, 0);
        TextField objField = new TextField();
        objField.setPrefWidth(125);

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
                colFocus = GridPane.getColumnIndex(clickedNode);
                rowFocus = GridPane.getRowIndex(clickedNode);
                System.out.println("Mouse clicked cell: " + colFocus + " And: " + rowFocus);

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
                else
                {
                    rectangle.setOnKeyPressed(new EventHandler<KeyEvent>() 
                    {
                        @Override
                        public void handle(KeyEvent ke) 
                        {
                            if (ke.getCode().equals(KeyCode.DELETE)) 
                            {
                                UMLClass clsRemove = sequenceDiagram.getClasses().get(colFocus);

                                for (int i = 0; i < sequenceDiagram.messages.size(); i++)
                                {
                                    if (sequenceDiagram.messages.get(i).sender.getName().equals(clsRemove.getName()) || sequenceDiagram.messages.get(i).receiver.getName().equals(clsRemove.getName()))
                                    {
                                        System.out.println("Removed: " + clsRemove.getName() + " " + sequenceDiagram.messages.get(i).sender.getName() + " " + sequenceDiagram.messages.get(i).receiver.getName());
                                        removeRow(seqGridMsgs, i);
                                        sequenceDiagram.messages.remove(i);
                                    }
                                }

                                sequenceDiagram.removeClassByIndex(colFocus);
                                removeColumn(seqGrid, colFocus);

                                if (sequenceDiagram.getClasses().size() < 7)
                                {
                                    addSeqObjButton.setDisable(false);
                                }


                                updateGrids();
                            }
                        }
                    });
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

                    Text objName = new Text(objField.getText());
                    objRectangle.getChildren().remove(objField);
                    objRectangle.getChildren().add(objName);

                    if (seqGrid.getColumnCount() >= 7)
                    {
                        addSeqObjButton.setDisable(true);
                    }
                    else
                    {
                        addSeqObjButton.setDisable(false);
                    }

                    updateGrids();
                }
            }
        });

        seqGrid.getColumnConstraints().add(new ColumnConstraints(-1, -1, -1, Priority.ALWAYS, HPos.CENTER, false));
        if (colCount == 0)
        {
            seqGrid.getRowConstraints().add(new RowConstraints(-1, -1, -1, Priority.ALWAYS, VPos.CENTER, false));
            seqGrid.getRowConstraints().add(new RowConstraints(-1, -1, -1, Priority.ALWAYS, VPos.CENTER, false));
        }
        
        /// Columns for acts
        seqGridAct.getColumnConstraints().add(new ColumnConstraints(-1, -1, -1, Priority.ALWAYS, HPos.CENTER, false));
        for (int i = 0; i < seqGridAct.getColumnCount(); i++)
        {
            seqGridAct.getColumnConstraints().set(i, new ColumnConstraints(0, seqGrid.getWidth() / (seqGrid.getColumnCount()), seqGrid.getWidth() / (seqGrid.getColumnCount()), Priority.ALWAYS, HPos.CENTER, false));
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

            //System.out.println(seqGrid.getColumnCount());
            seqGridMsgs.getColumnConstraints().add(seqGridMsgs.getColumnCount()-2, updateColumn);

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
    public void openActCreator() throws IOException
    {
        FXMLLoader loader = new FXMLLoader(Main.class.getClassLoader().getResource("activation.fxml"));
        Parent root = (Parent)loader.load();

        ActivationController actController = loader.getController();
        actController.loadData(sequenceDiagram);

        Stage popUp = new Stage();

        popUp.initModality(Modality.APPLICATION_MODAL);
        popUp.initOwner(Main.stage);
        popUp.setTitle("Create New Activation...");
        popUp.setScene(new Scene(root));
        popUp.setResizable(false);
        popUp.showAndWait();

        if (actController.createdAct != null)
        {
            sequenceDiagram.getClasses().get(actController.indexOfClass).addActivation(actController.createdAct);
            refreshActs();
        }
    }

    @FXML
    public void refreshActs() throws IOException
    {
        seqActBox.getChildren().remove(seqGridAct);
        seqGridAct = new GridPane();

        Integer messageCounter = 0;
        while (messageCounter < sequenceDiagram.messages.size())
        {
            Integer inCount = 0;
            Integer outCount = 0;

            for (int j = 0; j < sequenceDiagram.getClasses().get(j).getActivations().size(); j++)
            {
                Integer fromIndex = sequenceDiagram.getClasses().get(0).getActivations().get(j).getStart();
                Integer toIndex = sequenceDiagram.getClasses().get(0).getActivations().get(j).getEnd();

                if (fromIndex >= messageCounter && toIndex <= messageCounter)
                {
                    inCount++;

                    if (outCount > 0 || messageCounter == sequenceDiagram.messages.size() - 1)
                    {
                        Line line = new Line(0, 0, 0, 30 * outCount);
                        seqGridAct.add(line, 0, 0);

                        outCount = 0;
                    }
                }
                else
                {
                    outCount++;

                    if (inCount > 0 || messageCounter == sequenceDiagram.messages.size() - 1)
                    {
                        Rectangle rectangle = new Rectangle(20, 30 * inCount);
                        seqGridAct.add(rectangle, 0, 0);

                        inCount = 0;
                    }
                }
            }
            messageCounter++;
        }

        return;
    }

    @FXML
    public void displaySequence(SequenceDiagram diagram)
    {
        if (addSeqObjFirst != null)
        {
            seqEditorBox.getChildren().remove(addSeqObjFirst);
            seqObjBox.setPickOnBounds(false);
            addSeqObjFirst = null;

            initGrid();
            initGridMsg();
            initGridAct();
        }

        ArrayList<String> nameList = new ArrayList<String>();

        nameList = diagram.getNameClasses();
       
        for (int i = 0; i < nameList.size(); i++)
        {
            VBox startObj = new VBox();
            Line startLine = new Line(0, 0, 0, seqEditorBox.getHeight() * 0.01);
            StackPane objRectangle = new StackPane();
            Rectangle rectangle = new Rectangle(125, 50);
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

                    colFocus = GridPane.getColumnIndex(clickedNode);
                    rowFocus = GridPane.getRowIndex(clickedNode);

                    System.out.println("Mouse clicked cell: " + colFocus + " And: " + rowFocus);

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
                    else
                    {
                        rectangle.setOnKeyPressed(new EventHandler<KeyEvent>() 
                        {
                            @Override
                            public void handle(KeyEvent ke) 
                            {
                                if (ke.getCode().equals(KeyCode.DELETE)) 
                                {
                                    UMLClass clsRemove = sequenceDiagram.getClasses().get(colFocus);

                                    for (int i = 0; i < sequenceDiagram.messages.size(); i++)
                                    {
                                        System.out.println("Seeing " + sequenceDiagram.messages.get(i).message + ": " + clsRemove.getName() + " " + sequenceDiagram.messages.get(i).sender.getName() + " " + sequenceDiagram.messages.get(i).receiver.getName());
                                        if (sequenceDiagram.messages.get(i).sender.getName().equals(clsRemove.getName()) || sequenceDiagram.messages.get(i).receiver.getName().equals(clsRemove.getName()))
                                        {
                                            System.out.println("Removed " + sequenceDiagram.messages.get(i).message + ": " + clsRemove.getName() + " " + sequenceDiagram.messages.get(i).sender.getName() + " " + sequenceDiagram.messages.get(i).receiver.getName());
                                            removeRow(seqGridMsgs, i);
                                            sequenceDiagram.messages.remove(i);
                                            i--;
                                        }
                                    }
                                    
                                    sequenceDiagram.removeClassByIndex(colFocus);
                                    removeColumn(seqGrid, colFocus);
                                    System.out.println(sequenceDiagram.getNameClasses());

                                    if (sequenceDiagram.getClasses().size() < 7)
                                    {
                                        addSeqObjButton.setDisable(false);
                                    }

                                    updateGrids();
                                }
                            }
                        });
                    }
                }
            });

            startObj.setAlignment(Pos.CENTER);

            rectangle.setStroke(Color.BLACK);
            rectangle.setFill(Color.TRANSPARENT);

            objRectangle.getChildren().add(rectangle);

            seqGrid.getColumnConstraints().add(new ColumnConstraints(-1, -1, -1, Priority.ALWAYS, HPos.CENTER, false));
            if (i == 0)
            {
                seqGrid.getRowConstraints().add(new RowConstraints(-1, -1, -1, Priority.ALWAYS, VPos.CENTER, false));
                seqGrid.getRowConstraints().add(new RowConstraints(-1, -1, -1, Priority.ALWAYS, VPos.CENTER, false));
            }


            // TODO TODO TODO Mark Activation
            seqGridAct.getColumnConstraints().add(new ColumnConstraints(-1, -1, -1, Priority.ALWAYS, HPos.CENTER, false));

            //seqGridAct.setMinWidth(seqEditorBox.getWidth());
            
            objRectangle.getChildren().add(new Text(nameList.get(i)));

            startObj.getChildren().addAll(objRectangle, startLine);

            seqGrid.add(startObj, i, 0);

            Line timeAxis = new Line(0, 0, 0, seqEditorBox.getHeight() * 0.85);
            timeAxis.getStrokeDashArray().addAll(25d, 10d);

            seqGrid.add(timeAxis, i, 1);
            updateGridMsg();
        }

        for (int i = 0; i < sequenceDiagram.messages.size(); i++)
        {
            UMLMessage message = sequenceDiagram.messages.get(i);
            int msgCount = sequenceDiagram.messages.indexOf(message);
            int sendIndex = sequenceDiagram.getClassIndexByName(message.sender.getName());
            int recIndex = sequenceDiagram.getClassIndexByName(message.receiver.getName());
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

            if (message.order == -1 || msgCount == 0)
            {
                seqGridMsgs.getRowConstraints().add(new RowConstraints(30));
            }
            else
            {
                seqGridMsgs.getRowConstraints().add(sequenceDiagram.messages.size(), new RowConstraints(30));
                for (Node child : seqGridMsgs.getChildren()) 
                {
                    if (GridPane.getRowIndex(child) != null && GridPane.getRowIndex(child) >= message.order)
                    {
                        Integer rowIndex = GridPane.getRowIndex(child);
                        GridPane.setRowIndex(child, rowIndex == null ? 1 : 1 + rowIndex);
                    }
                }
                
                msgCount = message.order;
            }
            
            
            if (sendIndex < recIndex)
            {
                if (Math.abs(sendIndex - recIndex) != 1)
                {
                    for (int j = sendIndex + 1; j < recIndex; j++)
                    {
                        Line newLine = new Line(0, 0, seqEditorBox.getWidth() / seqGrid.getColumnCount(), 0);
                        if (message.transmition == false)
                        {
                            newLine.getStrokeDashArray().addAll(25d, 10d);
                        }
                        seqGridMsgs.add(newLine, j, msgCount);
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
                    for (int j = sendIndex; j > recIndex + 1; j--)
                    {
                        Line newLine = new Line(0, 0, seqEditorBox.getWidth() / seqGrid.getColumnCount(), 0);
                        if (message.transmition == false)
                        {
                            newLine.getStrokeDashArray().addAll(25d, 10d);
                        }
                        seqGridMsgs.add(newLine, j, msgCount);
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

            messageLabel.setOnMouseClicked((MouseEvent event) -> 
            {
                Effect effect = new DropShadow(BlurType.GAUSSIAN, Color.DODGERBLUE, 5, 0.75, 0, 0);
                Rectangle selectRec = new Rectangle(messageLabel.getWidth() + 5, messageLabel.getHeight() + 1);
                GridPane.setValignment(selectRec, VPos.TOP);
                GridPane.setHalignment(selectRec, HPos.CENTER);

                messageLabel.requestFocus();
                System.out.println("Textfield on focus");
                selectRec.setStroke(Color.DODGERBLUE);
                selectRec.setFill(Color.TRANSPARENT);
                selectRec.setEffect(selectRec.getEffect() == null ? effect : null);
                
                Node clickedNode = event.getPickResult().getIntersectedNode();
                if (clickedNode != seqGridMsgs) 
                {
                    // click on descendant node
                    Node parent = clickedNode.getParent();
                    while (parent != seqGridMsgs) 
                    {
                        clickedNode = parent;
                        parent = clickedNode.getParent();
                    }
                    Integer colIndex = GridPane.getColumnIndex(clickedNode);
                    Integer rowIndex = GridPane.getRowIndex(clickedNode);
                    System.out.println("Mouse clicked cell: " + colIndex + " And: " + rowIndex);

                    colFocus = colIndex;
                    rowFocus = rowIndex;

                    seqGridMsgs.add(selectRec, colIndex, rowIndex);
                }
            });

            messageLabel.focusedProperty().addListener(new ChangeListener<Boolean>()
            {
                @Override
                public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
                {
                    if (newPropertyValue)
                    {
                        messageLabel.setOnKeyPressed(new EventHandler<KeyEvent>() 
                        {
                            @Override
                            public void handle(KeyEvent ke) 
                            {
                                if (ke.getCode().equals(KeyCode.DELETE)) 
                                {
                                    removeRow(seqGridMsgs, rowFocus);
                                    sequenceDiagram.messages.remove(rowFocus);
                                    System.out.println(sequenceDiagram.getMessagesText());
                                }
                            }
                        });
                    }
                    else
                    {
                        System.out.println("Textfield out focus");
                        for (int j = 0; j < (seqGridMsgs.getChildren().size()); j++)
                        {
                            Node nodeMsg = seqGridMsgs.getChildren().get(j);
                            
                            if (nodeMsg instanceof Rectangle)
                            {
                                seqGridMsgs.getChildren().remove(j);
                            }
                        }
                    }
                }
            });

            //sequenceDiagram.messages.add(msgCount, message);
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

        messageLabel.setOnMouseClicked((MouseEvent event) -> 
        {
            messageLabel.requestFocus();
            System.out.println("Textfield on focus");
            Effect effect = new DropShadow(BlurType.GAUSSIAN, Color.DODGERBLUE, 5, 0.75, 0, 0);
            Rectangle selectRec = new Rectangle(messageLabel.getWidth() + 5, messageLabel.getHeight() + 1);
            GridPane.setValignment(selectRec, VPos.TOP);
            GridPane.setHalignment(selectRec, HPos.CENTER);
            selectRec.setStroke(Color.DODGERBLUE);
            selectRec.setFill(Color.TRANSPARENT);
            selectRec.setEffect(selectRec.getEffect() == null ? effect : null);
            
            Node clickedNode = event.getPickResult().getIntersectedNode();
            if (clickedNode != seqGridMsgs) 
            {
                // click on descendant node
                Node parent = clickedNode.getParent();
                while (parent != seqGridMsgs) 
                {
                    clickedNode = parent;
                    parent = clickedNode.getParent();
                }
                Integer colIndex = GridPane.getColumnIndex(clickedNode);
                Integer rowIndex = GridPane.getRowIndex(clickedNode);
                System.out.println("Mouse clicked cell: " + colIndex + " And: " + rowIndex);

                colFocus = colIndex;
                rowFocus = rowIndex;

                seqGridMsgs.add(selectRec, colIndex, rowIndex);
            }
        });

        messageLabel.focusedProperty().addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
            {
                if (newPropertyValue)
                {
                    messageLabel.setOnKeyPressed(new EventHandler<KeyEvent>() 
                    {
                        @Override
                        public void handle(KeyEvent ke) 
                        {
                            if (ke.getCode().equals(KeyCode.DELETE)) 
                            {
                                removeRow(seqGridMsgs, rowFocus);
                                sequenceDiagram.messages.remove(rowFocus);
                                System.out.println(sequenceDiagram.getMessagesText());
                            }
                        }
                    });
                }
                else
                {
                    System.out.println("Textfield out focus");
                    for (int i = 0; i < (seqGridMsgs.getChildren().size()); i++)
                    {
                        Node nodeMsg = seqGridMsgs.getChildren().get(i);
                        
                        if (nodeMsg instanceof Rectangle)
                        {
                            seqGridMsgs.getChildren().remove(i);
                        }
                    }
                }
            }
        });

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

        if (message.order == -1 || msgCount == 0)
        {
            seqGridMsgs.getRowConstraints().add(new RowConstraints(30));
        }
        else
        {
            seqGridMsgs.getRowConstraints().add(sequenceDiagram.messages.size(), new RowConstraints(30));
            for (Node child : seqGridMsgs.getChildren()) 
            {
                if (GridPane.getRowIndex(child) != null && GridPane.getRowIndex(child) >= message.order)
                {
                    Integer rowIndex = GridPane.getRowIndex(child);
                    GridPane.setRowIndex(child, rowIndex == null ? 1 : 1 + rowIndex);
                }
            }
              
            msgCount = message.order;
        }
        
        
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

        sequenceDiagram.messages.add(msgCount, message);
        //System.out.println("Sender index: " + sendIndex);
        //System.out.println("Receiver index: " + recIndex);
    }

    @FXML
    public void initGrid()
    {
        seqGrid.setMaxWidth(1220);
        seqGrid.setAlignment(Pos.CENTER);
        //seqGrid.setGridLinesVisible(true);
        seqGrid.setPickOnBounds(false);
        seqObjBox.getChildren().add(seqGrid);
    }

    @FXML
    public void initGridMsg()
    {
        seqGridMsgs.setMaxWidth(1220);
        seqGridMsgs.setAlignment(Pos.TOP_CENTER);
        //seqGridMsgs.setGridLinesVisible(true);

        msgWidth = seqEditorBox.getWidth() / 2;

        ColumnConstraints columnSpacer = new ColumnConstraints(msgWidth / 2);
        ColumnConstraints column = new ColumnConstraints(msgWidth);
        seqGridMsgs.getColumnConstraints().addAll(columnSpacer, column, columnSpacer);
        seqMsgBox.getChildren().add(seqGridMsgs);
    }

    @FXML
    public void initGridAct()
    {
        seqGridAct.setMaxWidth(1220);
        seqGridAct.setAlignment(Pos.TOP_CENTER);
        seqGridAct.setGridLinesVisible(true);
        seqGridAct.setPickOnBounds(false);
        seqGridAct.getRowConstraints().add(new RowConstraints(30));
        seqActBox.getChildren().add(seqGridAct);
    }

    /**
     * Gets row index constrain for given node, forcefully as integer: 0 as null.
     * @param node Node to look up the constraint for.
     * @return The row index as primitive integer.
     */
    public static int getRowIndexAsInteger(Node node) {
        final var a = GridPane.getRowIndex(node);
        if (a == null) {
            return 0;
        }
        return a;
    }

    public static int getColumnIndexAsInteger(Node node) {
        final var a = GridPane.getColumnIndex(node);
        if (a == null) {
            return 0;
        }
        return a;
    }

    /**
     * Removes row from grid pane by index.
     * Note: Might not work correctly if row spans are used.
     * @param grid Grid pane to be affected.
     * @param targetRowIndexIntegerObject Target row index to be removed. Integer object type, because for some reason `getRowIndex` returns null for children at 0th row.
     */
    public static void removeRow(GridPane grid, Integer targetRowIndexIntegerObject) {
        final int targetRowIndex = targetRowIndexIntegerObject == null ? 0 : targetRowIndexIntegerObject;

        // Remove children from row
        grid.getChildren().removeIf(node -> getRowIndexAsInteger(node) == targetRowIndex);

        // Update indexes for elements in further rows
        grid.getChildren().forEach(node -> {
            final int rowIndex = getRowIndexAsInteger(node);
            if (targetRowIndex < rowIndex) {
                GridPane.setRowIndex(node, rowIndex - 1);
            }
        });

        // Remove row constraints
        grid.getRowConstraints().remove(targetRowIndex);
    }

    public static void removeColumn(GridPane grid, Integer targetColumnIndexIntegerObject) {
        final int targetColumnIndex = targetColumnIndexIntegerObject == null ? 0 : targetColumnIndexIntegerObject;

        // Remove children from row
        grid.getChildren().removeIf(node -> getColumnIndexAsInteger(node) == targetColumnIndex);

        // Update indexes for elements in further rows
        grid.getChildren().forEach(node -> {
            final int rowIndex = getColumnIndexAsInteger(node);
            if (targetColumnIndex < rowIndex) {
                GridPane.setColumnIndex(node, rowIndex - 1);
            }
        });

        // Remove row constraints
        grid.getColumnConstraints().remove(targetColumnIndex);
    }

    public void updateGrids()
    {
        addSeqObjFirst = new VBox();
        seqObjBox.getChildren().remove(seqGrid);
        seqGrid = new GridPane();

        seqMsgBox.getChildren().remove(seqGridMsgs);
        seqGridMsgs = new GridPane();

        displaySequence(sequenceDiagram);
    }
}
