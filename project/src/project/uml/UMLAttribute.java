package ija.homework1.uml;

public class UMLAttribute extends Element
{
    public UMLClassifier type;

    public UMLAttribute(String name, UMLClassifier type) 
    {
        super(name);
        this.type = type;
    }

    public UMLClassifier getType() 
    {
        return type;
    }

    public java.lang.String toString()
    {
        return (String) name + ":" + type;
    }
}
