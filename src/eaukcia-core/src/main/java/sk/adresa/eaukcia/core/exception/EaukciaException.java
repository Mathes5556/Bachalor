package sk.adresa.eaukcia.core.exception;

public class EaukciaException extends Exception {

    
    protected EaukciaException(String message) {
        super(message);

    }
    
    protected EaukciaException(String message, Throwable cause) {
        super(message,cause);

    }
    
    protected EaukciaException(Throwable cause) {
        super(cause);
    }
    
    protected EaukciaException() {
        super();
    }
}
