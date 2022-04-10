package uml;

import com.fasterxml.jackson.annotation.*;

public class UMLMessage extends UMLOperation{
    @JsonProperty("sender")
    private UMLClass sender = new UMLClass();
    @JsonProperty("receiver")
    private UMLClass receiver = new UMLClass();
    @JsonProperty("transmition")
    private boolean transmition = true; // ture = sync, false = async

    public UMLMessage(){}

    public UMLMessage(String name, UMLClassifier type, UMLOperation operation, UMLClass sender, UMLClass reciever){
        super(name, type);
        this.sender = sender;
        this.receiver = reciever;
    }   
    public UMLMessage(String name, UMLClassifier type, UMLClass sender, UMLClass receiver, boolean transmition){
        super(name, type);
        this.sender = sender;
        this.receiver = receiver;
        this.transmition = transmition;
    }
    
    public boolean transmition(){
        return this.transmition;
    }
}
