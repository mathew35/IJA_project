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
import javafx.scene.control.*;

import uml.*;

/**
 * Objekt řadiče, který je vytvořen pomocí FXML a používá se k inicializaci prvků uživatelského rozhraní v UML Editoru.
 */
public class EditorController implements Initializable{
    @FXML
    private TreeView<String> ClassTree;

    @FXML
    private TextField ClassName;

    @FXML
    private TabPane tabPane;

    
    public void SelectClass() {
    }
    
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
        tabPane.getSelectionModel().selectLast(); 
    }

    /**
    * TODO
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
}
