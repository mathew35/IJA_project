package project;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.fxml.*;
import javafx.scene.control.MenuItem;
import javafx.stage.Window;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import uml.*;
public class MenuBarController {
    ClassDiagram classDiagram = new ClassDiagram("genereted");
    @FXML
    private MenuItem MenuSave;
    public void onMenuSaveClick(){
        SaveDiagram(classDiagram);
    }

    public void SaveDiagram(ClassDiagram diag){
        Window stage = Stage.getWindows().stream().filter(Window::isShowing).findFirst().orElse(null);
        FileChooser fileChooser = new FileChooser();
 
        //Set extension filter for text files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.writeValue(file, diag);
            } catch (StreamWriteException e) {
                e.printStackTrace();
            } catch (DatabindException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
    }
    
}