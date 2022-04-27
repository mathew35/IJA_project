/**
 * Řadič, který řeší přiřazení a logiku metod k prvků UI ve scéně UML editoru.
 *
 * @author Adam Bazel (xbazel00)
 * @author Matůš Vráblik (xvrabl05)
 * @since  2022-04-12
 */

package project;

import java.util.ResourceBundle;
import java.net.URL;
import javafx.fxml.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import uml.*;

/**
 * Řadič, který řeší přiřazení a logiku metod k prvků UI ve scéně UML editoru.
 */
public class EditorController implements Initializable{
    @FXML
    private TreeView<String> ClassTree;

    @FXML
    private TextField ClassName;

    @FXML
    private TabPane tabPane;

    @FXML
    private ListView<String> seqenceObjects;

    @FXML
    private HBox seqDisplay;

    String[] seqObjList = {"ATM", "Bank", "Database"};
    
    /**
     * Po načtení scény provede prvně tyto úkony pro správné zobrazení a pracování aplikace.
     *
     * @param arg0 TODO.
     * @param arg1 TODO.
     */
    @Override
    public void initialize(URL arg0, ResourceBundle arg1){
        ClassDiagram cd = new ClassDiagram("Diagram");
        TreeItem<String> rootItem = new TreeItem<>(cd.getName());
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
        seqDisplay.setPadding(new Insets(10,10,10,10));
        Text tf[]=new Text[2];
        Line lines[] = new Line[tf.length];
        VBox seqYAxis[] = new VBox[tf.length];

        int messagesCount[] = {1, 0};
        // Add first spacer
        seqDisplay.getChildren().add(createSpacer());

        for(int i=0;i<tf.length;i++)
        {
            seqYAxis[i] = new VBox();
            seqYAxis[i].setAlignment(Pos.CENTER);
            seqDisplay.getChildren().add(seqYAxis[i]);
            tf[i] = new Text("Display Line "+ seqDisplay.getHeight());
            //seqDisplay.getChildren().add(tf[i]);
            if (messagesCount[i] == 0)
            {
                lines[i] = new Line(0, 0, 0, seqDisplay.getHeight() * 0.9);
                seqYAxis[i].getChildren().addAll(tf[i], lines[i]);
            }
            else
            {
                lines[i] = new Line(0, 0, 0, seqDisplay.getHeight() * 0.9 / (2 * messagesCount[i]));
                Line active = new Line(0, 0, 0, seqDisplay.getHeight() * 0.9 / (2 * messagesCount[i]));
                active.setStrokeWidth(5.0);
                seqYAxis[i].getChildren().addAll(tf[i], lines[i], active);
            }
            
            //lines[i].getStrokeDashArray().addAll(25d, 10d);
            //seqDisplay.getChildren().add(seqYAxis[i]);
            // Add a spacer after the label
            seqDisplay.getChildren().add(createSpacer());
        }
        seqDisplay.setAlignment(Pos.CENTER_RIGHT);
    }
}
