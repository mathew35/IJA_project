/**
 * Incializace celé aplikace a zajišťuje spuštění JavaFX s kombinací souborů typu FXML.
 *
 * @author Adam Bazel (xbazel00)
 * @author Matůš Vráblik (xvrabl05)
 * @since  2022-04-12
 */

package project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.awt.*;
import java.io.*;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.*;

import uml.*;

/**
 * Incializace celé aplikace a zajišťuje spuštění JavaFX s kombinací souborů typu FXML.
 */
public class Main extends Application{

    /**
     * Metoda, která načítá z JSON souboru diagram tříd do aplikace.
     *
     * @param objectMapper Objekt třídy, která poskytuje funkce pro čtení a zápis JSON.
     */
    static ClassDiagram loadDiagram(ObjectMapper objectMapper)
    {
        try {
            ClassDiagram diagram = objectMapper.readValue(new File("target/diagram.json"), ClassDiagram.class);
    
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
     * Metoda, která načítá z JSON souboru sekvenční diagram do aplikace.
     *
     * @param objectMapper Objekt třídy, která poskytuje funkce pro čtení a zápis JSON.
     */
    static SequenceDiagram loadSequence(ObjectMapper objectMapper)
    {
        try {
            SequenceDiagram diagram = objectMapper.readValue(new File("target/sequence.json"), SequenceDiagram.class);
    
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
    static void exportDiagram(ObjectMapper objectMapper, ClassDiagram classdiagram)
    {
        try {
            objectMapper.writeValue(new File("target/diagram.json"), classdiagram);
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
    static void exportSequence(ObjectMapper objectMapper, SequenceDiagram seqdiagram)
    {
        try {
            objectMapper.writeValue(new File("target/sequence.json"), seqdiagram);
        } catch (StreamWriteException e) {
            e.printStackTrace();
        } catch (DatabindException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Načtení FXML souboru do scény a určení scény na "jeviště".
     *
     * @param stage kontejner JavaFX nejvyšší úrovně, který zobrazuje scénu.
     */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getClassLoader().getResource("menu.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2, Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2);
        stage.setTitle("UML Diagram Editor");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Inicializace aplikace.
     *
     * @param args Argumenty.
     */
    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();

        /*ClassDiagram d = new ClassDiagram("My model");
        UMLClass cls = d.createClass("C1");
        
        UMLClass cls2 = d.createClass("C2");                
        UMLAttribute attr1 = new UMLAttribute("count", d.classifierForName("int"));
        UMLAttribute attr2 = new UMLAttribute("id", d.classifierForName("certain"));
        UMLAttribute attr3 = new UMLAttribute("c", d.classifierForName("yeah"));

        UMLAttribute attr4 = new UMLAttribute("sucakge", d.classifierForName("string"));
        UMLAttribute attr5 = new UMLAttribute("ye", d.classifierForName("volume"));
        UMLAttribute attr6 = new UMLAttribute("insanity", d.classifierForName("sinsiter"));

        cls.addAttribute(attr1);
        cls.addAttribute(attr2);
        cls.addAttribute(attr3);

        cls2.addAttribute(attr6);
        cls2.addAttribute(attr4);
        cls2.addAttribute(attr5);

        loadDiagram(objectMapper);
        
        exportDiagram(objectMapper, d);*/

        /*SequenceDiagram diagram = new SequenceDiagram("Sequence");
        UMLClass cls = diagram.createClass("ATM");
        UMLClass cls2 = diagram.createClass("Bank");
        UMLClass cls3 = diagram.createClass("Database");

        UMLClassifier cls4 = UMLClassifier.forName("int");

        diagram.createMessage("msg1", cls4, cls, cls2, true);
        diagram.createMessage("msg2", cls4, cls, cls3, true);
        diagram.createMessage("msg3", cls4, cls2, cls, true);

        exportSequence(objectMapper, diagram);

        loadSequence(objectMapper);*/

        launch();
    }
}