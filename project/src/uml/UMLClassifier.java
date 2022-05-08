package uml;

import com.fasterxml.jackson.annotation.*;

public class UMLClassifier extends Element{
    @JsonProperty("userDefined")
    private boolean userDefined = false;
    public UMLClassifier(){}
    public UMLClassifier(String name, boolean userDefined){
        super(name);
        this.userDefined = userDefined;
    }   
    public UMLClassifier(String name){
        super(name);
    }
    public static UMLClassifier forName(String name){
        return new UMLClassifier(name);
    }
    public boolean userDefined(){
        return this.userDefined;
    }

    public String toString(){
        String name = this.getName() + "(" + this.userDefined() + ")";
        return name;
    }
}
