/**
 * Třída reprezentuje sekvenční diagram. Je odvozen od třídy Element (má název). Obsahuje seznam tříd (instance třídy UMLClass) a seznam zpráv (instance třídy UMLMessage).
 *
 * @author Adam Bazel (xbazel00)
 * @since  2022-04-12
 */

package uml;

import java.util.*;
import com.fasterxml.jackson.annotation.*;

/**
 * Třída reprezentuje sekvenční diagram. Je odvozen od třídy Element (má název). Obsahuje seznam tříd (instance třídy UMLClass) a seznam zpráv (instance třídy UMLMessage).
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")
public class SequenceDiagram extends Element{
    @JsonProperty("classes")
    private List<UMLClass> classes = new ArrayList<UMLClass>();
    @JsonProperty("messages")
    private List<UMLMessage> messages = new ArrayList<UMLMessage>();
        
    public SequenceDiagram(){}

    /**
     * Konstruktor pro vytvoření instance diagramu.
     * @param name Název vytvářené třídy.
     */
    public SequenceDiagram(String name){
        super(name);
    }

    /**
     * Vytvoří instanci UML zprávy a vloží ji do diagramu.
     * 
     * @param name Název vytvářené třídy.
     * @param type Typ atributu.
     * @param sender Odesílatel zprávy.
     * @param receiver Příjemce zprávy.
     * @param transmition Typ přenosu (synchroní, asynchroní).
     */
    public UMLMessage createMessage(String name, UMLClassifier type, UMLClass sender, UMLClass receiver, boolean transmition)
    {
        UMLMessage newMessage = new UMLMessage(name, type, sender, receiver, transmition);
        this.messages.add(newMessage);

        return newMessage;
    }
}
