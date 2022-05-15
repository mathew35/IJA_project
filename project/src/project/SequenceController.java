package project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.exc.*;
import com.fasterxml.jackson.databind.*;
import javafx.beans.value.*;

import javafx.fxml.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.stage.*;
import javafx.util.Callback;
import javafx.scene.input.*;

import javafx.scene.paint.Color;
import javafx.event.EventHandler;

import uml.*;

public class SequenceController
{
    SequenceDiagram sequenceDiagram = new SequenceDiagram();
    ClassDiagram classDiagram = new ClassDiagram();
    GridPane seqGrid = new GridPane();
    GridPane seqGridMsgs = new GridPane();
    GridPane seqGridAct = new GridPane();
    double msgWidth;
    int colFocus, rowFocus;
    EditorController editorStatic;

    ArrayList<SequenceDiagram> snapshots = new ArrayList<SequenceDiagram>();
    int snapshotPos = -1;

    @FXML
    private TabPane tabPane;

    @FXML
    private Button addSeqObjButton, messageCreatorButton, actCreatorButton, undoButton, redoButton, removeObjButton;

    @FXML
    private StackPane seqEditorBox;

    @FXML
    private VBox seqObjBox, seqMsgBox, seqActBox, addSeqObjFirst;

    @FXML
    private HBox optionSeqBox, seqEditorBack;

    @FXML
    private Region actRegion, msgRegion;

    Callback<ListView<UMLClass>, ListCell<UMLClass>> cellFactory = new Callback<ListView<UMLClass>, ListCell<UMLClass>>() {

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

    public void initialize()
    {
        addSeqObjButton.setTooltip(new Tooltip("Add Object"));
        removeObjButton.setTooltip(new Tooltip("Remove Object"));
        messageCreatorButton.setTooltip(new Tooltip("Add Message..."));
        actCreatorButton.setTooltip(new Tooltip("Add Activation Interval..."));

        undoButton.setTooltip(new Tooltip("Undo"));
        redoButton.setTooltip(new Tooltip("Redo"));
    }

    public void setClassDiagram(ClassDiagram cDiagram)
    {
        sequenceDiagram.deepCopy(cDiagram);
        for(UMLClass i:sequenceDiagram.getClasses()){
            for(UMLInstance j:sequenceDiagram.instances){
                if(i.getName().equals(j.asgclass.getName())){
                    ArrayList<String> toRM = new ArrayList<String>();
                    for(UMLOperation oper: j.asgclass.getOperations()){
                        toRM.add(oper.getName());
                    }
                    for(String oper:toRM){
                        j.asgclass.removeAttrOper(oper);
                    }
                    for(UMLOperation oper:i.getOperations()){
                        j.asgclass.addOperation(oper);
                    }
                }
            }
        }
        // System.out.println(sequenceDiagram.getClasses());
        checkIncon();
    }

    public void changeInOperation(UMLOperation oldOper, UMLOperation newOper, UMLClass uclass){
        for(UMLClass i:sequenceDiagram.getClasses()){
            if(i.getName().equals(uclass.getName())){
                for(UMLOperation j:i.getOperations()){
                        if(j.getName().equals(oldOper.getName())){
                            j.rename(newOper.getName());
                            break;
                        }
                }
                break;
            }
        }
        for(UMLInstance i:sequenceDiagram.instances){
            if(i.asgclass.getName().equals(uclass.getName())){
                for(UMLOperation j:i.asgclass.getOperations()){
                        if(j.getName().equals(oldOper.getName())){
                            j.rename(newOper.getName());
                            break;
                        }
                }
            }
        }
        for(UMLMessage i:sequenceDiagram.messages){
            if(i.receiver.asgclass.getName().equals(uclass.getName())){
                if(i.operation.getName().equals(oldOper.getName())){
                    i.operation.rename(newOper.getName());
                }
            }
        }
        updateGrids();  
    }
        
    public void changeInClass(UMLClass oldClass, UMLClass newClass){
        for(UMLClass i:sequenceDiagram.getClasses()){
            if(i.getName().equals(oldClass.getName())){
                i.rename(newClass.getName());
                break;
            }
        }
        for(UMLInstance i:sequenceDiagram.instances){
            if(i.asgclass.getName().equals(oldClass.getName())){
                i.asgclass.rename(newClass.getName());
            }
        }
        for(UMLMessage i:sequenceDiagram.messages){
            if(i.sender.asgclass.getName().equals(oldClass.getName())){
                i.sender.asgclass.rename(newClass.getName());
            }
            if(i.receiver.asgclass.getName().equals(oldClass.getName())){
                i.receiver.asgclass.rename(newClass.getName());
            }
        }
        updateGrids();        
    }

    @FXML
    public void onUndoClick()
    {
        if(snapshotPos > 0)
        {
            snapshotPos--;
            sequenceDiagram = snapshots.get(snapshotPos).deepCopySeq();
            updateGrids();

            if (snapshotPos == 0)
            {
                undoButton.setDisable(true);
            }

            if (snapshotPos != snapshots.size())
            {
                redoButton.setDisable(false);
            }
        }
    }

    @FXML
    public void removeObj()
    {
        UMLInstance instRemove = sequenceDiagram.instances.get(colFocus);

        for (int i = 0; i < sequenceDiagram.messages.size(); i++)
        {
            //System.out.println("Seeing " + sequenceDiagram.messages.get(i).message + ": " + instRemove.instancename + " " + sequenceDiagram.messages.get(i).sender.instancename + " " + sequenceDiagram.messages.get(i).receiver.instancename);
            if (sequenceDiagram.messages.get(i).sender.instancename.equals(instRemove.instancename) || sequenceDiagram.messages.get(i).receiver.instancename.equals(instRemove.instancename))
            {
                //System.out.println("Removed " + sequenceDiagram.messages.get(i).message + ": " + instRemove.instancename + " " + sequenceDiagram.messages.get(i).sender.instancename + " " + sequenceDiagram.messages.get(i).receiver.instancename);
                removeRow(seqGridMsgs, i);
                sequenceDiagram.messages.remove(i);
                i--;
            }
        }
        
        sequenceDiagram.instances.remove(colFocus);
        removeColumn(seqGrid, colFocus);

        if (sequenceDiagram.instances.size() < 8)
        {
            addSeqObjButton.setDisable(false);
        }

        updateGrids();
        createSnapshot(sequenceDiagram);
        removeObjButton.setDisable(true);
    }

    @FXML
    public void onRedoClick()
    {
        if(snapshots.size()>snapshotPos+1)
        {
            snapshotPos++;
            sequenceDiagram  = snapshots.get(snapshotPos).deepCopySeq();
            updateGrids();

            if (snapshotPos + 1 == snapshots.size())
            {
                redoButton.setDisable(true);
            }

            if (snapshotPos != 0)
            {
                undoButton.setDisable(false);
            }
        }
    }
    public void createSnapshot(SequenceDiagram diag)
    {
        while(snapshots.size()>snapshotPos+1)
        {
            snapshots.remove(snapshotPos+1);
        }
        snapshotPos++;
        snapshots.add(diag.deepCopySeq());

        if (snapshotPos != 0)
        {
            undoButton.setDisable(false);
        }

        if (snapshotPos + 1 != snapshots.size())
        {
            redoButton.setDisable(false);
        }
    }

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

            SequenceDiagram sequenceDiagramLoad = loadSequence(objectMapper, file);

            sequenceDiagram.instances = sequenceDiagramLoad.instances;
            for(UMLInstance j:sequenceDiagram.instances){
                for(UMLClass i:sequenceDiagram.getClasses()){
                    if(i.getName().equals(j.asgclass.getName())){
                        ArrayList<String> toRM = new ArrayList<String>();
                        for(UMLOperation oper: j.asgclass.getOperations()){
                            toRM.add(oper.getName());
                        }
                        for(String oper:toRM){
                            j.asgclass.removeAttrOper(oper);
                        }
                        for(UMLOperation oper:i.getOperations()){
                            j.asgclass.addOperation(oper);
                        }
                    }
                }
            }
            sequenceDiagram.messages = sequenceDiagramLoad.messages;

            displaySequence(sequenceDiagram, true);
            createSnapshot(sequenceDiagram);
            checkIncon();
        }
    }

    private void checkIncon()
    {
        List<UMLClass> classes = sequenceDiagram.getClasses();

        for (int i = 0; i < sequenceDiagram.instances.size(); i++)
        {
            boolean found = false;
            for (int j = 0; j < classes.size(); j++)
            {
                if (classes.get(j).getName().equals(sequenceDiagram.instances.get(i).asgclass.getName()))
                {
                    found = true;
                }
            }

            if (found == false)
            {
                for (int j = 0; j < seqGrid.getRowCount(); j++)
                {
                    //System.out.println("i: " + i + " j: " + j + " = " + (i + j * sequenceDiagram.instances.size()));
                    //System.out.println(seqGrid.getChildren().get(i + j * sequenceDiagram.instances.size()));
            
                    for (Node node : seqGrid.getChildren()) 
                    {
                        if (GridPane.getColumnIndex(node) == i && GridPane.getRowIndex(node) == j) 
                        {
                            if (node instanceof VBox)
                            {
                                StackPane stacked = (StackPane)((VBox)node).getChildren().get(0);
                                ((Rectangle)stacked.getChildren().get(0)).setStroke(Color.RED);
                                ((Text)stacked.getChildren().get(1)).setFill(Color.RED);
                            }

                            if (node instanceof Line)
                            {
                                ((Line)node).setStroke(Color.RED);
                            }
                        }
                    }
                }

                Integer messageCounter = 0;
                Integer maxDraw = seqGridAct.getRowCount();

                while (messageCounter < maxDraw + 1)
                {
                    //System.out.println("Counter: " + messageCounter);
                    for (int j = 0; j < sequenceDiagram.instances.get(i).activations.size(); j++)
                    {
                        Integer fromIndex = sequenceDiagram.instances.get(i).activations.get(j).getStart();
                        Integer toIndex = sequenceDiagram.instances.get(i).activations.get(j).getEnd();
                        boolean deactivated = sequenceDiagram.instances.get(i).activations.get(j).getDeactivation();

                        for (Node node : seqGridAct.getChildren()) 
                        {
                            if (GridPane.getColumnIndex(node) == i && GridPane.getRowIndex(node) == messageCounter) 
                            {
                                if (deactivated == false)
                                {
                                    if (node instanceof Region)
                                    {
                                        ((Region)node).setStyle("-fx-border-color: red; -fx-border-width: 1; -fx-border-style: hidden solid hidden solid;");
                                        
                                        if (fromIndex == messageCounter)
                                        {
                                            ((Region)node).setStyle("-fx-border-color: red; -fx-border-width: 1; -fx-border-style: solid solid hidden solid");
                                        }
                    
                                        if (toIndex == messageCounter)
                                        {
                                            ((Region)node).setStyle("-fx-border-color: red; -fx-border-width: 1; -fx-border-style: hidden solid solid solid;");
                                        }
                    
                                        if (fromIndex == messageCounter && toIndex == messageCounter)
                                        {
                                            ((Region)node).setStyle("-fx-border-color: red; -fx-border-width: 1; -fx-border-style: solid solid solid solid; -fx-fill: white;");
                                        }
                                    }
                                }                                    
                            }
                        }
                    }
                    messageCounter++;
                }
            }
        }

        for (int i = 0; i < seqGridMsgs.getRowCount(); i++)
        {
            if (sequenceDiagram.messages.get(i).operation != null)
            {
                // System.out.println(sequenceDiagram.messages.get(i).operation.getName());
                boolean found = false;
                for (int j = 0; j < classes.size(); j++)
                {
                    for (int k = 0; k <  classes.get(j).getOperations().size(); k++)
                    {
                        if (classes.get(j).getOperations().get(k).getName().equals(sequenceDiagram.messages.get(i).operation.getName()))
                        {
                            found = true;
                        }
                    }
                }

                if (found == false)
                {
                    for (Node node : seqGridMsgs.getChildren()) 
                    {
                        if (GridPane.getRowIndex(node) == i) 
                        {
                            //System.out.println(node);
                            if (node instanceof Label)
                            {
                                ((Label)node).setTextFill(Color.RED);
                            }

                            if (node instanceof UMLArrow)
                            {
                                ((UMLArrow)node).setColorRed();
                            }

                            if (node instanceof Line)
                            {
                                ((Line)node).setStroke(Color.RED);
                            }
                        }
                    }
                }
            }
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

        undoButton.setDisable(true);
        redoButton.setDisable(true);

        int colCount = sequenceDiagram.instances.size();
        VBox startObj = new VBox();
        startObj.setPadding(new Insets(0, 0, 5, 0));
        HBox rectangleAlignment = new HBox();
        rectangleAlignment.setPrefWidth(125);
        rectangleAlignment.setAlignment(Pos.CENTER);

        StackPane objRectangle = new StackPane();
        Rectangle rectangle = new Rectangle(125, 50);
        Effect effect = new DropShadow(BlurType.GAUSSIAN, Color.DODGERBLUE, 5, 0.75, 0, 0);
        TextField objField = new TextField();
        Text spacer = new Text(":");

        ComboBox<UMLClass> dropClasses = new ComboBox<UMLClass>();
        dropClasses.setButtonCell(cellFactory.call(null));
        dropClasses.setCellFactory(cellFactory);
        dropClasses.getItems().addAll(sequenceDiagram.getClasses());

        objField.setPrefWidth(50);

        rectangle.setOnMouseClicked((MouseEvent event) -> 
        {
            rectangle.requestFocus();
            Node clickedNode = event.getPickResult().getIntersectedNode();
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
                // System.out.println("Mouse clicked cell: " + colFocus + " And: " + rowFocus);

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

                    removeObjButton.setOnMouseClicked((MouseEvent event) -> 
                    {
                        removeObj();
                    });
                    removeObjButton.setDisable(true);
                }
                else
                {
                    removeObjButton.setDisable(false);
                    rectangle.setOnKeyPressed(new EventHandler<KeyEvent>() 
                    {
                        @Override
                        public void handle(KeyEvent ke) 
                        {
                            if (ke.getCode().equals(KeyCode.DELETE)) 
                            {
                                Alert alert = new Alert(AlertType.CONFIRMATION, "Deleting instance will result in deletion of associated messages",  ButtonType.NO, ButtonType.YES);
                                alert.showAndWait();

                                if (alert.getResult() == ButtonType.YES) {
                                    removeObj();
                                }
                            }
                        }
                    });
                }
            }
        });

        startObj.setAlignment(Pos.CENTER);

        rectangle.setStroke(Color.BLACK);
        rectangle.setFill(Color.WHITE);

        objRectangle.getChildren().add(rectangle);
        objRectangle.getChildren().add(rectangleAlignment);
        addSeqObjButton.setDisable(true);

        // Multiple Event Handler https://stackoverflow.com/questions/31794167/how-to-handle-multiple-event-types-from-one-class-in-javafx
        objField.setOnKeyPressed(new EventHandler<KeyEvent>() 
        {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) 
                {
                    if (dropClasses.getValue() != null)
                    {  
                        boolean foundSame = false;
                        for (int i = 0; i < sequenceDiagram.instances.size(); i++)
                        {
                            if (sequenceDiagram.instances.get(i).instancename.equals(objField.getText()) && sequenceDiagram.instances.get(i).asgclass.equals(dropClasses.getValue()))
                            {
                                foundSame = true;
                            }
                        }

                        if (foundSame == false)
                        {
                            sequenceDiagram.createInstance(objField.getText(), dropClasses.getValue());

                            if (seqGrid.getColumnCount() >= 8)
                            {
                                addSeqObjButton.setDisable(true);
                            }
                            else
                            {
                                addSeqObjButton.setDisable(false);
                            }

                            updateGrids();
                            createSnapshot(sequenceDiagram);
                            undoButton.setDisable(false);
                            redoButton.setDisable(false);
                        }                      
                    }
                }
            }
        });

        seqGrid.getColumnConstraints().add(new ColumnConstraints(-1, -1, -1, Priority.ALWAYS, HPos.CENTER, false));
        if (colCount == 0)
        {
            seqGrid.getRowConstraints().add(new RowConstraints(-1, -1, -1, Priority.ALWAYS, VPos.CENTER, false));
            seqGrid.getRowConstraints().add(new RowConstraints(-1, -1, -1, Priority.ALWAYS, VPos.CENTER, false));
        }
        
        seqGridAct.getColumnConstraints().add(new ColumnConstraints(-1, -1, -1, Priority.ALWAYS, HPos.CENTER, false));
        for (int i = 0; i < seqGridAct.getColumnCount(); i++)
        {
            seqGridAct.getColumnConstraints().set(i, new ColumnConstraints(seqEditorBox.getWidth()/seqGrid.getColumnCount()));
        }
        rectangleAlignment.getChildren().addAll(objField, spacer, dropClasses);

        startObj.getChildren().addAll(objRectangle);

        seqGrid.add(startObj, colCount, 0);
        objField.requestFocus();

        Line timeAxis = new Line(0, 0, 0, seqEditorBox.getHeight() * 0.85);
        timeAxis.getStrokeDashArray().addAll(25d, 10d);

        seqGrid.add(timeAxis, colCount, 1);
        updateGridMsg();
    }

    @FXML // Done for Instances
    public void updateGridMsg()
    {
        if (seqGrid.getColumnCount() > 2)
        {
            ColumnConstraints updateColumn = new ColumnConstraints(seqEditorBox.getWidth()/seqGrid.getColumnCount());
            ColumnConstraints updateSpacerColumn = new ColumnConstraints((seqEditorBox.getWidth()/seqGrid.getColumnCount())/2);

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

        if (msgController.msgType != 2)
        {
            if (msgController.createdMessage != null)
            {
                createMessage(msgController.createdMessage);
                updateGrids();
            }
        }
        else
        {
            if (msgController.createdMessage != null && msgController.createdInstance != null)
            {
                sequenceDiagram.instances.add(msgController.createdInstance);
                createMessage(msgController.createdMessage);
                sequenceDiagram.instances.get(sequenceDiagram.instances.size() - 1).addActivation(new UMLActivation(0, sequenceDiagram.messages.size() - 1, true));
                updateGrids();
                refreshActs();
            }
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
            sequenceDiagram.instances.get(actController.indexOfInstance).addActivation(actController.createdAct);
            createSnapshot(sequenceDiagram);
            refreshActs();
        }
    }

    @FXML
    public void refreshActs()
    {
        int maxDraw = 0;
        for (int i = 0; i < sequenceDiagram.instances.size(); i++)
        {
            for (int j = 0; j < sequenceDiagram.instances.get(i).activations.size(); j++)
            {
                Integer fromIndex = sequenceDiagram.instances.get(i).activations.get(j).getStart();
                Integer toIndex = sequenceDiagram.instances.get(i).activations.get(j).getEnd();
                if (maxDraw < fromIndex)
                {
                    maxDraw = fromIndex;
                }

                if (maxDraw < toIndex)
                {
                    maxDraw = toIndex;
                }
            }
        }
        
        for (int i = 0; i < sequenceDiagram.instances.size(); i++)
        {
            //System.out.print(sequenceDiagram.instances.get(i).asgclass.getName());
            Integer messageCounter = 0;
            while (messageCounter < maxDraw + 1)
            {
                //System.out.println("Counter: " + messageCounter);
                for (int j = 0; j < sequenceDiagram.instances.get(i).activations.size(); j++)
                {
                    Integer fromIndex = sequenceDiagram.instances.get(i).activations.get(j).getStart();
                    Integer toIndex = sequenceDiagram.instances.get(i).activations.get(j).getEnd();
                    boolean deactivated = sequenceDiagram.instances.get(i).activations.get(j).getDeactivation();

                    System.out.println("Adding to " + sequenceDiagram.instances.get(i).asgclass.getName() + " Start: " + fromIndex + " End: " + toIndex + " Counter: " + messageCounter);
    
                    if (sequenceDiagram.instances.get(i).activations.get(j).isInBounds(messageCounter))
                    {
                        if (deactivated == false)
                        {
                            Region regionAct = new Region();
                            Rectangle rectangle = new Rectangle(20, 30);

                            rectangle.setFill(Color.TRANSPARENT);

                            regionAct.setPrefSize(20, 30);
                            regionAct.setMaxSize(20, 30);
                            regionAct.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), new Insets(0))));
                            regionAct.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-border-style: hidden solid hidden solid;");
                            
                            if (fromIndex == messageCounter)
                            {
                                regionAct.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-border-style: solid solid hidden solid");
                            }
        
                            if (toIndex == messageCounter)
                            {
                                regionAct.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-border-style: hidden solid solid solid;");
                            }
        
                            if (fromIndex == messageCounter && toIndex == messageCounter)
                            {
                                regionAct.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-border-style: solid solid solid solid; -fx-fill: white;");
                            }
        
                            GridPane.setValignment(regionAct, VPos.TOP);
                            GridPane.setHalignment(regionAct, HPos.CENTER);

                            GridPane.setValignment(rectangle, VPos.TOP);
                            GridPane.setHalignment(rectangle, HPos.CENTER);

                            rectangle.setOnMouseClicked((MouseEvent event) -> 
                            {
                                rectangle.requestFocus();
                                
                                Node clickedNode = event.getPickResult().getIntersectedNode();
                                if (clickedNode != seqGridAct) 
                                {
                                    // click on descendant node
                                    Node parent = clickedNode.getParent();
                                    while (parent != seqGridAct) 
                                    {
                                        clickedNode = parent;
                                        parent = clickedNode.getParent();
                                    }
                                    Integer colIndex = GridPane.getColumnIndex(clickedNode);
                                    Integer rowIndex = GridPane.getRowIndex(clickedNode);
                                    // System.out.println("Mouse clicked cell: " + colIndex + " And: " + rowIndex);

                                    colFocus = colIndex;
                                    rowFocus = rowIndex;
                                }
                            });

                            rectangle.focusedProperty().addListener(new ChangeListener<Boolean>()
                            {
                                @Override
                                public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
                                {
                                    if (newPropertyValue)
                                    {
                                        rectangle.setOnKeyPressed(new EventHandler<KeyEvent>() 
                                        {
                                            @Override
                                            public void handle(KeyEvent ke) 
                                            {
                                                if (ke.getCode().equals(KeyCode.DELETE)) 
                                                {
                                                    UMLInstance instace = sequenceDiagram.instances.get(colFocus);
                                                    List<UMLActivation> instActivations = instace.getActivations();

                                                    for (int i = 0; i < instActivations.size(); i++)
                                                    {
                                                        if (rowFocus >= instActivations.get(i).getStart() && rowFocus <= instActivations.get(i).getEnd())
                                                        {
                                                            instace.removeActivation(instActivations.get(i));
                                                            i--;
                                                        }
                                                    }

                                                    updateGrids();
                                                    createSnapshot(sequenceDiagram);
                                                }
                                            }
                                        });
                                    }
                                }
                            });
        
                            seqGridAct.add(regionAct, i, messageCounter);
                            seqGridAct.add(rectangle, i, messageCounter);
                        }
                        else
                        {
                            Rectangle rectangle = new Rectangle(20, 30);
                            rectangle.setFill(Color.web("f4f4f4"));
                            GridPane.setValignment(rectangle, VPos.TOP);
                            GridPane.setHalignment(rectangle, HPos.CENTER);

                            if (toIndex == messageCounter)
                            {
                                rectangle.setHeight(15);
                            }

                            seqGridAct.add(rectangle, i, messageCounter);
                        }
                    }
                }
                messageCounter++;
            }
        }

        for (int i = 0; i < seqGridAct.getColumnCount(); i++)
        {
            seqGridAct.getColumnConstraints().set(i, new ColumnConstraints(seqEditorBox.getWidth()/seqGridAct.getColumnCount())); 
        }

        return;
    }
    

    @FXML
    public void displaySequence(SequenceDiagram diagram, boolean loading)
    {
        sequenceDiagram = diagram;

        if (addSeqObjFirst != null)
        {
            seqEditorBox.getChildren().remove(addSeqObjFirst);
            seqObjBox.setPickOnBounds(false);
            addSeqObjFirst = null;

            initGrid();
            initGridMsg();
            initGridAct();
        }
       
        for (int i = 0; i < sequenceDiagram.instances.size(); i++)
        {
            VBox startObj = new VBox();
            startObj.setPadding(new Insets(0, 0, 5, 0)); //margins around the whole grid
            StackPane objRectangle = new StackPane();
            Rectangle rectangle = new Rectangle(125, 50);
            Effect effect = new DropShadow(BlurType.GAUSSIAN, Color.DODGERBLUE, 5, 0.75, 0, 0);

            rectangle.setOnMouseClicked((MouseEvent event) -> 
            {
                rectangle.requestFocus();
                Node clickedNode = event.getPickResult().getIntersectedNode();
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

                    // System.out.println("Mouse clicked cell: " + colFocus + " And: " + rowFocus);

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
                        removeObjButton.setOnMouseClicked((MouseEvent event) -> 
                        {
                            removeObj();
                        });
                        removeObjButton.setDisable(true);
                    }
                    else
                    {
                        removeObjButton.setDisable(false);
                        rectangle.setOnKeyPressed(new EventHandler<KeyEvent>() 
                        {
                            @Override
                            public void handle(KeyEvent ke) 
                            {
                                if (ke.getCode().equals(KeyCode.DELETE)) 
                                {
                                    Alert alert = new Alert(AlertType.CONFIRMATION, "Deleting instance will result in deletion of associated messages. Are you sure you want to continue?",  ButtonType.NO, ButtonType.YES);
                                    alert.showAndWait();

                                    if (alert.getResult() == ButtonType.YES) {
                                        removeObj();
                                    }
                                }
                            }
                        });
                    }
                }
            });

            startObj.setAlignment(Pos.CENTER);

            rectangle.setStroke(Color.BLACK);
            rectangle.setFill(Color.WHITE);

            seqGrid.getColumnConstraints().add(new ColumnConstraints(-1, -1, -1, Priority.ALWAYS, HPos.CENTER, false));
            if (i == 0)
            {
                seqGrid.getRowConstraints().add(new RowConstraints(-1, -1, -1, Priority.ALWAYS, VPos.CENTER, false));
                seqGrid.getRowConstraints().add(new RowConstraints(-1, -1, -1, Priority.ALWAYS, VPos.CENTER, false));
            }

            seqGridAct.getColumnConstraints().add(new ColumnConstraints(-1, -1, -1, Priority.ALWAYS, HPos.CENTER, false));
            for (int j = 0; j < seqGridAct.getColumnCount(); j++)
            {
                seqGridAct.getColumnConstraints().set(j, new ColumnConstraints(seqEditorBox.getWidth()/seqGrid.getColumnCount()));
            }
            
            objRectangle.getChildren().addAll(rectangle, new Text(sequenceDiagram.instances.get(i).instancename + ":" + sequenceDiagram.instances.get(i).asgclass.getName()));

            //objRectangle.getChildren().add(rectangle);

            startObj.getChildren().addAll(objRectangle);

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

            int sendIndex = sequenceDiagram.getIndexOfInstace(message.sender);
            int recIndex = sequenceDiagram.getIndexOfInstace(message.receiver);
            //System.out.println("1 sendIndex: " + sendIndex + " recIndex: " + recIndex);

            if (loading == true)
            {
                sendIndex = sequenceDiagram.getIndexOfInstace(message.sender);
                recIndex = sequenceDiagram.getIndexOfInstace(message.receiver);
            }
            
            Label messageLabel = new Label();

            GridPane.setValignment(messageLabel, VPos.TOP);
            GridPane.setHalignment(messageLabel, HPos.CENTER);

            if (message.operation != null)
            {    
                messageLabel.setText(message.operation.getName() + "(" + message.message +")");
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
                        if (message.transmition == 3)
                        {
                            newLine.getStrokeDashArray().addAll(25d, 10d);
                        }
                        seqGridMsgs.add(newLine, j, msgCount);
                        GridPane.setValignment(newLine, VPos.CENTER); 
                    }
                }

                if (message.transmition == 0 || message.transmition == 1 || message.transmition == 2)
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
                        if (message.transmition == 3)
                        {
                            newLine.getStrokeDashArray().addAll(25d, 10d);
                        }
                        seqGridMsgs.add(newLine, j, msgCount);
                        GridPane.setValignment(newLine, VPos.CENTER); 
                    }
                }
                if (message.transmition == 0 || message.transmition == 1 || message.transmition == 2)
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
                // System.out.println("Textfield on focus");
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
                    // System.out.println("Mouse clicked cell: " + colIndex + " And: " + rowIndex);

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
                                    if (sequenceDiagram.messages.get(rowFocus).transmition == 2)
                                    {
                                        colFocus = sequenceDiagram.getIndexOfInstace(sequenceDiagram.messages.get(rowFocus).receiver);
                                        removeObj();
                                    }

                                    for (int j = 0; j < sequenceDiagram.instances.size(); j++)
                                    {
                                        for (int i = 0; i < sequenceDiagram.instances.get(j).activations.size(); i++)
                                        {
                                            System.out.println("colFocus: " + colFocus + " i: " + i);
                                            if (sequenceDiagram.instances.get(j).activations.get(i).getDeactivation() == true)
                                            {
                                                if (sequenceDiagram.instances.get(j).activations.get(i).isInBounds(rowFocus))
                                                {
                                                    sequenceDiagram.instances.get(j).activations.get(i).setEnd(sequenceDiagram.instances.get(j).activations.get(i).getEnd() - 1);
                                                }
                                            }
                                            else
                                            {
                                                if (sequenceDiagram.instances.get(j).activations.get(i).isInBounds(rowFocus))
                                                {
                                                    sequenceDiagram.instances.get(j).activations.get(i).setEnd(sequenceDiagram.instances.get(j).activations.get(i).getEnd() - 1);
                                                    if (sequenceDiagram.instances.get(j).activations.get(i).getEnd() < sequenceDiagram.instances.get(j).activations.get(i).getStart())
                                                    {
                                                        sequenceDiagram.instances.get(j).activations.remove(i);
                                                    }
                                                }
                                                else if (sequenceDiagram.instances.get(j).activations.get(i).getStart() > rowFocus)
                                                {
                                                    sequenceDiagram.instances.get(j).activations.get(i).setEnd(sequenceDiagram.instances.get(j).activations.get(i).getEnd() - 1);
                                                    sequenceDiagram.instances.get(j).activations.get(i).setStart(sequenceDiagram.instances.get(j).activations.get(i).getStart() - 1);
                                                }
                                            }
                                        }
                                    }

                                    removeRow(seqGridMsgs, rowFocus);
                                    sequenceDiagram.messages.remove(rowFocus);

                                    updateGrids();
                                    createSnapshot(sequenceDiagram);
                                }
                            }
                        });
                    }
                    else
                    {
                        // System.out.println("Textfield out focus");
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

            seqGridAct.getRowConstraints().add(new RowConstraints(30));
        }

        refreshActs();
    }

    @FXML
    public void createMessage(UMLMessage message)
    {
        int msgCount = sequenceDiagram.returnMessagesCount();
        int sendIndex = sequenceDiagram.instances.indexOf(message.sender);
        int recIndex = sequenceDiagram.instances.indexOf(message.receiver);
        Label messageLabel = new Label();

        //System.out.println("sendIndex: " + sendIndex + "recIndex: " + recIndex);

        GridPane.setValignment(messageLabel, VPos.TOP);
        GridPane.setHalignment(messageLabel, HPos.CENTER);

        messageLabel.setOnMouseClicked((MouseEvent event) -> 
        {
            messageLabel.requestFocus();
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
                // System.out.println("Mouse clicked cell: " + colIndex + " And: " + rowIndex);

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
                                if (sequenceDiagram.messages.get(rowFocus).transmition == 2)
                                {
                                    colFocus = sequenceDiagram.getIndexOfInstace(sequenceDiagram.messages.get(rowFocus).receiver);
                                    removeObj();
                                }

                                for (int j = 0; j < sequenceDiagram.instances.size(); j++)
                                {
                                    for (int i = 0; i < sequenceDiagram.instances.get(j).activations.size(); i++)
                                    {
                                        System.out.println("colFocus: " + colFocus + " i: " + i);
                                        if (sequenceDiagram.instances.get(j).activations.get(i).getDeactivation() == true)
                                        {
                                            if (sequenceDiagram.instances.get(j).activations.get(i).isInBounds(rowFocus))
                                            {
                                                sequenceDiagram.instances.get(j).activations.get(i).setEnd(sequenceDiagram.instances.get(j).activations.get(i).getEnd() - 1);
                                            }
                                        }
                                        else
                                        {
                                            if (sequenceDiagram.instances.get(j).activations.get(i).isInBounds(rowFocus))
                                            {
                                                sequenceDiagram.instances.get(j).activations.get(i).setEnd(sequenceDiagram.instances.get(j).activations.get(i).getEnd() - 1);
                                                if (sequenceDiagram.instances.get(j).activations.get(i).getEnd() < sequenceDiagram.instances.get(j).activations.get(i).getStart())
                                                {
                                                    sequenceDiagram.instances.get(j).activations.remove(i);
                                                }
                                            }
                                            else if (sequenceDiagram.instances.get(j).activations.get(i).getStart() > rowFocus)
                                            {
                                                sequenceDiagram.instances.get(j).activations.get(i).setEnd(sequenceDiagram.instances.get(j).activations.get(i).getEnd() - 1);
                                                sequenceDiagram.instances.get(j).activations.get(i).setStart(sequenceDiagram.instances.get(j).activations.get(i).getStart() - 1);
                                            }
                                        }
                                    }
                                }

                                removeRow(seqGridMsgs, rowFocus);

                                sequenceDiagram.messages.remove(rowFocus);
                                updateGrids();
                                createSnapshot(sequenceDiagram);
                            }
                        }
                    });
                }
                else
                {
                    // System.out.println("Textfield out focus");
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
            messageLabel.setText(message.operation.getName() + "(" + message.message +")");
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
            // System.out.println("Message order: " + message.order);
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
                    if (message.transmition == 3)
                    {
                        newLine.getStrokeDashArray().addAll(25d, 10d);
                    }
                    seqGridMsgs.add(newLine, i, msgCount);
                    GridPane.setValignment(newLine, VPos.CENTER); 
                }
            }

            if (message.transmition != 2)
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
                    if (message.transmition == 3)
                    {
                        newLine.getStrokeDashArray().addAll(25d, 10d);
                    }
                    seqGridMsgs.add(newLine, i, msgCount);
                    GridPane.setValignment(newLine, VPos.CENTER); 
                }
            }

            if (message.transmition != 2)
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

        seqGridAct.getRowConstraints().add(new RowConstraints(30));
        createSnapshot(sequenceDiagram);
    }

    @FXML
    public void initGrid()
    {
        seqGrid.setMaxWidth(1220);
        seqGrid.setAlignment(Pos.CENTER);
        seqGrid.setPickOnBounds(false);
        seqObjBox.getChildren().add(seqGrid);
    }

    @FXML
    public void initGridMsg()
    {
        seqGridMsgs.setMaxWidth(1220);
        seqGridMsgs.setAlignment(Pos.TOP_CENTER);
        //seqGridMsgs.setGridLinesVisible(true);
        seqGridMsgs.setPickOnBounds(false);

        msgWidth = seqEditorBox.getWidth() / 2;

        ColumnConstraints columnSpacer = new ColumnConstraints(msgWidth / 2);
        ColumnConstraints column = new ColumnConstraints(msgWidth);
        seqGridMsgs.getColumnConstraints().addAll(columnSpacer, column, columnSpacer);
        seqMsgBox.getChildren().add(seqGridMsgs);
        seqMsgBox.setPickOnBounds(false);
        msgRegion.setPickOnBounds(false);
    }

    @FXML
    public void initGridAct()
    {
        seqGridAct.setMaxWidth(1220);
        seqGridAct.setAlignment(Pos.TOP_CENTER);
        //seqGridAct.setGridLinesVisible(true);
        seqGridAct.setPickOnBounds(false);
        seqActBox.getChildren().add(seqGridAct);
        seqActBox.setPickOnBounds(false);
        actRegion.setPickOnBounds(false);
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

        seqActBox.getChildren().remove(seqGridAct);
        seqGridAct = new GridPane();

        displaySequence(sequenceDiagram, false);
        checkIncon();
    }
}
