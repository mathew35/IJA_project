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
    public UMLInstance sender;
    @JsonProperty("receiver")
    public UMLInstance receiver;
    @JsonProperty("transmition")
    public int transmition; // 0 = sync, 1 = async, 2 = creation, 3 = reply
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
    public UMLMessage(UMLOperation operation, UMLInstance  sender, UMLInstance  receiver, int transmition, int order)
    {
        this.operation = operation;
        this.sender = sender;
        this.receiver = receiver;
        this.transmition = transmition;
        this.order = order;
    }

    /**
     * Konstruktor pro vytvoření instance diagramu s udáním typu zprávy.
     * @param message Operace dané zprávy.
     * @param operation Operace dané zprávy.
     * @param sender Odesílatel zprávy.
     * @param receiver Příjemce zprávy.
     * @param transmition Typ přenosu (volání, odpověď).
     * @param order Pořadí zprávy.
     */
    public UMLMessage(String message, UMLOperation operation, UMLInstance  sender, UMLInstance  receiver, int transmition, int order)
    {
        this.message = message;
        this.operation = operation;
        this.sender = sender;
        this.receiver = receiver;
        this.transmition = transmition;
        this.order = order;
    }

    /**
     * Konstruktor pro vytvoření instance diagramu s udáním typu zprávy.
     * @param message Operace dané zprávy.
     * @param sender Odesílatel zprávy.
     * @param receiver Příjemce zprávy.
     * @param transmition Typ přenosu.
     * @param order Pořadí zprávy.
     */
    public UMLMessage(String message, UMLInstance  sender, UMLInstance  receiver, int transmition, int order)
    {
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
        this.transmition = transmition;
        this.order = order;
    }
}