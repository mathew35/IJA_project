package project;

import java.util.ResourceBundle;
import java.net.URL;
import javafx.fxml.*;
import javafx.scene.control.*;

import uml.*;

public class ClassDiagramController implements Initializable{
    @FXML
    private TreeView<String> ClassTree;

    @FXML
    private TextField ClassName;

    
    public void SelectClass() {
    }
    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1){
        ClassDiagram cd = new ClassDiagram("Diagram");
        TreeItem<String> rootItem = new TreeItem<>(cd.getName());
        ClassTree.setShowRoot(false);
        ClassTree.setRoot(rootItem);
        ClassName.setPromptText("Class Name");
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
}
