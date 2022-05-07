/**
 * Řadič, který řeší přiřazení a logiku metod k prvků UI v úvodní scéně aplikace.
 *
 * @author Adam Bazel (xbazel00)
 * @since  2022-04-12
 */

package project;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import uml.ClassDiagram;
import uml.SequenceDiagram;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;


/**
 * Řadič, který je vytvořen pomocí FXML a používá se k inicializaci prvků uživatelského rozhraní v úvodní scéně aplikace.
 */
public class MenuController {
    private Stage stage;
    private Scene scene;

    /**
     * Metoda, která přepíná "jeviště" na editorskou scénu.
     *
     * @param event Představující typ akce, v tomto případě změna scény.
     */
    public void switchToEditor(ActionEvent event) throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getClassLoader().getResource("class.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(fxmlLoader.load());

        EditorController editor = fxmlLoader.getController();
        editor.selectTab(1);

        stage.setScene(scene);
        stage.show();
    }

    public void openClass(ActionEvent event) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getClassLoader().getResource("class.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(fxmlLoader.load());

        FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser,"Class");
        File file = fileChooser.showOpenDialog(stage);
        if(file != null)
        {
            ObjectMapper objectMapper = new ObjectMapper();
            EditorController editor = fxmlLoader.getController();

            editor.setClassDiagram(loadClass(objectMapper, file));

            editor.selectTab(1);

            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        }
    }

    public void openSequence(ActionEvent event) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getClassLoader().getResource("class.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(fxmlLoader.load());

        FileChooser fileChooser = new FileChooser();

        configureFileChooser(fileChooser,"Sequence");
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) 
        {
            ObjectMapper objectMapper = new ObjectMapper();
            EditorController editor = fxmlLoader.getController();

            editor.sequenceDiagrams.set(0, loadSequence(objectMapper, file));

            editor.selectTab(2);
    
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

            // After show otherwise bad values for lines
            editor.displaySequence(loadSequence(objectMapper, file));
        }
    }

    private static void configureFileChooser(final FileChooser fileChooser, String type) 
    {      
        fileChooser.setTitle("Open " + type + " File...");                
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON", "*.json"));
    }

        /**
     * Metoda, která načítá z JSON souboru sekvenční diagram do aplikace.
     *
     * @param objectMapper Objekt třídy, která poskytuje funkce pro čtení a zápis JSON.
     */
    SequenceDiagram loadSequence(ObjectMapper objectMapper, File file)
    {
        try {
            //SequenceDiagram diagram = objectMapper.readValue(new File("data/sequence.json"), SequenceDiagram.class);
            SequenceDiagram diagram = objectMapper.readValue(file, SequenceDiagram.class);
    
            return diagram;
        } catch (StreamReadException e) {
            e.printStackTrace();
        } catch (DatabindException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Metoda, která načítá z JSON souboru diagram tříd do aplikace.
     *
     * @param objectMapper Objekt třídy, která poskytuje funkce pro čtení a zápis JSON.
     */
    static ClassDiagram loadClass(ObjectMapper objectMapper, File file)
    {
        try {
            ClassDiagram diagram = objectMapper.readValue(file, ClassDiagram.class);
            return diagram;
        } catch (StreamReadException e) {
            e.printStackTrace();
        } catch (DatabindException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Metoda, která exportuje diagram tříd v aplikaci do souboru.
     *
     * @param objectMapper Objekt třídy, která poskytuje funkce pro čtení a zápis JSON.
     */
    void exportClass(ObjectMapper objectMapper, ClassDiagram classdiagram)
    {
        try {
            objectMapper.writeValue(new File("data/diagram.json"), classdiagram);
        } catch (StreamWriteException e) {
            e.printStackTrace();
        } catch (DatabindException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoda, která exportuje diagram tříd v aplikaci do souboru.
     *
     * @param objectMapper Objekt třídy, která poskytuje funkce pro čtení a zápis JSON.
     */
    void exportSequence(ObjectMapper objectMapper, SequenceDiagram seqdiagram)
    {
        try {
            objectMapper.writeValue(new File("data/sequence.json"), seqdiagram);
        } catch (StreamWriteException e) {
            e.printStackTrace();
        } catch (DatabindException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}