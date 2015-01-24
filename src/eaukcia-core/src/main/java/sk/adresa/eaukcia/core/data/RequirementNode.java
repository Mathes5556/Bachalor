/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.adresa.eaukcia.core.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 *
 * @author mathes
 */
public class RequirementNode {
    
    private Integer item_id;
    
    private String unit;
    
    private String code;
    
    private String name;
    
    private String unit_Type;
    
    private int fk_parent;
    
    private  int order_no;
    
    private RequirementNode parent;
     
    private ArrayList<RequirementNode> childrens = new ArrayList<RequirementNode>();
    
    private ArrayList<RequirementNode> allParents = new ArrayList<RequirementNode>();
    
    private ArrayList<Event> offerEvents = new ArrayList<Event>();
    
    public void addOfferEvent(Event event){
        this.offerEvents.add(event);
    }
    /**
     * get offer for event , but ordening by value
     * 
     * @return 
     */
    public ArrayList<Event> getOfferEvents() {
        Collections.sort(offerEvents);
        return offerEvents;
    }

    public void setOfferEvents(ArrayList<Event> offerEvents) {
        this.offerEvents = offerEvents;
    }
    
    public RequirementNode getParent() {
        return parent;
    }

    public void setParent(RequirementNode parent) {
        this.parent = parent;
    }


    
    public void addChildren(RequirementNode node){
        this.childrens.add(node);
    }
    
    
    public Integer getItem_id() {
        return item_id;
    }

    public void setItem_id(Integer item_id) {
        this.item_id = item_id;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit_Type() {
        return unit_Type;
    }

    public void setUnit_Type(String unit_Type) {
        this.unit_Type = unit_Type;
    }

    public int getFk_parent() {
        return fk_parent;
    }

    public void setFk_parent(int fk_parent) {
        this.fk_parent = fk_parent;
    }

    public int getOrder_no() {
        return order_no;
    }

    public void setOrder_no(int order_no) {
        this.order_no = order_no;
    }

    public ArrayList<RequirementNode> getChildrens() {
        return childrens;
    }

    public void setChildrens(ArrayList<RequirementNode> childrens) {
        this.childrens = childrens;
    }
    
    
}
