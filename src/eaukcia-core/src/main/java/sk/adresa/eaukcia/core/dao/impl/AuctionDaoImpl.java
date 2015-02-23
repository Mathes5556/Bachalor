package sk.adresa.eaukcia.core.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext ;


import org.apache.ibatis.session.RowBounds;

import sk.adresa.eaukcia.core.dao.AuctionDao;
import sk.adresa.eaukcia.core.data.Auction;
import sk.adresa.eaukcia.core.data.AuctionEvent;
import sk.adresa.eaukcia.core.query.Paging;
import sk.adresa.eaukcia.core.data.filter.AuctionFilter;
import sk.adresa.eaukcia.core.query.PaginatedList;


import org.json.*;
import org.springframework.web.jsf.FacesContextUtils;
import sk.adresa.eaukcia.core.data.Event;
import sk.adresa.eaukcia.core.data.EventType;
import sk.adresa.eaukcia.core.data.Offer;
import sk.adresa.eaukcia.core.data.RequirementNode;
import sk.adresa.eaukcia.core.data.User;
import sk.adresa.eaukcia.core.service.impl.AuctionServiceImpl;
import sk.adresa.eaukcia.core.util.Util;

public class AuctionDaoImpl extends AbstractDao implements AuctionDao {
    
    private static final String DEFAULT_PREFIX = "sk.adresa.eaukcia.core.dao.impl.AuctionMapper.";
    private static final String DEFAULT_PREFIX2 = "sk.adresa.eaukcia.core.dao.impl.AuctionLogMapper.";
    
    public AuctionDaoImpl() {
    }
    
    @Override
    public Auction getAuction(int auctionId) {
        Object object = sqlSession.selectOne(DEFAULT_PREFIX + "getAuction", auctionId);
        return object instanceof Auction ? ((Auction) object) : null;
    }
    final public static String ADD_CRITERION = "addCriterionItemToCriterion";
    final public static String ID_ITEM = "itemId";
    final public static String NAME = "name";
    final public static String AMOUNT = "amount";
    final public static String NEW_VALUE = "valueSupport";
    final public static String OLD_VALUE = "oldValueSupport";
    final public static String PARENT_ITEM_ID = "parentItemId";
    final public static String UNIT_TYPE = "unitType";
    final public static String CODE = "code";
    
    @Override
    public List<Event> getAllEvents(){
         List<Event> events = sqlSession.selectList(DEFAULT_PREFIX2 + "getAuctionLogs");
         AuctionEvent e = (AuctionEvent) sqlSession.selectOne(DEFAULT_PREFIX2 + "getAuctionLog", 1);
         return events;
    }
    
    @Override
    public List<RequirementNode> getAllRequirementForAuction(){
         List<RequirementNode> requirments = sqlSession.selectList(DEFAULT_PREFIX2 + "getRequirments");
         return requirments;
    }
    
    @Override
    public PaginatedList<Auction> getFilteredAuctions(AuctionFilter filter, Paging paging) {
       /*
        FacesContext context = FacesContext.getCurrentInstance();
       Map<String,String> getParams = context.getExternalContext().getRequestParameterMap();
       
       List<Event> events = sqlSession.selectList(DEFAULT_PREFIX2 + "getAuctionLogs");
       HashMap<Integer, ArrayList<Event>> nodeEvents = AuctionServiceImpl.offersEventToMap(events);
       
       //dam events do offerov
       
       
       List<RequirementNode> requirments = sqlSession.selectList(DEFAULT_PREFIX2 + "getRequirments");
       HashMap<Integer, RequirementNode> nodes = AuctionServiceImpl.getRequirementHashpMap(requirments);
       AuctionServiceImpl.makeDependency(nodes);
       
       //add eventOffers to appropiate products
        //priradim pre kazdy node vseky ponuky
       for (Integer key : nodeEvents.keySet()) {
            ArrayList<Event> offerEvents = nodeEvents.get(key);
            RequirementNode node = nodes.get(key);
            node.setOfferEvents(offerEvents);
       }
       
      
       */
       /*
       
       
        AuctionLogDaoImpl auctionLog = new AuctionLogDaoImpl();
        auctionLog.setSqlSession(sqlSession);
        System.out.print("faak");
        
        
        ArrayList<AuctionEvent> JSONoffers = new ArrayList<AuctionEvent>();
        
        for (int i = 1; i < 520; i++) {
            AuctionEvent auctionEvent = auctionLog.getAuctionLog(i);
            if (auctionEvent == null) {
                continue;
            }
            if (!auctionEvent.getAction().equals(ADD_CRITERION)) {
                System.out.println("------");
                System.out.println("------");
                System.out.println("------");
                System.out.println("------");
                
                System.out.println(auctionEvent.getValue());
                JSONoffers.add(auctionEvent);
            }
            
        }
        
        
        
        Deque queue = new ArrayDeque();
        /*
        HashMap<Integer, RequirementNode> requirementNodes = new HashMap<Integer, RequirementNode>();
        //TODO naplnenie z DB
        
        
        AuctionEvent auctionEvent = auctionLog.getAuctionLog(400);
        //.out.println("faak");
        //System.out.print(auctionEvent.getValue());
        String jsonAuction = auctionEvent.getValue();



        //for(JSONObject jsonObj : jsons){ //one jsnObj is one offer
        
        ArrayList<Offer> offers = new ArrayList<Offer>();
        
        for (AuctionEvent offer : JSONoffers) {
            if (offer == null) {
                continue;
            }
            if(offer.getValue() == null){
                continue;
            }
            Offer off = new Offer(offer.getId(), offer.getUser(), offer.getTime());
            offers.add(off);
            try {
                JSONObject jsonObj = new JSONObject(offer.getValue());
                String auctionName = jsonObj.getString("nameInAuction");
                JSONArray items = jsonObj.getJSONArray("items");
                for (int i = 0; i < items.length(); i++) {
                    JSONObject node = items.getJSONObject(i);
                    queue.add(node);
                    
                }
                //System.out.print("faak2");
                //System.out.print(auctionName);                
            } catch (JSONException ex) {
                Logger.getLogger(AuctionDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
                continue;
            }
            
            while (!queue.isEmpty()) { // while is there any items
                Deque nodes = new ArrayDeque();
                JSONObject item = (JSONObject) queue.pollFirst();
                nodes.addFirst(item);
                while (!nodes.isEmpty()) {
                    JSONObject node = (JSONObject) nodes.pollFirst();
                    try {
                        JSONArray childrens = node.getJSONArray("children");
                        if (childrens.length() == 0) {  // is  leaf 
                            String id = node.getString(ID_ITEM);
                            String name = node.getString(NAME);
                            String amount = node.getString(AMOUNT);                            
                            String newValue = node.getString(NEW_VALUE);
                            String oldValue = node.getString(OLD_VALUE);
                            String parentId = node.getString(PARENT_ITEM_ID);
                            String unitType = node.getString(UNIT_TYPE);
                            String codeItem = node.getString(CODE);
                            
                            int idNode = new Integer(id);

                            //RequirementNode product = requirementNodes.get(idNode);
                            Event event = new Event(off, idNode, new BigDecimal(oldValue), new BigDecimal(newValue), EventType.MAKE_OFFER);
                            off.addEvent(event);     
                        } else { // NOT leaf
                            for (int i = 0; i < childrens.length(); i++) {
                                JSONObject hierachicalNode = childrens.getJSONObject(i);
                                // davat do nodu nie que
                                nodes.add(hierachicalNode);
                            }
                        }
                    } catch (JSONException ex) {
                        Logger.getLogger(AuctionDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            
        }
        BigDecimal tmp = BigDecimal.ZERO;
        for(Offer o : offers){
            System.out.print(o.getSumOfOffer());
            System.out.print("userom " + o.getUser().getLogin());
            System.out.print("zmensilo sa o " + tmp.subtract(o.getSumOfOffer()).toString() + " v case " + o.getDate().toString());
            tmp = o.getSumOfOffer();
            System.out.print("-----------");
            
        }       
        */
        int totalRows = (Integer) sqlSession.selectOne(DEFAULT_PREFIX + "countFilteredAuctions", filter);
        Paging pp = new Paging(paging.getRowsPerPage(), paging.getActualPage(), totalRows);
        RowBounds bounds = new RowBounds(pp.getOffset(), pp.getRowsPerPage());
        List auctions = sqlSession.selectList(DEFAULT_PREFIX + "getFilteredAuctions", filter, bounds);
        return new PaginatedList<Auction>(auctions, pp);
        
    }
}
