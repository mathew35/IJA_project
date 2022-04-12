package project;

import java.util.ResourceBundle;
import java.net.URL;
import javafx.fxml.*;
import javafx.scene.control.*;

import uml.*;

public class EditorController implements Initializable{
    @FXML
    private TreeView<String> ClassTree;

    @FXML
    private TextField ClassName;

    @FXML
    private TabPane tabPane;

    
    public void SelectClass() {
    }
    
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

    public void selectItem(){
        TreeItem<String> item = ClassTree.getSelectionModel().getSelectedItem();
        if (item != null){
            ClassName.setText(item.getValue());
        }
    }
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

    @FXML
    private void addTab() {
        int numTabs = tabPane.getTabs().size();
        Tab tab = new Tab("Sequence Diagram "+(numTabs-1));

        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().selectLast();

    }
}
