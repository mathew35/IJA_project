package uml;

import com.fasterxml.jackson.annotation.*;

public class Element {
    @JsonProperty("name")
    private String name;
    public Element(){}
    public Element(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
    public void rename(String newName){
        this.name = newName;
    }
}
