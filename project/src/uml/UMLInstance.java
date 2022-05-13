/**
 * Třída reprezentuje zprávy mezi instancemi tříd.
 *
 * @author Adam Bazel (xbazel00)
 * @since  2022-04-12
 */

package uml;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.*;

/**
 * Třída reprezentuje zprávy mezi instancemi tříd.
 */
public class UMLInstance extends Element
{
    @JsonProperty("name")
    public String name;
    @JsonProperty("asgclass")
    public UMLClass asgclass;
    @JsonProperty("activations")
    public List<UMLActivation> activations;

    public UMLInstance(){}

    public UMLInstance(String name, UMLClass asgclass)
    {
        this.name = name;
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
}