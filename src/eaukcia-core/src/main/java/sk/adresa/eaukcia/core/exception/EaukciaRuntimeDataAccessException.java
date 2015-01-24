package sk.adresa.eaukcia.core.exception;

public class EaukciaRuntimeDataAccessException extends EaukciaRuntimeException {
    public enum Error { INCONSISTENT_STATE, GENERAL_DATABASE_EXCEPTION, NO_CONNECTION_TO_DB }
    
    private Error error;
    
    public EaukciaRuntimeDataAccessException(String message, Error error) {
        super(message);
        this.error = error;
    }
    
    public EaukciaRuntimeDataAccessException(String message, Throwable cause, Error error) {
        super(message,cause);
        this.error = error;
    }
    
    public EaukciaRuntimeDataAccessException(Throwable cause, Error error) {
        super(cause);
        this.error = error;
    }
    
    public EaukciaRuntimeDataAccessException(Error error) {
        super();
        this.error = error;
    }

    public Error getError() {
        return error;
    }
    
    @Override
    public String toString(){
        return String.format("Error: %s\n %s", error, super.toString());
    }
}
