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
    public UMLOperation operation;
    @JsonProperty("message")
    public String message;
    @JsonProperty("sender")
    public UMLClass sender;
    @JsonProperty("receiver")
    public UMLClass receiver;
    @JsonProperty("transmition")
    public boolean transmition; // true = call, false = reply
    @JsonProperty("occurence")
    public boolean occurence; // true = sync, false = async
    @JsonProperty("order")
    public int order; // -1 = last

    public UMLMessage(){}

    /**
     * Konstruktor pro vytvoření instance diagramu s udáním typu zprávy.
     * @param operation Operace dané zprávy.
     * @param sender Odesílatel zprávy.
     * @param receiver Příjemce zprávy.
     * @param transmition Typ přenosu (volání, odpověď).
     * @param occurence Typ přenosu (synchroní, asynchroní).
     * @param order Pořadí zprávy.
     */
    public UMLMessage(UMLOperation operation, UMLClass sender, UMLClass receiver, boolean transmition, boolean occurence, int order)
    {
        this.operation = operation;
        this.sender = sender;
        this.receiver = receiver;
        this.transmition = transmition;
        this.occurence = occurence;
        this.order = order;
    }

    /**
     * Konstruktor pro vytvoření instance diagramu s udáním typu zprávy.
     * @param message Operace dané zprávy.
     * @param sender Odesílatel zprávy.
     * @param receiver Příjemce zprávy.
     * @param transmition Typ přenosu (volání, odpověď).
     * @param occurence Typ přenosu (synchroní, asynchroní).
     * @param order Pořadí zprávy.
     */
    public UMLMessage(String message, UMLClass sender, UMLClass receiver, boolean transmition, boolean occurence, int order)
    {
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
        this.transmition = transmition;
        this.occurence = occurence;
        this.order = order;
    }
}