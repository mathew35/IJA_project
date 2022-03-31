package uml;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import com.fasterxml.jackson.annotation.*;

public class UMLClass  extends UMLClassifier{
    private boolean isAbstract = false;
    @JsonProperty("attributes")
    private List<UMLAttribute> attributes = new ArrayList<UMLAttribute>();
    public UMLClass(){}
    public UMLClass(String name){
        super(name,true);
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
    public int getAttrPosition(UMLAttribute attr){
        return this.attributes.indexOf(attr);
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
}
