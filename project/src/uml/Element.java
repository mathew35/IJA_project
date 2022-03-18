package uml;

public class Element {
    private String name;
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
