package uml;

import java.util.*;
import com.fasterxml.jackson.annotation.*;

public class UMLOperation extends UMLAttribute{
    @JsonProperty("arguments")
    private ArrayList<UMLAttribute> arguments = new ArrayList<UMLAttribute>();
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

    @JsonIgnore
    public final String getAlltoString()
    {
        String argumentsString = "";

        argumentsString = argumentsString + this.getName() + "(";
        
        for (int i = 0; i < this.arguments.size(); i++)
        {
            if (i > 0)
            {
                argumentsString = argumentsString + ", ";
            }

            argumentsString = argumentsString + this.arguments.get(i).getName();
        }
        argumentsString = argumentsString + ") : " + this.getTypeString();
        
        return argumentsString;
    }
}
