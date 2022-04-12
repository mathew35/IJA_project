package uml;

import java.util.*;
import com.fasterxml.jackson.annotation.*;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")
public class SequenceDiagram extends Element{
    @JsonProperty("classes")
    private List<UMLClass> classes = new ArrayList<UMLClass>();
    @JsonProperty("messages")
    private List<UMLMessage> messages = new ArrayList<UMLMessage>();
        
    public SequenceDiagram(){}
    public SequenceDiagram(String name){
        super(name);
    }

    public UMLClass createClass(String name){
        UMLClass search;
        for(int i = 0; i < this.classes.size(); i++){
            search = this.classes.get(i);
            if(search.getName() == name){
                return null;
            }
        }
        UMLClass newClass = new UMLClass(name);
        this.classes.add(newClass);
        return newClass;
    }

    public UMLMessage createMessage(String name, UMLClassifier type, UMLClass sender, UMLClass receiver, boolean transmition)
    {
        UMLMessage newMessage = new UMLMessage(name, type, sender, receiver, transmition);
        this.messages.add(newMessage);

        return newMessage;
    }
}
