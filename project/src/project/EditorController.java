package project;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class EditorController {
    @FXML
    private TabPane tabPane;

    @FXML
    public void initialize() {
        Tab deleted = tabPane.getSelectionModel().getSelectedItem();
        tabPane.getTabs().remove(deleted);
        tabPane.getSelectionModel().selectLast();    
    }
    
    @FXML
    private void addTab() {
        int numTabs = tabPane.getTabs().size();
        Tab tab = new Tab("Sequence Diagram "+(numTabs-1));

        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().selectLast();

    }
}
