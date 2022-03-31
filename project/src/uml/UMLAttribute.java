package uml;

import com.fasterxml.jackson.annotation.*;

public class UMLAttribute extends Element{
    @JsonProperty("type")
    private UMLClassifier type;
    public UMLAttribute(){}
    public UMLAttribute(String name, UMLClassifier type){
        super(name);
        this.type = type;        
    }
    public UMLClassifier getType(){
        return this.type;
    }
    public String toString(){
        String nametype = this.getName() + ":" + this.type;
        return nametype;
    }

}

