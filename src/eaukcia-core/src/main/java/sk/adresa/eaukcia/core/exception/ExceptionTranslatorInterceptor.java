package sk.adresa.eaukcia.core.exception;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.transaction.CannotCreateTransactionException;

/**
 *
 * @author juraj
 */
public class ExceptionTranslatorInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        try {
            return methodInvocation.proceed();
        } catch ( CannotCreateTransactionException e ){
            throw new EaukciaRuntimeDataAccessException("Chyba v komunikácií s DB", e, EaukciaRuntimeDataAccessException.Error.NO_CONNECTION_TO_DB);
        } catch (Throwable t) {
            throw t;
        }
    }



}
