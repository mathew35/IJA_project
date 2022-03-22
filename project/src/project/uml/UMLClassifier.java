package ija.homework1.uml;

public class UMLClassifier extends Element
{
    public boolean isUserDefined;

    public UMLClassifier(java.lang.String name, boolean isUserDefined)
    {
        super(name);
        this.isUserDefined = isUserDefined;
    }

    public UMLClassifier(java.lang.String name)
    {
        super(name);
        this.isUserDefined = true;
    }

    public static UMLClassifier forName(String name) 
    {
        return new UMLClassifier(name, false);
    }

    public boolean isUserDefined()
    {
        return isUserDefined;
    }

    public java.lang.String toString()
    {
        return (String) name + "(" + isUserDefined + ")";
    }
}
