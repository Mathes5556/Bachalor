/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.adresa.eaukcia.core.service;

import sk.adresa.eaukcia.core.data.AuctionEvent;
import sk.adresa.eaukcia.core.data.filter.AuctionEventFilter;
import sk.adresa.eaukcia.core.query.PaginatedList;
import sk.adresa.eaukcia.core.query.Paging;

/**
 *
 * @author juraj
 */
public interface AuctionLogService {

    /**
     * Gets a log.
     * @param auctionLogId
     * @return
     */
    public AuctionEvent getAuctionLog(int auctionLogId);

    /** 
     * Gets filtered logs.
     * @param filter
     * @param paging
     * @return
     */
    public PaginatedList<AuctionEvent> getFilteredAuctionLogs(AuctionEventFilter filter, Paging paging);
}
