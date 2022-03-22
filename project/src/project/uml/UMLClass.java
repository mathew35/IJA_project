package ija.homework1.uml;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public class UMLClass extends UMLClassifier
{
    boolean	isAbstract;
    List<UMLAttribute> classAttributes = new ArrayList<>();

    public UMLClass(java.lang.String name)
    {
        super(name);
        this.isAbstract = false;
    }

    public boolean isAbstract()
    {
        return this.isAbstract;
    }

    public void setAbstract(boolean isAbstract) 
    {
        this.isAbstract = isAbstract;
    }

    public boolean addAttribute(UMLAttribute attr) 
    {
        for (UMLAttribute attributeResult : classAttributes) 
        {
            if (name.equals(attributeResult.getName())) 
            {
                return false;
            }
        }

        classAttributes.add(attr);
        return true;
    }

    public int getAttrPosition(UMLAttribute attr) 
    {
        return classAttributes.indexOf(attr);
    }

    public int moveAttrAtPosition(UMLAttribute attr, int pos) 
    {
        if (!classAttributes.contains(attr))
        {
            return -1;
        }

        int attrPos = classAttributes.indexOf(attr);

        // moves the element to the specified position and moves other elements "up"
        Collections.swap(classAttributes, attrPos, pos);

        return 0;
    }

    public List<UMLAttribute> getAttributes() 
    {
        return Collections.unmodifiableList(classAttributes);
    }
}
