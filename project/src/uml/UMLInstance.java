/**
 * Třída reprezentuje zprávy mezi instancemi tříd.
 *
 * @author Adam Bazel (xbazel00)
 * @since  2022-04-12
 */

package uml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.*;

/**
 * Třída reprezentuje zprávy mezi instancemi tříd.
 */
public class UMLInstance extends Element
{
    @JsonProperty("instancename")
    public String instancename;
    @JsonProperty("asgclass")
    public UMLClass asgclass;
    @JsonProperty("activations")
    public List<UMLActivation> activations = new ArrayList<UMLActivation>();

    public UMLInstance(){}

    public UMLInstance(String instancename, UMLClass asgclass)
    {
        this.instancename = instancename;
        this.asgclass = asgclass;
    }

    public List<UMLActivation> getActivations(){
        return Collections.unmodifiableList(this.activations);
    }

    public void removeActivation(UMLActivation activationRemove)
    {
        this.activations.remove(activationRemove);
    }

    public void addActivation(UMLActivation activation)
    {
        this.activations.add(activation);
    }

    public void deepCopyInstance(UMLInstance another)
    {
        for (UMLActivation act : another.activations)
        {
            this.addActivation(new UMLActivation(act.getStart(), act.getEnd()));
        }  
    }
}