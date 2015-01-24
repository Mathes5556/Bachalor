package sk.adresa.eaukcia.core.exception;

public class EaukciaDataIntegrityViolationException extends EaukciaException {
    
    public EaukciaDataIntegrityViolationException(String message) {
        super(message);
    }
    
    public EaukciaDataIntegrityViolationException(String message, Throwable cause, Error error) {
        super(message,cause);
    }
    
    public EaukciaDataIntegrityViolationException(Throwable cause) {
        super(cause);
    }
    
}
