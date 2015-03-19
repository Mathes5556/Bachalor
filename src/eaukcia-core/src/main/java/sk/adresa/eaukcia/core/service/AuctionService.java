/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.adresa.eaukcia.core.service;

import java.util.ArrayList;
import java.util.HashMap;
import sk.adresa.eaukcia.core.data.Auction;
import sk.adresa.eaukcia.core.data.BidForItem;
import sk.adresa.eaukcia.core.data.RequirementNode;
import sk.adresa.eaukcia.core.data.User;
import sk.adresa.eaukcia.core.data.filter.AuctionFilter;
import sk.adresa.eaukcia.core.exception.EaukciaObjectNotFoundException;
import sk.adresa.eaukcia.core.query.PaginatedList;
import sk.adresa.eaukcia.core.query.Paging;

/**
 * Provides service for auction table.
 * @author Mathes
 */
public interface AuctionService {

    /**
     * Gets auction(only top most properties)
     * @param auctionId Id of the auction to get
     * @return Auction with selected id or null
     * @throws EaukciaObjectNotFoundException 
     */
    public Auction getAuction(int auctionId);

    /**
     * Return filtered auctions
     * @param filter Filter for auctions
     * @param paging Paging object
     * @return Filtered auction
     */
    public PaginatedList<Auction> getFilteredAuctions(AuctionFilter filter, Paging paging);
    
    /**
     * Load from DB all information about auction and get them into logic structure
     * 
     * @return 
     */
    public HashMap<Integer, RequirementNode> getAuctionHashMap();
    
    /**
     * get all bids for auction fom DB
     * @return 
     */
    public ArrayList<HashMap<Integer,BidForItem>>  getBids();
    
    /**
     * get hashmap of users this method can be call after caling getBids !!!
     * @return 
     */
    public HashMap<String, User> getUsers();
    
    /**
     * get name of auction currently loaded id DB 
     * @return 
     */
    public String getNameOfAuction();
    
}

