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
public class SequenceDiagram extends ClassDiagram{
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
     * @param operation Operace dané zprávy.
     * @param sender Odesílatel zprávy.
     * @param receiver Příjemce zprávy.
     * @param transmition Typ přenosu (synchroní, asynchroní).
     */
    public UMLMessage createMessage(UMLOperation operation, UMLClass sender, UMLClass receiver, boolean transmition, boolean occurence, int order)
    {
        UMLMessage newMessage = new UMLMessage(operation, sender, receiver, transmition, occurence, order);
        this.messages.add(newMessage);

        return newMessage;
    }

    /**
     * Vytvoří instanci UML zprávy a vloží ji do diagramu.
     * 
     * @param operation Operace dané zprávy.
     * @param sender Odesílatel zprávy.
     * @param receiver Příjemce zprávy.
     * @param transmition Typ přenosu (synchroní, asynchroní).
     */
    public UMLMessage createMessage(String message, UMLClass sender, UMLClass receiver, boolean transmition, boolean occurence, int order)
    {
        UMLMessage newMessage = new UMLMessage(message, sender, receiver, transmition, occurence, order);
        this.messages.add(newMessage);

        return newMessage;
    }

    /**
     * Vloží zprávu do pole zpráv.
     * 
     * @param message Operace dané zprávy.
     */
    public void addMessage(UMLMessage message)
    {
        this.messages.add(message);
    }

    /**
     * Navrací počet prvků v poli zpráv objektu.
     */
    public int returnMessagesCount()
    {
        return this.messages.size();
    }
}