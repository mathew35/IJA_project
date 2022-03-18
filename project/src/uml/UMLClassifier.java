package uml;

public class UMLClassifier extends Element{
    private boolean isUserDefined = false;
    public UMLClassifier(String name, boolean isUserDefined){
        super(name);
        this.isUserDefined = isUserDefined;
    }   
    public UMLClassifier(String name){
        super(name);
    }
    public static UMLClassifier forName(String name){
        return new UMLClassifier(name);
    }
    public boolean isUserDefined(){
        return this.isUserDefined;
    }
    public String toString(){
        String name = this.getName() + "(" + this.isUserDefined() + ")";
        return name;
    }
}
