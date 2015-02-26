package sk.adresa.eaukcia.core.dao;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import sk.adresa.eaukcia.core.data.Auction;
import sk.adresa.eaukcia.core.data.BidForItem;
import sk.adresa.eaukcia.core.data.Event;
import sk.adresa.eaukcia.core.data.RequirementNode;
import sk.adresa.eaukcia.core.query.Paging;
import sk.adresa.eaukcia.core.data.filter.AuctionFilter;

import sk.adresa.eaukcia.core.query.PaginatedList;


public interface AuctionDao {
   
    public Auction getAuction(int auctionId);
    
    /**
     *  all bids for all log from auction_log table
     * @return 
     */
    public ArrayList<HashMap<Integer,BidForItem>> getAllBids();
    /**
     *  Get all offer events in auction
     * 
     * @return 
     */
    public List<Event> getAllEvents();
    
    /**
     * Get all requirement in auction
     * 
     * @return 
     */
    public List<RequirementNode> getAllRequirementForAuction();
}
