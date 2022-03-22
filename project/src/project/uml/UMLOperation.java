package ija.homework1.uml;

import java.util.ArrayList;
import java.util.List;

public class UMLOperation extends UMLAttribute
{
    List<UMLAttribute> operationArguments = new ArrayList<>();

    public UMLOperation(String name, UMLClassifier type) 
    {
        super(name, type);
    }

    public static UMLOperation create(String name, UMLClassifier type, UMLAttribute... args) 
    {
        UMLOperation operation = new UMLOperation(name, type);
        
        for (UMLAttribute arg : args)
        {
            operation.operationArguments.add(arg);
        }

        return operation;
    }
    
    public boolean addArgument​(UMLAttribute arg)
    {
        for (UMLAttribute argumentResult : operationArguments) 
        {
            if (name.equals(argumentResult.getName())) 
            {
                return false;
            }
        }

        operationArguments.add(arg);
        return true;
    }

    public List<UMLAttribute> getArguments() 
    {
        return operationArguments;
    }
}
