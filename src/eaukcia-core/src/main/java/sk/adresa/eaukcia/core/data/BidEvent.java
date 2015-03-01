/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.adresa.eaukcia.core.data;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author mathes
 */
public class BidEvent extends Event{
    
    public BidEvent(Integer item_id, BigDecimal numeric_value, String fk_user, Date last_modified){
        super.setItem_id(item_id);
        super.setNumeric_value(numeric_value);
        super.setFk_user(fk_user);
        super.setLast_modified(last_modified);
    }
    
}
