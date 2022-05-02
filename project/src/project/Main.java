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
import uml.*;

import java.awt.*;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Incializace celé aplikace a zajišťuje spuštění JavaFX s kombinací souborů typu FXML.
 */
public class Main extends Application{    
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
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Inicializace aplikace.
     *
     * @param args Argumenty.
     */
    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();

        MenuController menu = new MenuController();

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

        //menu.loadClass(objectMapper, null);
        
        menu.exportClass(objectMapper, d);*/

        /*SequenceDiagram diagram = new SequenceDiagram("Sequence");
        UMLClass cls = diagram.createClass("ATM");
        UMLClass cls2 = diagram.createClass("Bank");
        UMLClass cls3 = diagram.createClass("Database");
        UMLOperation op1 = UMLOperation.create("method1", diagram.classifierForName("void"));
        diagram.createMessage(op1, cls, cls2, true);
        diagram.createMessage(op1, cls, cls3, true);
        diagram.createMessage(op1, cls2, cls, true);
        menu.exportSequence(objectMapper, diagram);
        //loadSequence(objectMapper);*/

        launch();
    }
}