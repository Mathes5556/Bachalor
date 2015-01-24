package sk.adresa.eaukcia.core.data;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author mathes
 */
public class Event implements Comparable<Event> {
     //TODO UNIT_TYPE,CODE
    private Integer item_id;
        
    private BigDecimal numeric_value;
    
    private int round;
    
    private String fk_user;
    
    private Date last_modified;
    
    public Integer getItem_id() {
        return item_id;
    }

    public void setItem_id(Integer item_id) {
        this.item_id = item_id;
    }

    public BigDecimal getNumeric_value() {
        return numeric_value;
    }

    public void setNumeric_value(BigDecimal numeric_value) {
        this.numeric_value = numeric_value;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public String getFk_user() {
        return fk_user;
    }

    public void setFk_user(String fk_user) {
        this.fk_user = fk_user;
    }

    public Date getLast_modified() {
        return last_modified;
    }

    public void setLast_modified(Date last_modified) {
        this.last_modified = last_modified;
    }
   
    
    
    /*
    private RequirementNode product;
    
    private EventType type;
    
    private Offer offer;
    
     public Event( Offer offer, int idProduct, BigDecimal offeredValue, EventType type) {
        this.idProduct = idProduct;
        this.value = offeredValue;
        this.type = type;
        this.offer = offer;
    }
     
    public int getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }


    public BigDecimal getOfferedValue() {
        return value;
    }

    public void setOfferedValue(BigDecimal offeredValue) {
        this.value = offeredValue;
    }

    public RequirementNode getProduct() {
        return product;
    }

    public void setProduct(RequirementNode product) {
        this.product = product;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }
    */

    @Override
    public int compareTo(Event o) {
        return this.getLast_modified().compareTo(o.getLast_modified());
    }
}
