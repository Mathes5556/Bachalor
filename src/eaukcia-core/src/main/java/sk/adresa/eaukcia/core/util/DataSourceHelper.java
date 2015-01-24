package sk.adresa.eaukcia.core.util;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.sql.DataSource;


public class DataSourceHelper {
    
    public static DataSource getDataSource(String jndiName) throws NamingException {
        InitialContext ic = new InitialContext();
        return (DataSource)ic.lookup(jndiName);
        
        
    }
    
}
