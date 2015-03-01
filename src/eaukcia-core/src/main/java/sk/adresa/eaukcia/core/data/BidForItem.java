/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.adresa.eaukcia.core.data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 *
 * @author mathes
 */
public class BidForItem {
    
    private BigInteger code;
    
    private String fk_user; 

    private Date time;

    private BigDecimal numeric_value;

    private Integer item_id;
    
    private User user;
    
    
    public BidForItem(Integer item_id, BigInteger code, String fk_user, BigDecimal numeric_value, Date time,  User user) {
        this.code = code;
        this.fk_user = fk_user;
        this.numeric_value = numeric_value;
        this.item_id = item_id;
        this.time = time;
        this.user = user;
    }

    public void setCode(BigInteger code) {
        this.code = code;
    }

    public String getFk_user() {
        return fk_user;
    }

    public void setFk_user(String fk_user) {
        this.fk_user = fk_user;
    }

    public BigDecimal getNumeric_value() {
        return numeric_value;
    }

    public void setNumeric_value(BigDecimal numeric_value) {
        this.numeric_value = numeric_value;
    }
      
    public Integer getItem_id() {
        return item_id;
    }

    public void setItem_id(Integer item_id) {
        this.item_id = item_id;
    }
    
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
    
}
