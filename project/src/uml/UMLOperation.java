package uml;

import java.util.List;
import java.util.ArrayList;

public final class UMLOperation extends UMLAttribute{
    private ArrayList<UMLAttribute> args;
    public UMLOperation(String name, UMLClassifier type){
        super(name, type);
    }
    public static UMLOperation create(String name, UMLClassifier type, UMLAttribute... args){
        UMLOperation inst = new UMLOperation(name, type);
        inst.args = new ArrayList<UMLAttribute>();
        for(UMLAttribute arg : args){
            inst.args.add(arg);
        }
        return inst;        
    }
    public boolean addArgument(UMLAttribute arg){
        if(this.args.contains(arg)){
            return false;
        }
        this.args.add(arg);
        return true;
    }
    public final List<UMLAttribute> getArguments(){
        return this.args;
    }
}
