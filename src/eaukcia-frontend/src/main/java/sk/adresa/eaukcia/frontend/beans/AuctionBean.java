package sk.adresa.eaukcia.frontend.beans;


import com.google.gson.Gson;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javassist.bytecode.analysis.Util;
import javax.annotation.PostConstruct;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import net.sf.json.JSON;


import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.json.JSONObject;

import org.richfaces.component.UIDataTable;
import org.richfaces.component.html.HtmlDataTable;
import org.richfaces.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import sk.adresa.eaukcia.core.dao.impl.AuctionDaoImpl;
import sk.adresa.eaukcia.core.dao.impl.AuctionLogDaoImpl;

import sk.adresa.eaukcia.core.data.Auction;
import sk.adresa.eaukcia.core.data.AuctionEvent;
import sk.adresa.eaukcia.core.data.BidEvent;
import sk.adresa.eaukcia.core.data.BidForItem;
import sk.adresa.eaukcia.core.data.Event;
import sk.adresa.eaukcia.core.data.RequirementNode;
import sk.adresa.eaukcia.core.query.Paging;
import sk.adresa.eaukcia.core.data.filter.AuctionFilter;
import sk.adresa.eaukcia.core.query.PaginatedList;
import sk.adresa.eaukcia.core.service.AuctionService;
import sk.adresa.eaukcia.frontend.uidata.UiAuction;

@Component
public class AuctionBean {
    
    //@Autowired
    //@Qualifier("sessionFactory")
    //private SessionFactory sessionFactory;
    
     
    //@Autowired
   // private ApplicationContext appContext;
    String addedId;

    public String getAddedId() {
        return addedId;
    }

    public void setAddedId(String addedId) {
        this.addedId = addedId;
    }
    private Auction auction = new Auction();
    private int scrollerPage;
    private List<UiAuction> uiAuctions;
    private AuctionService auctionService;
    static Logger logger = Logger.getLogger(AuctionBean.class);
    private AuctionLogBean auctionLogBean;
    private UIDataTable table;
    private HashMap<Integer, RequirementNode> productNodes; 
    
    @PostConstruct
    public void initBean(){
        //AuctionLogDaoImpl log = new AuctionLogDaoImpl();
        //AuctionEvent event = log.getAuctionLog(55);
        this.productNodes = this.auctionService.getAuctionHashMap();
        ArrayList<HashMap<Integer,BidForItem>>  allBids = this.auctionService.getBids();
        
        //doplnim requirment nody o bidy 
        for (Map.Entry<Integer, RequirementNode> entry : productNodes.entrySet()) {
            Integer id = entry.getKey();
            RequirementNode requirementNode = entry.getValue();
            for (HashMap<Integer,BidForItem> bidsInOneOffer: allBids){
                if(bidsInOneOffer.get(id) == null){//v tomto bide nebol zlepseny dany requirementNode pouziem stary offer
                    Event lastOfferEvent = requirementNode.getOfferEvents().get(requirementNode.getOfferEvents().size()-1);
                    requirementNode.addOfferEvent(lastOfferEvent);
                }
                else{
                     BidForItem bid = bidsInOneOffer.get(id);
                     BidEvent newEvent = new BidEvent(bid.getItem_id(), bid.getNumeric_value(), bid.getFk_user(), bid.getTime());
                     requirementNode.addOfferEvent(newEvent);
                }
            }
        }

        // ulozim zmeny v bidoch <integerBid, newValue>  -> arraylist of Event !!!!!
        // prejdem cely productNodes ak jeho id(key) v key of bidov
            //tak pridaj, inak nakopriuj(nemenilo sa)
        
    }
    
    public void listener(AjaxBehaviorEvent event) {
        System.out.println("listener");
        String result = "called by " + event.getComponent().getClass().getName();
    }
    
    public void setAuctionService(AuctionService auctionService) {
        this.auctionService = auctionService;
    }
    
    final public static String ID = "id";
    
    private String getParamValue(String value){
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String,String> getParams = context.getExternalContext().getRequestParameterMap();
        return getParams.get(value);
    }
    public ArrayList<Integer> getIds(){
        ArrayList<Integer> result = new ArrayList<Integer> ();
        String value =  this.getParamValue(ID);
        if(value == null){
            result.add(new Integer(0));
            return result;
        }
        if(value.contains(",")){  //is there more ids
            String[] ids = value.split(",");
            for (int i = 0; i < ids.length; i++) {
                result.add(new Integer(ids[i]));
            }
        }
        else{
            Integer oneId = value == null ? null : new Integer(value);
            result.add(oneId);
        }
        
        return result;        
    }
    
    final public static String NAME = "name";
    
    final public static String CHILDRENS = "children";
    
    final public static int MAX_LEVEL_FOR_TREE = 1;
    /**
     *  get hierarchy JSON object for visualization into tree
     * 
     * @param rootID
     * @return 
     */
    public String getHierarchyForTree(){
        HashMap<String, Object> result =  new HashMap<String, Object>();
        
        //
        RequirementNode node = this.productNodes.get(new Integer(this.getIds().get(0)));
        
        Double bestValue = node.getLastOffer();
       // String showingText = new StringBuilder()
         //                       .append(node.getName())
           //                     .append("  ")
             //                   .append(bestValue.toString()).toString();
        result.put(NAME, node.getName());
        
        ArrayList<HashMap<String, Object>> childrens = new ArrayList<HashMap<String, Object>>();
        for(RequirementNode children :  node.getChildrens()){
                // only for one in comment 
            //HashMap<String, Object> childrenHashMap = new HashMap<String, Object>();
            //childrenHashMap.put(NAME, children.getName());
            
            
            HashMap<String, Object> childrenHashMap = this.getChildrenHashMap(children, MAX_LEVEL_FOR_TREE);
            
            childrens.add(childrenHashMap);
        }
        
        result.put(CHILDRENS, childrens);
        
        JSONObject json = new JSONObject(result);
        return json.toString();
    }
    
    private HashMap<String, Object> getChildrenHashMap(RequirementNode node, int level){
        HashMap<String, Object> result =  new HashMap<String, Object>();
        result.put(NAME, node.getName());
        ArrayList<HashMap<String, Object>> childrens = new ArrayList<HashMap<String, Object>>();
        for(RequirementNode children :  node.getChildrens()){
            if(level == 0){
                 HashMap<String, Object> childrenHashMap = new HashMap<String, Object>();
                childrenHashMap.put(NAME, children.getName());
                childrens.add(childrenHashMap);
            }
            else{
                HashMap<String, Object> childrenHashMap = this.getChildrenHashMap(children, level -1);
                childrens.add(childrenHashMap);
            }    
        }
        
        result.put(CHILDRENS, childrens);
        return result;
    }
    /*
    public String getTree(){
        RequirementNode root = this.productNodes.get(new Integer(0));
        //HashMap<String, Object> result = this.getCreateHashMapForNode(root);
        JSONObject json = new JSONObject(root);
        return json.toString();
    }
    
    private HashMap<String, Object> getCreateHashMapForNode(RequirementNode node){
        HashMap<String, Object> result =  new HashMap<String, Object>();
        result.put(NAME, node.getName());
        
        ArrayList<RequirementNode> childrens = node.getChildrens();
        if(childrens.isEmpty()){            
             result.put("SIZE", 1000); //TODO make price of node
             return result;
        }
        ArrayList<HashMap<String, Object>> childrenHashMap = new ArrayList<HashMap<String, Object>>();
        for(RequirementNode children :  childrens){
            HashMap<String, Object> recursion = this.getCreateHashMapForNode(children);
            childrenHashMap.add(recursion);
        }
        result.put(CHILDRENS, childrens);
        return null;
    }
    */
    /**
     *  Get date in string of all offers
     * 
     * @return 
     */
    public JSONArray getOffersDate(){
        ArrayList<String> result = new ArrayList<String>();
        ArrayList<Event> offers = this.productNodes.get(new Integer(0)).getOfferEvents();
        for(Event offer : offers){
            Date date = offer.getLast_modified();
            Format formatter = new SimpleDateFormat("HH:mm:ss");
            String stringDate = formatter.format(date);
            result.add(stringDate);
        }
        JSONArray jsonAraays= new JSONArray(result);
        return jsonAraays; 
    }
    
    /**
     *  get json Name of all children fol all ids
     * 
     * @return 
     */
    public JSONArray getChildrensNameOfAllId(){
       List<JSONArray> results = new ArrayList<JSONArray>();
       ArrayList<Integer> ids = this.getIds();
       for(Integer id : ids){
           ArrayList<String> childrens = this.getChildrensName(id);
            JSONArray jsonAraay = new JSONArray(childrens);
            results.add(jsonAraay);
        }
        JSONArray jsonAraays= new JSONArray(results);
        return jsonAraays; 
    }
    
    /**
     *  get json value of all children for all ids
     * 
     * @return 
     */
    public JSONArray getChildrensValueOfAllId(){
       List<JSONArray> results = new ArrayList<JSONArray>();
       ArrayList<Integer> ids = this.getIds();
       for(Integer id : ids){
           ArrayList<Double> childrens = this.getChildrensValue(id,-1);
            JSONArray jsonAraay = new JSONArray(childrens);
            results.add(jsonAraay);
        }
        JSONArray jsonAraays= new JSONArray(results);
        return jsonAraays; 
    }
    
    /**
     *  get numeric value of all direct children
     * 
     * @param id
     * @param idOffer
     * @return 
     */
    public ArrayList<Double> getChildrensValue(Integer id, int idOffer){
        ArrayList<Double> results = new ArrayList<Double>();
        RequirementNode node = this.productNodes.get(new Integer(id));
        ArrayList<RequirementNode> allChildrens = node.getChildrens();
        for(RequirementNode children :  allChildrens){
            if(idOffer == -1){ // then last
                idOffer = children.getOfferEvents().size() - 1;
            }
            Double value = children.getOfferEvents().get(idOffer).getNumeric_value().doubleValue();
            results.add(value);
        }
        return results;
    }
    
    public ArrayList<String> getChildrensName(Integer id){
        ArrayList<String> results = new ArrayList<String>();
        RequirementNode node = this.productNodes.get(new Integer(id));
        ArrayList<RequirementNode> allChildrens = node.getChildrens();
        for(RequirementNode children :  allChildrens){
            String value = children.getName();
            results.add(value);
        }
        return results;
    }
    
    public JSONArray  getHistoryOfOfferForNodes(){
        List<JSONArray> results = new ArrayList<JSONArray>();
        ArrayList<Integer> ids =  this.getIds(); 
        for(Integer id : ids){
            List<Event> events = this.productNodes.get(new Integer(id)).getOfferEvents();
            List<Double> offers = new ArrayList<Double>();
            if(id == null){
                id = new Integer(0); // Default TOTAl value -> id = 0
            }
            for(Event event : events){
                offers.add(event.getNumeric_value().doubleValue());
            }
  
            JSONArray jsonAraay = new JSONArray(offers);
            results.add(jsonAraay);
        }
        JSONArray jsonAraays= new JSONArray(results);
        
        return jsonAraays;
    }
    
    public ArrayList<RequirementNode> getNodes(){
        ArrayList<RequirementNode> nodes = new ArrayList<RequirementNode>();
        ArrayList<Integer> ids =  this.getIds(); 
        for(Integer id : ids){
            RequirementNode node = this.productNodes.get(id);
            nodes.add(node);
        }
        return nodes;
    }
    
    public ArrayList<String>  getNameOfNode(){
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<Integer> ids =  this.getIds(); 
        for(Integer id : ids){
            RequirementNode node = this.productNodes.get(id);
            names.add(node.getName());
        }
        return names;
    }
    
    public JSONArray  getJSNameOfNode(){
        JSONArray jsonAraays= new JSONArray(this.getNameOfNode());        
        return jsonAraays;
    }
    
    public Collection<RequirementNode> getAllProducts(){
        Collection<RequirementNode>  result = this.productNodes.values() ;
        return result;
        //ArrayList<String> result = new ArrayList<String>();
        //for(RequirementNode node : this.productNodes.values()){
        //    result.add(node.getName());
        //}
        //return result;   
    }
    
    private String toJavascriptArray(ArrayList<String> arrL){
        //String[] arr = (String[]) arrL.toArray(); 
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        
        for(int i=0; i<arrL.size(); i++){
            sb.append("\"").append(arrL.get(i)).append("\"");
            if(i+1 < arrL.size()){
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }
    
    public HashMap<Integer, RequirementNode> getProductNodes(){
        if(this.productNodes == null){
            this.productNodes = this.auctionService.getAuctionHashMap();
        }
        
        return this.productNodes;
    }
    
    public List<UiAuction> getAuctionList() {
        Paging paging = new Paging();
        AuctionFilter filter = new AuctionFilter();

        PaginatedList<Auction> auctionList = auctionService.getFilteredAuctions(filter, paging);
        uiAuctions = new ArrayList<UiAuction>(auctionList.getData().size());
        for (Auction auction : auctionList.getData()) {
            UiAuction uiAuction = new UiAuction(auction);
            uiAuctions.add(uiAuction);
        }
        return uiAuctions;
    }
    
    public String prepareOpeningLog(){
        auctionLogBean.setAuction(auction);
        return "showAuctionLog";
    }

    
    public void setScrollerPage(int scrollerPage) {
        this.scrollerPage = scrollerPage;
    }

    public int getScrollerPage() {
        return scrollerPage;
    }


    public void setAuctionLogBean(AuctionLogBean auctionLogBean) {
        this.auctionLogBean = auctionLogBean;
    }

    public AuctionLogBean getAuctionLogBean() {
        return auctionLogBean;
    }

    public void selectionChanged(ActionEvent event) {
        UIComponent comp = event.getComponent();
        Object obj = comp.getParent();
        if (obj instanceof HtmlDataTable) {
            Object rowData = table.getRowData();
            if (rowData instanceof Auction) {
                Auction selObj = (Auction) rowData;
                auction = selObj;
            }
        }
    }
    
    public Auction getAuction(){
        return auction;
    }
    
    public void setTable(UIDataTable table) {
        this.table = table;
    }

    public UIDataTable getTable() {
        return table;
    }

}
