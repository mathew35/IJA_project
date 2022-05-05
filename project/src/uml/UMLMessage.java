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
public class UMLMessage{
    @JsonProperty("operation")
    private UMLOperation operation;
    @JsonProperty("sender")
    private UMLClass sender;
    @JsonProperty("receiver")
    private UMLClass receiver;
    @JsonProperty("transmition")
    private boolean transmition = true; // true = call, false = reply
    @JsonProperty("order")
    private int order; // true = call, false = reply

    public UMLMessage(){}

    /**
     * Konstruktor pro vytvoření instance diagramu s udáním typu zprávy.
     * @param operation Operace dané zprávy.
     * @param sender Odesílatel zprávy.
     * @param receiver Příjemce zprávy.
     * @param transmition Typ přenosu (volání, odpověď).
     */
    public UMLMessage(UMLOperation operation, UMLClass sender, UMLClass receiver, boolean transmition, int order)
    {
        this.operation = operation;
        this.sender = sender;
        this.receiver = receiver;
        this.transmition = transmition;
        this.order = order;
    }

    /**
     * Metoda na navrácení typu zprávy.
     */
    public boolean transmition()
    {
        return this.transmition;
    }
}