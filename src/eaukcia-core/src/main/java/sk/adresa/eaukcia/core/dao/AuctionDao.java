package sk.adresa.eaukcia.core.dao;



import java.util.List;
import sk.adresa.eaukcia.core.data.Auction;
import sk.adresa.eaukcia.core.data.Event;
import sk.adresa.eaukcia.core.data.RequirementNode;
import sk.adresa.eaukcia.core.query.Paging;
import sk.adresa.eaukcia.core.data.filter.AuctionFilter;

import sk.adresa.eaukcia.core.query.PaginatedList;


public interface AuctionDao {
   
    public Auction getAuction(int auctionId);
    public PaginatedList<Auction> getFilteredAuctions(AuctionFilter filter, Paging paging);
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
