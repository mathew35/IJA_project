  
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

import org.w3c.dom.NameList;

import java.net.URL;
import javafx.fxml.*;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Pos;

import uml.*;

/**
 * Řadič, který řeší přiřazení a logiku metod k prvků UI ve scéně UML editoru.
 */
public class EditorController implements Initializable
{
    ClassDiagram classDiagram = new ClassDiagram();
    ArrayList<SequenceDiagram> sequenceDiagrams = new ArrayList<SequenceDiagram>();
    GridPane seqGrid = new GridPane();

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
    private Button addMessageButton;

    @FXML
    private HBox seqEditorBox;

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

    private Node createHSpacer() {
        final Region spacer = new Region();
        // Make it always grow or shrink according to the available space
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    private Node createVSpacer() {
        final Region spacer = new Region();
        // Make it always grow or shrink according to the available space
        VBox.setVgrow(spacer, Priority.ALWAYS);
        return spacer;
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
            initGrid();
        };

        int objsCount = sequenceDiagrams.get(tabPane.getSelectionModel().getSelectedIndex()-2).getNameClasses().size();
        TextField objField = new TextField();
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
                    seqGrid.getChildren().remove(objField);
                    seqGrid.add(objName, objsCount, 0);

                    addSeqObjButton.setDisable(false);
                }
            }
        });

        objField.focusedProperty().addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
            {
                if (!newPropertyValue)
                {
                    if (objField.getText() == "" && objsCount != 0)
                    {
                        seqGrid.getChildren().remove(objField);
                        seqGrid.getColumnConstraints().remove(objsCount);
                    }
                    /*else
                    {
                        sequenceDiagrams.get(tabPane.getSelectionModel().getSelectedIndex()-2).createClass(objField.getText());

                        Text objName = new Text(objField.getText());
                        seqGrid.getChildren().remove(objField);
                        seqGrid.add(objName, objsCount, 0);
                    }*/
                    addSeqObjButton.setDisable(false);
                }
            }
        });

        seqGrid.getColumnConstraints().add(new ColumnConstraints(-1, -1, -1, Priority.ALWAYS, HPos.CENTER, false));
        if (objsCount == 0)
        {
            seqGrid.getRowConstraints().add(new RowConstraints(-1, -1, -1, Priority.ALWAYS, VPos.CENTER, false));
            seqGrid.getRowConstraints().add(new RowConstraints(-1, -1, -1, Priority.ALWAYS, VPos.CENTER, false));
        }
        
        seqGrid.add(objField, objsCount, 0);
        objField.requestFocus();
        seqGrid.add(new Line(0, 0, 0, seqEditorBox.getHeight() * 0.9), objsCount, 1);
    }

    public void clickGrid(javafx.scene.input.MouseEvent event) 
    {
        Node clickedNode = event.getPickResult().getIntersectedNode();
        if (clickedNode != seqGrid) {
            // click on descendant node
            Node parent = clickedNode.getParent();
            while (parent != seqGrid) {
                clickedNode = parent;
                parent = clickedNode.getParent();
            }
            Integer colIndex = GridPane.getColumnIndex(clickedNode);
            Integer rowIndex = GridPane.getRowIndex(clickedNode);
            System.out.println("Mouse clicked cell: " + colIndex + " And: " + rowIndex);
        }
    }

    @FXML
    public void addMessage()
    {

    }
    /*@FXML
    public void addMessage()
    {
        VBox msgBox = new VBox();
        msgBox.setAlignment(Pos.CENTER);
        Node nodeSender = seqDisplay.getChildren().get(1);
        Node nodeSpacer = seqDisplay.getChildren().get(2);
        Node nodeReciever = seqDisplay.getChildren().get(3);
        double start_width = 0;

        start_width = ((Region)nodeSpacer).getWidth() + ((((VBox)nodeSender).getWidth() + ((VBox)nodeReciever).getWidth()) / 2);

        if (seqDisplayLines.getChildren().size() == 0)
        {
            seqDisplayLines.getChildren().add(createHSpacer());
        }

        //seqDisplayLines.getChildren().addAll(debug, horLine);

        Text debug2 = new Text("" + start_width + " | " + ((VBox)nodeSender).getWidth() + " | " + ((VBox)nodeReciever).getWidth());
        Line horLine2 = new Line(1, 0, start_width, 0);
        msgBox.getChildren().addAll(debug2,horLine2);
        seqDisplayLines.getChildren().addAll(msgBox, createHSpacer());
    }*/

    @FXML
    public void displaySequence(SequenceDiagram diagram)
    {
        if (addSeqObjFirst != null)
        {
            initGrid();
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
    public void initGrid()
    {
        seqEditorBox.getChildren().remove(addSeqObjFirst);
        addSeqObjFirst = null;
        seqGrid.setMinWidth(900);
        seqGrid.setMinHeight(460);
        seqGrid.setAlignment(Pos.CENTER);
        seqGrid.setGridLinesVisible(true);
        seqEditorBox.getChildren().add(seqGrid);
    }
}
