package project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.fxml.*;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Button;
import javafx.stage.Window;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import com.fasterxml.jackson.databind.ObjectMapper;
import uml.*;
public class MenuBarController {
    ClassDiagram classDiagram = new ClassDiagram("genereted");
    ArrayList<ClassDiagram> snapshots = new ArrayList<ClassDiagram>();
    int snapshotPos = -1;

    @FXML
    private MenuItem MenuSave;

    @FXML
    private Button RedoButton;

    @FXML
    private Button UndoButton;

    public void onMenuSaveClick(){
        SaveDiagram(classDiagram);
    }

    public void onUndoClick(){
        if(snapshotPos > 0){
            snapshotPos--;
            classDiagram = snapshots.get(snapshotPos).deepCopy();
            refresh();
        }
        System.out.print(snapshots.size());
        System.out.print(snapshotPos);
        System.out.println(classDiagram);
        System.out.println("undo");
    }

    public void onRedoClick(){
        if(snapshots.size()>snapshotPos+1){
            snapshotPos++;
            classDiagram = snapshots.get(snapshotPos).deepCopy();
            refresh();
        }
        System.out.print(snapshots.size());
        System.out.print(snapshotPos);
        System.out.println(classDiagram);
        System.out.println("redo");
    }
    public void createSnapshot(ClassDiagram diag){
        System.out.print(snapshots.size());
        System.out.print(snapshotPos);
        System.out.println(classDiagram);
        System.out.println("createsnapshot");
        while(snapshots.size()>snapshotPos+1){
            snapshots.remove(snapshotPos+1);
        }
        snapshotPos++;
        snapshots.add(diag.deepCopy());
        System.out.println(diag.getClasses().get(0).getAttributes());
        System.out.println(diag.deepCopy().getClasses().get(0).getAttributes());
        System.out.println("diag test");
    }
    public void refresh(){
        //to be implemented inside controllers
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