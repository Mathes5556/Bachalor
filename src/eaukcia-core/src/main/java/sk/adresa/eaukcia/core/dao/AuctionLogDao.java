package sk.adresa.eaukcia.core.dao;


import sk.adresa.eaukcia.core.data.AuctionEvent;
import sk.adresa.eaukcia.core.query.Paging;
import sk.adresa.eaukcia.core.data.filter.AuctionEventFilter;

import sk.adresa.eaukcia.core.query.PaginatedList;

public interface AuctionLogDao {
    public AuctionEvent getAuctionLog(int auctionLogId);
    public PaginatedList<AuctionEvent> getFilteredAuctionLogs(AuctionEventFilter filter,Paging paging);
    
}
