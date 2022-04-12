/**
 * Třída reprezentuje zprávy mezi instancemi tříd.
 *
 * @author Adam Bazel (xbazel00)
 * @since  2022-04-12
 */

package uml;

import com.fasterxml.jackson.annotation.*;

/**
 * Třída reprezentuje zprávy mezi instancemi tříd.
 */
public class UMLMessage extends UMLOperation{
    @JsonProperty("sender")
    private UMLClass sender = new UMLClass();
    @JsonProperty("receiver")
    private UMLClass receiver = new UMLClass();
    @JsonProperty("transmition")
    private boolean transmition = true; // ture = sync, false = async

    public UMLMessage(){}

    /**
     * Konstruktor pro vytvoření instance diagramu bez udání typu zprávy.
     * @param name Název vytvářené třídy.
     * @param type Typ atributu.
     * @param sender Odesílatel zprávy.
     * @param receiver Příjemce zprávy.
     */
    public UMLMessage(String name, UMLClassifier type, UMLClass sender, UMLClass receiver)
    {
        super(name, type);
        this.sender = sender;
        this.receiver = receiver;
    }   

    /**
     * Konstruktor pro vytvoření instance diagramu s udáním typu zprávy.
     * @param name Název vytvářené třídy.
     * @param type Typ atributu.
     * @param sender Odesílatel zprávy.
     * @param receiver Příjemce zprávy.
     * @param transmition Typ přenosu (synchroní, asynchroní).
     */
    public UMLMessage(String name, UMLClassifier type, UMLClass sender, UMLClass receiver, boolean transmition)
    {
        super(name, type);
        this.sender = sender;
        this.receiver = receiver;
        this.transmition = transmition;
    }

    /**
     * Metoda na navrácení typu zprávy.
     */
    public boolean transmition()
    {
        return this.transmition;
    }
}
