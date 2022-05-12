package uml;

import java.util.*;
import com.fasterxml.jackson.annotation.*;

public class UMLClass  extends UMLClassifier{
    private boolean isAbstract = false;
    @JsonProperty("parent")
    private UMLClass parent = null;
    @JsonProperty("attributes")
    private List<UMLAttribute> attributes = new ArrayList<UMLAttribute>();
    @JsonProperty("operations")
    private List<UMLOperation> operations = new ArrayList<UMLOperation>();
    @JsonProperty("activations")
    private List<UMLActivation> activations = new ArrayList<UMLActivation>();
    public UMLClass(){}
    public UMLClass(String name){
        super(name,true);
    }
    public UMLClass(UMLClass another){
        super(another.getName(),another.userDefined());
        this.parent = another.parent;
        this.attributes = another.attributes;
        this.operations = another.operations;
    }
    public void setParent(UMLClass parent){
        this.parent = parent;
    }
    public UMLClass getParent(){
        return this.parent;
    }
    public boolean isAbstract(){
        return this.isAbstract;
    }
    public void setAbstract(boolean isAbstract){
        this.isAbstract = isAbstract;
    }
    public boolean addAttribute(UMLAttribute attr){
        int pos = getAttrPosition(attr);
        if(pos == -1){
            this.attributes.add(attr);
            return true;
        }
        return false;
    }
    public boolean addOperation(UMLOperation oper){
        int pos = getOperPosition(oper);
        if(pos == -1){
            this.operations.add(oper);
            return true;
        }
        return false;
    }
    public void removeAttrOper(String name){
        UMLAttribute foundA = null;
        UMLOperation foundO = null;
        for(UMLAttribute i:this.attributes){
            if(name==i.getName()){
                foundA = i;
            }
        }
        for(UMLOperation i:this.operations){
            if(name==i.getName()){
                foundO = i;
            }
        }
        if(foundA != null){
            this.attributes.remove(foundA);
        }
        if(foundO != null){
            this.operations.remove(foundO);
        }
    }
    public void renameAttrOper(String name,String newName){
        UMLAttribute foundA = null;
        UMLOperation foundO = null;
        for(UMLAttribute i:this.getAttributes()){
            if(name==i.getName()){
                foundA = i;
            }
        }
        for(UMLOperation i:this.getOperations()){
            if(name==i.getName()){
                foundO = i;
            }
        }
        if(foundA != null){
            this.attributes.get(this.attributes.indexOf(foundA)).rename(newName);
        }
        if(foundO != null){
            this.operations.get(this.operations.indexOf(foundO)).rename(newName);
        }
    }
    public int getAttrPosition(UMLAttribute attr){
        return this.attributes.indexOf(attr);
    }
    public int getOperPosition(UMLOperation oper){
        return this.operations.indexOf(oper);
    }
    public int moveAttrAtPosition(UMLAttribute attr, int pos){
        int actpos = getAttrPosition(attr);
        if(actpos != -1){
            if(pos > actpos){
                UMLAttribute tmp = this.attributes.get(this.attributes.size() - 1);
                for(int i = this.attributes.size() - 1; i > pos; i--){
                    this.attributes.set(i, this.attributes.get(i - 1));
                }
                for(int i = pos - 1; i > 0; i--){
                    this.attributes.set(i, this.attributes.get(i - 1));
                }
                this.attributes.set(0, tmp);
                this.attributes.set(pos, attr);
            }
            if(pos < actpos){
                for(int i = actpos; i > pos; i--){
                    this.attributes.set(i, this.attributes.get(i - 1));
                }
                this.attributes.set(pos, attr);
            }
            return pos;
        }
        return -1;
    }
    public List<UMLAttribute> getAttributes(){
        return Collections.unmodifiableList(this.attributes);
    }

    public List<UMLOperation> getOperations(){
        return Collections.unmodifiableList(this.operations);
    }

    public List<UMLActivation> getActivations(){
        return Collections.unmodifiableList(this.activations);
    }

    public void removeActivation(UMLActivation activationRemove)
    {
        this.activations.remove(activationRemove);
    }

    public void addActivation(UMLActivation activation)
    {
        this.activations.add(activation);
    }

    public void deepCopyClass(UMLClass another){
        for(UMLAttribute i:another.attributes){
            this.addAttribute(new UMLAttribute(i.getName(),i.getType()));
        }
        for(UMLOperation i:another.operations){
            this.addOperation(UMLOperation.create(i.getName(),i.getType(),i.getArguments().toArray(UMLAttribute[]::new)));
        }
        for(UMLActivation i:another.activations){
            this.addActivation(new UMLActivation(i.getStart(), i.getEnd(), i.getDeactivation()));
        }        
    }
}
