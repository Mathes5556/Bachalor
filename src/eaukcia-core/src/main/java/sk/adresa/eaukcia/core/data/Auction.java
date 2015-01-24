package sk.adresa.eaukcia.core.data;

import java.util.List;

import net.sf.json.JSONObject;
import org.hibernate.validator.Length;


import org.hibernate.validator.Range;

import sk.adresa.eaukcia.core.util.Assert;
import sk.adresa.eaukcia.core.util.Util;

public class Auction implements LoggableObject {

    public static final String AUCTION_STATUS_RUNNING = "RUNNING";
    public static final String AUCTION_STATUS_BLOCKED = "BLOCKED";
    public static final String AUCTION_STATUS_FINISHED = "FINISHED";
    public static final String AUCTION_STATUS_WAITING = "WAITING";
    
    public static final String AUCTION_TYPE_PRIVATE = "PRIVATE";
    public static final String AUCTION_TYPE_PUBLIC = "PUBLIC";
    public static final String AUCTION_TYPE_TRAINING = "TRAINING";
    
    private int id;
    @Length(min = 3, max = 100, message = "{incorrectLength}: {min}..{max}")
    private String name;
    @Length(max = 4000, message = "{incorrectMaxLength} {max}")
    private String description;
    private String auctionType, auctionStatus, currency;
    private int activeRound;
    @Range(min = 3, message = "{incorrectMinimalLength} {min}")
    private double itemCount, pricePerUnit;
    
    
    public Auction() {
    }

    public Auction(int id) {
        this.id = id;
    }
    
    /**
     * Creates shallow copy of template
     * @param template 
     */
    public Auction(Auction template){
        Assert.isNotNull(template, "template");
        
        id = template.getId();
        name = template.getName();
        description = template.getDescription();
        auctionType = template.getAuctionType();
        auctionStatus = template.getAuctionStatus();
        currency = template.getCurrency();
        activeRound = template.getActiveRound();
        itemCount = template.getActiveRound();
        pricePerUnit = template.getPricePerUnit();
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setAuctionType(String auctionType) {
        this.auctionType = auctionType;
    }

    public String getAuctionType() {
        return auctionType;
    }

    public void setAuctionStatus(String auctionStatus) {
        this.auctionStatus = auctionStatus;
    }

    public String getAuctionStatus() {
        return auctionStatus;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setActiveRound(int activeRound) {
        this.activeRound = activeRound;
    }

    public int getActiveRound() {
        return activeRound;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public void setItemCount(double itemCount) {
        this.itemCount = itemCount;
    }

    public double getItemCount() {
        return itemCount;
    }

    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return "\nObject Auction:{ "
                + "\n  id:" + getId()
                + "\n  name:" + getName()
                + "\n  status:" + getAuctionStatus()
                + "\n  description:" + getDescription()
                + "\n  activeRound:" + getActiveRound()
                + "\n  itemCount:" + getItemCount()
                + "\n  pricePerUnit:" + getPricePerUnit()
                + "\n  auctionType:" + getAuctionType()
                + "\n  currency:" + getCurrency()
                + "\n}";
    }
    
    @Override
    public String toLogString() {
        //TODO if root Item it throws an error beacuse root has parent root with the same id (json has problems with it)
        // root shouldnt be the auction item
        JSONObject jsonAuc = JSONObject.fromObject(this);
        return jsonAuc.toString();
    }
}
