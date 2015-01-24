package sk.adresa.eaukcia.core.exception;

public class EaukciaRuntimeException extends RuntimeException {


    public EaukciaRuntimeException(String message) {
        super(message);
    }
    
    public EaukciaRuntimeException(String message, Throwable cause) {
        super(message,cause);
    }
    
    public EaukciaRuntimeException(Throwable cause) {
        super(cause);
    }
    
    public EaukciaRuntimeException() {
        super();
    }
}
