/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.adresa.eaukcia.core.service.impl;

import sk.adresa.eaukcia.core.dao.AuctionLogDao;
import sk.adresa.eaukcia.core.data.AuctionEvent;
import sk.adresa.eaukcia.core.data.filter.AuctionEventFilter;
import sk.adresa.eaukcia.core.exception.EaukciaDataIntegrityViolationException;
import sk.adresa.eaukcia.core.query.PaginatedList;
import sk.adresa.eaukcia.core.query.Paging;
import sk.adresa.eaukcia.core.service.AuctionLogService;
import sk.adresa.eaukcia.core.util.Assert;

/**
 *
 * @author juraj
 */
public class AuctionLogServiceImpl implements AuctionLogService{
    private AuctionLogDao auctionLogDao;

    public AuctionLogServiceImpl(AuctionLogDao auctionLogDao){
        Assert.isNotNull(auctionLogDao, "auctionLogDao");
        this.auctionLogDao = auctionLogDao;
    }


    @Override
    public AuctionEvent getAuctionLog(int auctionLogId)   {
        Assert.isNotNegativeOrZero(auctionLogId, "auctionLogId");
        return auctionLogDao.getAuctionLog(auctionLogId);
    }

    @Override
    public PaginatedList<AuctionEvent> getFilteredAuctionLogs(AuctionEventFilter filter, Paging paging)  {
        Assert.isNotNull(paging, "paging");
        return auctionLogDao.getFilteredAuctionLogs(filter,paging);
    }
}
