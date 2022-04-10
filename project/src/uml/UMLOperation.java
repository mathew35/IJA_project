package uml;

import java.util.List;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.*;

public class UMLOperation extends UMLAttribute{
    @JsonProperty("arguments")
    private ArrayList<UMLAttribute> arguments;
    public UMLOperation(){}
    public UMLOperation(String name, UMLClassifier type){
        super(name, type);
    }
    public static UMLOperation create(String name, UMLClassifier type, UMLAttribute... arguments){
        UMLOperation inst = new UMLOperation(name, type);
        inst.arguments = new ArrayList<UMLAttribute>();
        for(UMLAttribute arg : arguments){
            inst.arguments.add(arg);
        }
        return inst;        
    }
    public boolean addArgument(UMLAttribute arg){
        if(this.arguments.contains(arg)){
            return false;
        }
        this.arguments.add(arg);
        return true;
    }
    public final List<UMLAttribute> getArguments(){
        return this.arguments;
    }
}
