package sk.adresa.eaukcia.core.data;

/**
 *
 * @author mathes
 */
public enum EventType {
    ADD_REQUIRMENT("add..."),
    
    MAKE_OFFER("...");
    
    private String type;
    
    private EventType(String type) {
        this.type = type;
    }

}
