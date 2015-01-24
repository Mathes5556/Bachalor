package sk.adresa.eaukcia.core.exception;

public class EaukciaObjectNotFoundException extends EaukciaException {
    
    public EaukciaObjectNotFoundException(String message) {
        super(message);
    }
    
    public EaukciaObjectNotFoundException(String message, Throwable cause, Error error) {
        super(message,cause);
    }
    
    public EaukciaObjectNotFoundException(Throwable cause) {
        super(cause);
    }
    
}
