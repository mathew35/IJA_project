package src.project.uml;


public class Element 
{
	public java.lang.String name;

	public Element(java.lang.String name) 
    {
	    this.name = name;
    }

    public java.lang.String getName()
    {
        return name;
    }

    public void rename​(java.lang.String newName)
    {
        this.name = newName;
    }
}