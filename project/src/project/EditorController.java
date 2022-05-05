  
/**
 * Řadič, který řeší přiřazení a logiku metod k prvků UI ve scéně UML editoru.
 *
 * @author Adam Bazel (xbazel00)
 * @author Matůš Vráblik (xvrabl05)
 * @since  2022-04-12
 */

package project;

import java.util.ArrayList;
import java.util.ResourceBundle;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;

import uml.*;

/**
 * Řadič, který řeší přiřazení a logiku metod k prvků UI ve scéně UML editoru.
 */
public class EditorController implements Initializable
{
    ClassDiagram classDiagram = new ClassDiagram();
    ArrayList<SequenceDiagram> sequenceDiagrams = new ArrayList<SequenceDiagram>();
    GridPane seqGrid = new GridPane();
    GridPane seqGridMsgs = new GridPane();
    double msgWidth;

    @FXML
    private TreeView<String> ClassTree;

    @FXML
    private TextField ClassName;

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

    /**
     * Po načtení scény provede prvně tyto úkony pro správné zobrazení a pracování aplikace.
     *
     * @param arg0 TODO.
     * @param arg1 TODO.
     */
    @Override
    public void initialize(URL arg0, ResourceBundle arg1){
        //ClassDiagram cd = new ClassDiagram("Diagram");
        TreeItem<String> rootItem = new TreeItem<>(classDiagram.getName());
        ClassTree.setShowRoot(false);
        ClassTree.setRoot(rootItem);
        ClassName.setPromptText("Class Name");

        Tab deleted = tabPane.getSelectionModel().getSelectedItem();
        tabPane.getTabs().remove(deleted);
    }

    /**
    * Detekuje selekci třídy v ClassDiagram tabu a zobrazí informace o této tříde
    */
    public void selectItem(){
        TreeItem<String> item = ClassTree.getSelectionModel().getSelectedItem();
        if (item != null){
            ClassName.setText(item.getValue());
        }
    }
    /**
     * TODO
     */
    public void SelectClass() {
    }

    /**
    * Přidání třídy do diagramu tříd
    */
    public void onAddClassClick(){
        TreeItem<String> rootItem = ClassTree.getRoot();
        String text = ClassName.getText();
        if (text.isEmpty()) {
            ClassName.setPromptText("Zadaj nazov novej triedy");
            ClassName.setStyle("-fx-prompt-text-fill: red");
            return;
        }
        rootItem.getChildren().add(new TreeItem<>(text));
        ClassName.setText("");
    }
    /**
    * Přidání podtřídy ke vybrané tříde v diagramu tříd
    */
    public void onAddSubclassClick(){}

    /**
    * Přidání karty do panelu karet 
    */
    @FXML
    private void addTab() {
        int numTabs = tabPane.getTabs().size();
        Tab tab = new Tab("Sequence Diagram "+(numTabs-1));

        sequenceDiagrams.add(new SequenceDiagram());

        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().selectLast();
    }

    @FXML
    public void selectTab(int tabIndex)
    {
        tabPane.getSelectionModel().select(tabIndex);
    }

    @FXML
    private void addSeqObj()
    {
        if (addSeqObjFirst != null)
        {
            seqEditorBox.getChildren().remove(addSeqObjFirst);
            addSeqObjFirst = null;

            initGridState();
            initGrid();
        };

        int colCount = sequenceDiagrams.get(tabPane.getSelectionModel().getSelectedIndex()-2).getNameClasses().size();
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
                    sequenceDiagrams.get(tabPane.getSelectionModel().getSelectedIndex()-2).createClass(objField.getText());

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

    /*@FXML
    public void addMessage()
    {
        seqGridMsgs.getRowConstraints().add(new RowConstraints(20));
        /*Arrow arrow = new Arrow();
        arrow.setStartX(0);
        arrow.setStartY(0);
        arrow.setEndX(seqEditorBox.getWidth() / 2);
        arrow.setEndY(0);
        seqGridMsgs.add(arrow, 1, seqGridMsgs.getRowCount());
    }*/

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
        msgController.loadData(sequenceDiagrams.get(tabPane.getSelectionModel().getSelectedIndex()-2));

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
            initGrid();
            initGridState();
        }

        ArrayList<String> nameList = new ArrayList<String>();

        nameList = diagram.getNameClasses();

        for (int i = 0; i < nameList.size(); i++)
        {
            seqGrid.getColumnConstraints().add(new ColumnConstraints(-1, -1, -1, Priority.ALWAYS, HPos.CENTER, false));
            if (i == 0)
            {
                seqGrid.getRowConstraints().add(new RowConstraints(-1, -1, -1, Priority.ALWAYS, VPos.CENTER, false));
                seqGrid.getRowConstraints().add(new RowConstraints(-1, -1, -1, Priority.ALWAYS, VPos.CENTER, false));
            }
        
            seqGrid.add(new Text(nameList.get(i)), i, 0);
            seqGrid.add(new Line(0, 0, 0, seqEditorBox.getHeight() * 0.9), i, 1);
        }
    }

    @FXML
    public void createMessage(UMLMessage message)
    {
        int msgCount = sequenceDiagrams.get(tabPane.getSelectionModel().getSelectedIndex()-2).returnMessagesCount();
        int sendIndex = sequenceDiagrams.get(tabPane.getSelectionModel().getSelectedIndex()-2).getClasses().indexOf(message.sender);
        int recIndex = sequenceDiagrams.get(tabPane.getSelectionModel().getSelectedIndex()-2).getClasses().indexOf(message.receiver);

        seqGridMsgs.getRowConstraints().add(new RowConstraints());
       
        UMLArrow arrow = new UMLArrow();
        arrow.setStartX(0);
        arrow.setStartY(0);

        double arrowWidth = seqEditorBox.getWidth() / seqGrid.getColumnCount();
        
        if (sendIndex > recIndex)
        {
            arrowWidth = -arrowWidth;
        }

        arrow.setEndX(arrowWidth);
        arrow.setEndY(0);

        if (sendIndex < recIndex)
        {
            if (Math.abs(sendIndex - recIndex) != 1)
            {
                for (int i = sendIndex + 1; i < recIndex; i++)
                {
                    Line newLine = new Line(0, 0, seqEditorBox.getWidth() / seqGrid.getColumnCount(), 0);
                    seqGridMsgs.add(newLine, i, msgCount);
                    GridPane.setValignment(newLine, VPos.CENTER); 
                }
            }
            seqGridMsgs.add(arrow, recIndex, msgCount);
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
            seqGridMsgs.add(arrow, recIndex + 1, msgCount);
        }

        sequenceDiagrams.get(tabPane.getSelectionModel().getSelectedIndex()-2).addMessage(message);
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
        RowConstraints row = new RowConstraints(20);
        seqGridMsgs.getRowConstraints().add(row);
        seqGridMsgs.getColumnConstraints().addAll(columnSpacer, column, columnSpacer);
        seqMsgBox.getChildren().add(seqGridMsgs);
    }
}