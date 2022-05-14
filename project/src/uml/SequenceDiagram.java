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
    public List<UMLMessage> messages = new ArrayList<UMLMessage>();
    @JsonProperty("instances")
    public List<UMLInstance> instances = new ArrayList<UMLInstance>();
        
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
    public UMLMessage createMessage(UMLOperation operation, UMLInstance  sender, UMLInstance receiver, int transmition, int order)
    {
        UMLMessage newMessage = new UMLMessage(operation, sender, receiver, transmition, order);
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
    public UMLMessage createMessage(String message, UMLInstance  sender, UMLInstance receiver, int transmition, int order)
    {
        UMLMessage newMessage = new UMLMessage(message, sender, receiver, transmition, order);
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

    public UMLInstance createInstance(String name, UMLClass asgClass)
    {
        UMLInstance newInstance = new UMLInstance(name, asgClass);
        this.instances.add(newInstance);

        return  newInstance;
    }
    

    /**
     * Navrací počet prvků v poli zpráv objektu.
     */
    public int returnMessagesCount()
    {
        return this.messages.size();
    }

    @JsonIgnore
    public String getMessagesText()
    {
        String lineup = "";
        if (messages.size() != 0)
        {
            for (int i = 0; i < messages.size(); i++)
            {
                lineup = lineup + " " + messages.get(i).message;
            }
        }

        return lineup;
    }

    public Integer getIndexOfInstace(UMLInstance instance)
    {
        for (int i = 0; i < this.instances.size(); i++)
        {
            //System.out.println("Entered: " + instance.instancename + " Copared: " + this.instances.get(i).instancename);
            if (this.instances.get(i).instancename.equals(instance.instancename) && this.instances.get(i).asgclass.getName().equals(instance.asgclass.getName()))
            {
                //System.out.println("Found same: " + instance.instancename + " " + this.instances.get(i).instancename);
                return i;
            }
        }

        return -1;
    }

    @JsonIgnore
    public SequenceDiagram deepCopySeq()
    {
        SequenceDiagram retDiag = new SequenceDiagram(this.getName());
        for(String diagClassName:this.getNameClasses())
        {
            retDiag.createClass(diagClassName);
        }
        for(UMLClass diagClass:this.getClasses())
        {
            for(UMLClass retClass:retDiag.getClasses())
            {
                if(retClass.getName().equals(diagClass.getName()))
                {
                    retClass.deepCopyClass(diagClass);
                }
            }
        }

        for(UMLClassifier diagClassifier:this.getClassifiers())
        {
            retDiag.classifierForName(diagClassifier.getName());
        }

        for (UMLInstance dInstance: this.instances)
        {
            retDiag.createInstance(dInstance.instancename, dInstance.asgclass);
            for (UMLInstance rInstance:retDiag.instances)
            {
                if (rInstance.instancename.equals(dInstance.instancename))
                {
                    rInstance.deepCopyInstance(dInstance);
                }
            }
        }

        for (UMLMessage diaMessage: this.messages)
        {
            retDiag.createMessage(diaMessage.message, diaMessage.sender, diaMessage.receiver, diaMessage.transmition, diaMessage.order);
        }

        return retDiag;
    }
}