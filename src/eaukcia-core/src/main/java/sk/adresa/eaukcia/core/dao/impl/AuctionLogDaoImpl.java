package sk.adresa.eaukcia.core.dao.impl;

import java.util.List;

import org.apache.ibatis.session.RowBounds;

import sk.adresa.eaukcia.core.dao.AuctionLogDao;
import sk.adresa.eaukcia.core.data.AuctionEvent;
import sk.adresa.eaukcia.core.query.Paging;
import sk.adresa.eaukcia.core.data.filter.AuctionEventFilter;
import sk.adresa.eaukcia.core.query.PaginatedList;

public class AuctionLogDaoImpl extends AbstractDao implements AuctionLogDao{
    private static final String DEFAULT_PREFIX = "sk.adresa.eaukcia.core.dao.impl.AuctionLogMapper.";
    
    public AuctionLogDaoImpl() {
    }

    @Override
    public AuctionEvent getAuctionLog(int auctionLogId) {
        Object object = sqlSession.selectOne(DEFAULT_PREFIX + "getAuctionLog", auctionLogId);
        return object instanceof AuctionEvent ? ((AuctionEvent) object) : null;
        
    }

    @Override
    public PaginatedList<AuctionEvent> getFilteredAuctionLogs(AuctionEventFilter filter, Paging paging) {
        
        int totalRows = (Integer)sqlSession.selectOne(DEFAULT_PREFIX + "countFilteredAuctionLogs",filter);
        Paging pp = new Paging(paging.getRowsPerPage(), paging.getActualPage(), totalRows);
        RowBounds bounds = new RowBounds(pp.getOffset(), pp.getRowsPerPage());
        List auctionEvents = sqlSession.selectList(DEFAULT_PREFIX + "getFilteredAuctionLogs",filter,bounds);
        return new PaginatedList<AuctionEvent>(auctionEvents, pp);
        
    }
}
