package uml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.*;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")
public class ClassDiagram extends Element{
    @JsonProperty("classes")
    private List<UMLClass> classes = new ArrayList<UMLClass>();
    @JsonProperty("classifiers")
    private List<UMLClassifier> classifiers = new ArrayList<UMLClassifier>();
    public ClassDiagram(){}
    public ClassDiagram(String name){
        super(name);
    }
    public UMLClass createClass(String name){
        UMLClass search;
        for(int i = 0; i < this.classes.size(); i++){
            search = this.classes.get(i);
            if(search.getName().equals(name)){
                return null;
            }
        }
        UMLClass newClass = new UMLClass(name);
        UMLClassifier newClassifier = new UMLClassifier(name);
        this.classes.add(newClass);
        this.classifierForName(name);
        return newClass;
        
    }
    public void removeClass(String name){
        UMLClass search;
        for(int i = 0; i < this.classes.size(); i++){
            search = this.classes.get(i);
            if(search.getName().equals(name)){
                this.classes.remove(search);
            }
        }
        
    }

    public void removeClassByIndex(int index)
    {
        if (index <= this.classes.size())
        {
            this.classes.remove(index);
        }
    }
    public UMLClassifier classifierForName(String name){
        UMLClassifier classifier =  findClassifier(name);
        if(classifier == null){
            classifier = new UMLClassifier(name, false);
        }
        this.classifiers.add(classifier);
        return classifier; 
    }
    public UMLClassifier findClassifier(String name){
        UMLClassifier find = null;
        for(int i = 0; i < this.classifiers.size(); i++){
            find = this.classifiers.get(i);
            if(find.getName() == name){
                return find;
            }
        }
        return null;
    }

    @JsonIgnore
    public ArrayList<String> getNameClasses()
    {
        ArrayList<String> nameList = new ArrayList<String>();
        for (int i = 0; i < classes.size(); i++) 
        {
            nameList.add(classes.get(i).getName());
        }
        return nameList;
    }

    @JsonIgnore
    public List<UMLClass> getClasses(){
        return Collections.unmodifiableList(this.classes);
    }

    @JsonIgnore
    public List<UMLClassifier> getClassifiers(){
        return Collections.unmodifiableList(this.classifiers);
    }

    @JsonIgnore
    public int getClassIndexByName(String name)
    {
        for (int i = 0; i < this.classes.size(); i++)
        {
            if (this.classes.get(i).getName().equals(name))
            {
                return i;
            }
        }

        return -1;
    }

    public void deepCopy(ClassDiagram another){
        ClassDiagram retDiag = new ClassDiagram(another.getName());
        for(String diagClassName:another.getNameClasses()){
            retDiag.createClass(diagClassName);
        }
        for(UMLClass diagClass:another.classes){
            for(UMLClass retClass:retDiag.classes){
                if(retClass.getName().equals(diagClass.getName())){
                    retClass.deepCopyClass(diagClass);
                }
            }
        }
        for(UMLClass diagClass:another.classes){
            for(UMLClass retclass:retDiag.classes){
                if(retclass.getName().equals(diagClass.getName())){
                    if(diagClass.getParent() != null){
                        for(UMLClass search:retDiag.classes){
                            if(search.getName().equals(diagClass.getParent().getName())){
                                retclass.setParent(search);
                            }
                        }
                    }
                }
            }
        }
        for(UMLClassifier diagClassifier:another.classifiers){
            retDiag.classifierForName(diagClassifier.getName());
        }
        this.rename(retDiag.getName());
        this.classes = retDiag.classes;
        this.classifiers = retDiag.classifiers;
    }
}
