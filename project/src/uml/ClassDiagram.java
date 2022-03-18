package uml;

import java.util.ArrayList;

public class ClassDiagram extends Element{
    private ArrayList<UMLClass> classes = new ArrayList<UMLClass>();
    private ArrayList<UMLClassifier> classifiers = new ArrayList<UMLClassifier>();
    public ClassDiagram(String name){
        super(name);
    }
    public UMLClass createClass(String name){
        UMLClass search;
        for(int i = 0; i < this.classes.size(); i++){
            search = this.classes.get(i);
            if(search.getName() == name){
                return null;
            }
        }
        UMLClass newClass = new UMLClass(name);
        this.classes.add(newClass);
        this.classifiers.add(newClass);
        return newClass;
        
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
}
