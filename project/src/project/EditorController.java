  
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
import java.net.URL;
import javafx.fxml.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.event.EventHandler;

import uml.*;

/**
 * Řadič, který řeší přiřazení a logiku metod k prvků UI ve scéně UML editoru.
 */
public class EditorController implements Initializable
{
    ClassDiagram classDiagram = new ClassDiagram();
    ArrayList<SequenceDiagram> sequenceDiagrams = new ArrayList<SequenceDiagram>();

    @FXML
    private TreeView<String> ClassTree;

    @FXML
    private TextField ClassName;

    @FXML
    private TabPane tabPane;

    @FXML
    private HBox seqDisplay;

    @FXML
    private VBox addSeqObjFirst;

    @FXML
    private Button addSeqObjButton;
    
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

    private Node createSpacer() {
        final Region spacer = new Region();
        // Make it always grow or shrink according to the available space
        HBox.setHgrow(spacer, Priority.ALWAYS);
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
            seqDisplay.getChildren().remove(addSeqObjFirst);
        }

        Line line = new Line(0, 0, 0, seqDisplay.getHeight() * 0.9);
        TextField objField = new TextField();
        VBox objBox = new VBox();

        addSeqObjButton.setDisable(true);

        objField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) 
                {
                    sequenceDiagrams.get(tabPane.getSelectionModel().getSelectedIndex()-2).createClass(objField.getText());

                    Text objName = new Text(objField.getText() + sequenceDiagrams.get(0).getNameClasses());
                    objBox.getChildren().remove(objField);
                    objBox.getChildren().add(0, objName);

                    addSeqObjButton.setDisable(false);
                }
            }
        });

        objBox.setAlignment(Pos.CENTER);

        if (seqDisplay.getChildren().size() == 0)
        {
            seqDisplay.getChildren().add(createSpacer());
        }

        seqDisplay.getChildren().add(objBox);
        objBox.getChildren().addAll(objField, line);
        seqDisplay.getChildren().add(createSpacer());
    }

    @FXML
    public void displaySequence(SequenceDiagram diagram)
    {
        if (addSeqObjFirst != null)
        {
            seqDisplay.getChildren().remove(addSeqObjFirst);
        }

        ArrayList<String> nameList = new ArrayList<String>();
        ArrayList<Text> textList = new ArrayList<Text>();

        nameList = diagram.getNameClasses();

        //int messagesCount[] = {1, 0};
        // Add first spacer
        seqDisplay.getChildren().add(createSpacer());

        Line lines[] = new Line[nameList.size()];
        VBox seqYAxis[] = new VBox[nameList.size()];

        for(int i=0;i<nameList.size();i++)
        {
            seqYAxis[i] = new VBox();
            seqYAxis[i].setAlignment(Pos.CENTER);
            seqDisplay.getChildren().add(seqYAxis[i]);

            //seqDisplay.getHeight()
            textList.add(new Text(nameList.get(i)));
            /*if (messagesCount[i] == 0)
            {
                lines[i] = new Line(0, 0, 0, seqDisplay.getHeight() * 0.9);
                seqYAxis[i].getChildren().addAll(textList.get(i), lines[i]);
            }
            else
            {
                lines[i] = new Line(0, 0, 0, seqDisplay.getHeight() * 0.9 / (2 * messagesCount[i]));
                Line active = new Line(0, 0, 0, seqDisplay.getHeight() * 0.9 / (2 * messagesCount[i]));
                active.setStrokeWidth(5.0);
                seqYAxis[i].getChildren().addAll(textList.get(i), lines[i], active);
            }*/
            
            lines[i] = new Line(0, 0, 0, seqDisplay.getHeight() * 0.9);
            seqYAxis[i].getChildren().addAll(textList.get(i), lines[i]);
            
            //lines[i].getStrokeDashArray().addAll(25d, 10d);
            seqDisplay.getChildren().add(createSpacer());
        }
        seqDisplay.setAlignment(Pos.CENTER_RIGHT);
    }
}
