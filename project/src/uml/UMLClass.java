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
    public UMLClass(){}
    public UMLClass(String name){
        super(name,true);
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
}
