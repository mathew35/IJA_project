package uml;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class UMLClass  extends UMLClassifier{
    private boolean isAbstract = false;
    private ArrayList<UMLAttribute> Attributes = new ArrayList<UMLAttribute>();
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
            this.Attributes.add(attr);
            return true;
        }
        return false;
    }
    public int getAttrPosition(UMLAttribute attr){
        return this.Attributes.indexOf(attr);
    }
    public int moveAttrAtPosition(UMLAttribute attr, int pos){
        int actpos = getAttrPosition(attr);
        if(actpos != -1){
            if(pos > actpos){
                UMLAttribute tmp = this.Attributes.get(this.Attributes.size() - 1);
                for(int i = this.Attributes.size() - 1; i > pos; i--){
                    this.Attributes.set(i, this.Attributes.get(i - 1));
                }
                for(int i = pos - 1; i > 0; i--){
                    this.Attributes.set(i, this.Attributes.get(i - 1));
                }
                this.Attributes.set(0, tmp);
                this.Attributes.set(pos, attr);
            }
            if(pos < actpos){
                for(int i = actpos; i > pos; i--){
                    this.Attributes.set(i, this.Attributes.get(i - 1));
                }
                this.Attributes.set(pos, attr);
            }
            return pos;
        }
        return -1;
    }
    public List<UMLAttribute> getAttributes(){
        return Collections.unmodifiableList(this.Attributes);
    }
}
