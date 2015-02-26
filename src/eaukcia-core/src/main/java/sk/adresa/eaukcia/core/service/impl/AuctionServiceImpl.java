/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.adresa.eaukcia.core.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import sk.adresa.eaukcia.core.dao.AuctionDao;
import sk.adresa.eaukcia.core.dao.impl.AbstractDao;
import sk.adresa.eaukcia.core.data.Auction;
import sk.adresa.eaukcia.core.data.BidForItem;
import sk.adresa.eaukcia.core.data.Event;
import sk.adresa.eaukcia.core.data.RequirementNode;
import sk.adresa.eaukcia.core.data.filter.AuctionFilter;
import sk.adresa.eaukcia.core.query.PaginatedList;
import sk.adresa.eaukcia.core.query.Paging;
import sk.adresa.eaukcia.core.service.AuctionService;
import sk.adresa.eaukcia.core.util.Assert;
import sk.adresa.eaukcia.core.util.Util;

/**
 *
 * @author juraj
 */
public class AuctionServiceImpl extends AbstractDao implements AuctionService {

    private AuctionDao auctionDao;

    public AuctionServiceImpl(AuctionDao auctionDao){
        Assert.isNotNull(auctionDao, "auctionDao");
        this.auctionDao = auctionDao;
    }
    
    
    @Override
    public Auction getAuction(int auctionId) {
        Assert.isNotNegativeOrZero(auctionId, "auctionId");
        return auctionDao.getAuction(auctionId);
    }

    @Override
    public PaginatedList<Auction> getFilteredAuctions(AuctionFilter filter, Paging paging) {
        Assert.isNotNull(paging, "paging");
        return null;
    }
    
    @Override
    public ArrayList<HashMap<Integer,BidForItem>>  getBids(){
        return this.auctionDao.getAllBids();
    } 
            
    @Override
    public HashMap<Integer, RequirementNode> getAuctionHashMap(){
       //this.auctionDao.getFilteredAuctions(null, null);
        
       List<Event> events = this.auctionDao.getAllEvents();
       List<RequirementNode> requirments =  this.auctionDao.getAllRequirementForAuction();
       HashMap<Integer, ArrayList<Event>> nodeEvents = offersEventToMap(events);
       HashMap<Integer, RequirementNode> nodes = getRequirementHashpMap(requirments);
       makeDependency(nodes);
       addOffersToNodes(nodes, nodeEvents);
       
       return nodes;
    }
    /**
     *  add eventOffers to appropiate products
     * 
     * @param nodes
     * @param nodeEvents 
     */
    private void addOffersToNodes(HashMap<Integer, RequirementNode> nodes , HashMap<Integer, ArrayList<Event>> nodeEvents){
        for (Integer key : nodeEvents.keySet()) {
            ArrayList<Event> offerEvents = nodeEvents.get(key);
            RequirementNode node = nodes.get(key);
            node.setOfferEvents(offerEvents);
       }
    }
    
    /**
     * Add offer Events to hasp map where key is id of product and values are
     * arraylist of event
     *
     * @param events
     * @return
     */
    private static HashMap<Integer, ArrayList<Event>> offersEventToMap(List<Event> events) {
        HashMap<Integer, ArrayList<Event>> result = new HashMap<Integer, ArrayList<Event>>();
        for (Event event : events) {
            Integer idProduct = event.getItem_id();
            if (result.get(idProduct) == null) {
                result.put(idProduct, new ArrayList<Event>());
            }
            result.get(idProduct).add(event);
        }
        return result;
    }

    /**
     * Make children/parent dependency between nodes
     *
     * @param nodes
     */
    private static void makeDependency(HashMap<Integer, RequirementNode> nodes) {
        for (Integer key : nodes.keySet()) {
            RequirementNode node = nodes.get(key);
            Integer parentId = node.getFk_parent();
            RequirementNode parentNode = nodes.get(parentId);
            node.setParent(parentNode);
            parentNode.addChildren(node);
        }
    }

    /**
     *
     * @param listOfNode
     * @return
     */
    private static HashMap<Integer, RequirementNode> getRequirementHashpMap(List<RequirementNode> listOfNode) {
        HashMap<Integer, RequirementNode> result = new HashMap<Integer, RequirementNode>();
        for (RequirementNode node : listOfNode) {
            result.put(node.getItem_id(), node);
        }
        return result;
    }

    
    

}
