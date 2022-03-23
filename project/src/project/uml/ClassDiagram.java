package src.project.uml;

import java.util.ArrayList;
import java.util.List;


public class ClassDiagram extends Element
{
    List<UMLClassifier> diagramClassifiers = new ArrayList<>();

    public ClassDiagram(java.lang.String name)
    {
        super(name);
    }

    public UMLClass createClass(String name) 
    {
        for (UMLClassifier classResult : diagramClassifiers) 
        {
            if (name.equals(classResult.getName())) 
            {
                return null;
            }
        }

        UMLClass cls = new UMLClass(name);

        diagramClassifiers.add(cls);
        return cls;
    }

    public UMLClassifier classifierForName(String name) 
    {
        for (UMLClassifier classifierResult : diagramClassifiers) 
        {
            if (name.equals(classifierResult.getName())) 
            {
                return classifierResult;
            }
        }

        UMLClassifier cls = UMLClassifier.forName(name);
        diagramClassifiers.add(cls);

        return cls;
    }

    public UMLClassifier findClassifier(String name) 
    {
        for (UMLClassifier classifierResult : diagramClassifiers) 
        {
            if (name.equals(classifierResult.getName())) 
            {
                return classifierResult;
            }
        }

        return null;
    }

    // return tree of objects from JSON file
    public void loadDiagram()
    {
       
    }
}
