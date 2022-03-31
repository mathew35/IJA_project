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

public class Main extends Application{

    static ClassDiagram loadDiagram(ObjectMapper objectMapper)
    {
        try {
            ClassDiagram diagram = objectMapper.readValue(new File("target/diagram.json"), ClassDiagram.class);
    
            exportDiagram(objectMapper, diagram);
    
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
    
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getClassLoader().getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2, Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2);
        stage.setTitle("Projekt - UML diagram handler");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        //objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        /*PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
        .allowIfSubType("com.baeldung.jackson.inheritance")
        .allowIfSubType("java.util.ArrayList")
        .build();

        objectMapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL);*/

        ClassDiagram d = new ClassDiagram("My model");
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
        
        exportDiagram(objectMapper, d);
        launch();
    }
}