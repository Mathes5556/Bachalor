package sk.adresa.eaukcia.core.dao.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.math.BigDecimal;
import java.math.BigInteger;
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
import sk.adresa.eaukcia.core.data.BidForItem;
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
         //AuctionEvent e = (AuctionEvent) sqlSession.selectOne(DEFAULT_PREFIX2 + "getAuctionLog", 519);
         //String[][] a = e.getJSONValues();
         return events;
    }
    
    @Override
    public List<RequirementNode> getAllRequirementForAuction(){
         List<RequirementNode> requirments = sqlSession.selectList(DEFAULT_PREFIX2 + "getRequirments");
         //ArrayList<ArrayList<BidForItem>> a = getAllBids();
         return requirments;
    }
    
    
    @Override
    public ArrayList<HashMap<Integer,BidForItem>>  getAllBids() {
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
       
       
       
        AuctionLogDaoImpl auctionLog = new AuctionLogDaoImpl();
        auctionLog.setSqlSession(sqlSession);
        ArrayList<AuctionEvent> JSONoffers = new ArrayList<AuctionEvent>();
        
        for (int i = 1; i <= 631; i++) {
            AuctionEvent auctionEvent = auctionLog.getAuctionLog(i);
            if (auctionEvent == null) {
                continue;
            }
            if (auctionEvent.getAction().equals("setCriterionsForUser")) {   
                System.out.println(auctionEvent.getValue());
                JSONoffers.add(auctionEvent);
            }
            
        }
        
        Deque queue = new ArrayDeque();
        
        HashMap<Integer, RequirementNode> requirementNodes = new HashMap<Integer, RequirementNode>();
        
        ArrayList<Offer> offers = new ArrayList<Offer>();
        ArrayList<HashMap<Integer,BidForItem>> allBids = new ArrayList<HashMap<Integer,BidForItem>>();
        //login of user is key
        HashMap<String, User> usersParticipateInAction = new HashMap<String, User>();
        
        for (AuctionEvent offer : JSONoffers) {
            if(!usersParticipateInAction.containsKey(offer.getUser().getLogin())){
                usersParticipateInAction.put(offer.getUser().getLogin(), offer.getUser());
            }
           
            
            if(offer.getUser().getLogin().equals("admin")){
                continue;
            }
            HashMap<Integer,BidForItem> bidsInOneOffer = new  HashMap<Integer,BidForItem>();
            System.out.print("===");
          
            try{
            //dekodovat s GSONOM
            JsonElement jelement = new JsonParser().parse(offer.getValue());
            JsonObject  jobject = jelement.getAsJsonObject();
            JsonArray jarray = jobject.getAsJsonArray("items");
            Object typeOfAction = jobject.get("nameInAuction");
            if (typeOfAction != null &&  typeOfAction.toString().equals("Záruka")){
                continue;
            }
            
            for(JsonElement i : jarray){
                
                JsonObject  obj = i.getAsJsonObject();
                JsonArray childrens = obj.getAsJsonArray("children");
                String price = obj.get("integerValue").toString();
                String itemID = obj.get("itemId").toString();
                BidForItem bid = new BidForItem(new Integer(itemID), null, offer.getTargetUserId(),new BigDecimal(price), offer.getTime(), offer.getUser());
                bidsInOneOffer.put(new Integer(itemID), bid);
                System.out.print(price + " => " + itemID );
                for(JsonElement children : childrens){
                    obj = children.getAsJsonObject();
                    JsonArray childrens2 = obj.getAsJsonArray("children");
                    price = obj.get("integerValue").toString();
                    itemID = obj.get("itemId").toString();
                    bid = new BidForItem(new Integer(itemID), null, offer.getTargetUserId(),new BigDecimal(price), offer.getTime(), offer.getUser());
                    bidsInOneOffer.put(new Integer(itemID), bid);
                    //BidForItem bid = new BidForItem(new Integer(itemID), null, offer.getTargetUserId(),new BigDecimal(price), offer.getTime());
                    System.out.print(price + " => " + itemID );
                    for(JsonElement child : childrens2){
                        obj = child.getAsJsonObject();
                        JsonArray childrens3 = obj.getAsJsonArray("children");
                        if (childrens.size() != 0) {
                             System.out.print("doplnit uroven");
                        }
                        price = obj.get("integerValue").toString();
                        itemID = obj.get("itemId").toString();
                        bid = new BidForItem(new Integer(itemID), null, offer.getTargetUserId(),new BigDecimal(price), offer.getTime(), offer.getUser());
                        bidsInOneOffer.put(new Integer(itemID), bid);
                        //BidForItem bid = new BidForItem(new Integer(itemID), null, offer.getTargetUserId(),new BigDecimal(price), offer.getTime());
                        System.out.print(price + " => " + itemID );
                        
                        
                         for(JsonElement childr : childrens3){
                            obj = childr.getAsJsonObject();
                            JsonArray childrens4 = obj.getAsJsonArray("children");
                            if (childrens4.size() != 0) {
                                 System.out.print("doplnit uroven");
                            }
                            price = obj.get("integerValue").toString();
                            itemID = obj.get("itemId").toString();
                            bid = new BidForItem(new Integer(itemID), null, offer.getTargetUserId(),new BigDecimal(price), offer.getTime(), offer.getUser());
                            bidsInOneOffer.put(new Integer(itemID), bid);
                            //BidForItem bid = new BidForItem(new Integer(itemID), null, offer.getTargetUserId(),new BigDecimal(price), offer.getTime());
                            System.out.print(price + " => " + itemID );

                        }
                    }
                }
                System.out.print("==+++++++++++++++++++++++++++++=");
            }
            }
            catch(Exception e){
                
                continue;
            }
            finally{
                allBids.add(bidsInOneOffer);
            }
            continue;

            
            
            
            
            
            
            //vzdy novy bid 
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
                //String auctionName = jsonObj.getString("nameInAuction");
                JSONArray items = jsonObj.getJSONArray("items");
                for (int i = 0; i < items.length(); i++) {
                    JSONObject node = items.getJSONObject(i);
                    queue.add(node);
                }                
            } catch (JSONException ex) {
                Logger.getLogger(AuctionDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
                continue;
            }
            
            while (!queue.isEmpty()) { // while is there any items
                Deque nodes = new ArrayDeque();
                JSONObject item = (JSONObject) queue.pollFirst(); // vztiahnutie prveho z que
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
                            //Event event = new Event(off, idNode, new BigDecimal(newValue), EventType.MAKE_OFFER);
                            //off.addEvent(event);   
                            
                            //System.out.print(offer.getTime());
                            BidForItem bid = new BidForItem(idNode, null, offer.getTargetUserId(),new BigDecimal(newValue), offer.getTime(), null);
                            bidsInOneOffer.put(new Integer(idNode), bid);
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
        System.out.print("-----------");
        
        
        
     
        return allBids;
        /*
        BigDecimal tmp = BigDecimal.ZERO;
        for(Offer o : offers){
            System.out.print(o.getSumOfOffer());
            System.out.print("userom " + o.getUser().getLogin());
            System.out.print("zmensilo sa o " + tmp.subtract(o.getSumOfOffer()).toString() + " v case " + o.getDate().toString());
            tmp = o.getSumOfOffer();
            System.out.print("-----------");
            
        }       
        
        int totalRows = (Integer) sqlSession.selectOne(DEFAULT_PREFIX + "countFilteredAuctions", filter);
        Paging pp = new Paging(paging.getRowsPerPage(), paging.getActualPage(), totalRows);
        RowBounds bounds = new RowBounds(pp.getOffset(), pp.getRowsPerPage());
        List auctions = sqlSession.selectList(DEFAULT_PREFIX + "getFilteredAuctions", filter, bounds);
        return new PaginatedList<Auction>(auctions, pp);
        */
        
       // return null;
    }
}
