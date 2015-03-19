package sk.adresa.eaukcia.frontend.beans;


import com.google.gson.Gson;
import com.sun.org.apache.bcel.internal.generic.AALOAD;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import java.util.Set;
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
import org.omg.CORBA.INTERNAL;

import org.richfaces.component.UIDataTable;
import org.richfaces.component.html.HtmlDataTable;
import org.richfaces.json.JSONArray;
import org.richfaces.util.CollectionsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;
import sk.adresa.eaukcia.core.dao.impl.AuctionDaoImpl;
import sk.adresa.eaukcia.core.dao.impl.AuctionLogDaoImpl;
import sk.adresa.eaukcia.core.data.ApplicationMode;
import sk.adresa.eaukcia.core.data.AuctionData;

import sk.adresa.eaukcia.core.data.Auction;
import sk.adresa.eaukcia.core.data.AuctionEvent;
import sk.adresa.eaukcia.core.data.BidEvent;
import sk.adresa.eaukcia.core.data.BidForItem;
import sk.adresa.eaukcia.core.data.Event;
import sk.adresa.eaukcia.core.data.RequirementNode;
import sk.adresa.eaukcia.core.data.User;
import sk.adresa.eaukcia.core.query.Paging;
import sk.adresa.eaukcia.core.data.filter.AuctionFilter;
import sk.adresa.eaukcia.core.query.PaginatedList;
import sk.adresa.eaukcia.core.service.AuctionService;
import sk.adresa.eaukcia.core.service.impl.AllAuctionData;
import sk.adresa.eaukcia.frontend.uidata.UiAuction;

@Component
public class AuctionBean {
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
    
    private AllAuctionData allAuctionData;
    
    private String auctionName;
    
    //todo arraylisty ktory si budy pamarat seected ids,auctions, users
    
    private ApplicationMode applicationMode = ApplicationMode.PRODUCTS_COMPARISON;
    
    static Logger logger = Logger.getLogger(AuctionBean.class);
    private AuctionLogBean auctionLogBean;
    private UIDataTable table;
    
    //vsetko nizsie bude v objekte Auction ktory budem serializovat 
    private HashMap<Integer, RequirementNode> productNodes; 
    private HashMap<String, User> users = new HashMap<String, User>();
    private HashMap<String, RequirementNode> productsCodeMap = new HashMap<String, RequirementNode>(); 
    
    private ArrayList<AuctionData> auctions = new ArrayList<AuctionData>();
    
    @PostConstruct
    public void initBean() throws IOException{
        this.auctions = this.allAuctionData.getAuctions();
        this.auctionName = this.auctionService.getNameOfAuction();
        if(this.wasAuctionAlreadyLoadedFromDb(this.auctionName)){
            //give first auction as selected TODO??
             AuctionData auctionExample = this.auctions.get(0);
             this.productNodes = auctionExample.getProductNodes();
             this.users = auctionExample.getUsers();
             this.productsCodeMap = auctionExample.getProductsCodeMap();
        }
        else{  // load data from DB and save it
            this.productNodes = this.auctionService.getAuctionHashMap();
            ArrayList<HashMap<Integer,BidForItem>> allBids = this.auctionService.getBids();
            this.users = this.auctionService.getUsers();
            //doplnim requirment nody o bidy 
            for (Map.Entry<Integer, RequirementNode> entry : productNodes.entrySet()) {
                Integer id = entry.getKey();
                RequirementNode requirementNode = entry.getValue();

                for (HashMap<Integer,BidForItem> bidsInOneOffer : allBids){
                    if(bidsInOneOffer.size() == 0 ){
                        continue;
                    }

                    if(bidsInOneOffer.get(id) == null){//v tomto bide nebol zlepseny dany requirementNode pouziem stary offer
                        //TODO vyriesit/ocekovat prvy bid
                        if(requirementNode.getOfferEvents().isEmpty()){
                            continue;
                        }
                        
                        Event lastOfferEvent = requirementNode.getOfferEvents().get(requirementNode.getOfferEvents().size()-1);
                        //root je vzdy dany 
                        BidForItem rootBid = bidsInOneOffer.get(new Integer(0));
                        if(rootBid != null){
                            String byUser = rootBid.getFk_user();
                            lastOfferEvent.setFk_user(byUser);
                        }
                        requirementNode.addOfferEvent(lastOfferEvent);
                    }
                    else{
                         BidForItem bid = bidsInOneOffer.get(id);
                         BidEvent newEvent = new BidEvent(bid.getItem_id(), bid.getNumeric_value(), bid.getFk_user(), bid.getTime());
                         requirementNode.addOfferEvent(newEvent);
                    }
                }
            }

            //make hashmap where code of product is key

            for (Map.Entry<Integer, RequirementNode> entry : productNodes.entrySet()) {
                RequirementNode requirementNode = entry.getValue();
                if(requirementNode != null && requirementNode.getCode() != null){
                    this.productsCodeMap.put(requirementNode.getCode().trim(), requirementNode);
                }    
            }
            //save srialized object to disk 
            AuctionData createdAuction = this.saveAuctionObject(this.auctionName);
            this.auctions.add(createdAuction);
        }
        
       //ArrayList<File>  filesToSerialize = this.getAllSerializeFiles();
       //this.auctions = loadAuctionDatas(filesToSerialize);
       // this.loadAllAuctions2();
        
        
        //this.importBackUp();
    }
    
    private AuctionData saveAuctionObject(String name){
        // save to whole Auction Object
        AuctionData wholeAuctionData = new AuctionData(name, this.productNodes, this.users, this.productsCodeMap);
        try {
            wholeAuctionData.writeObject();
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.out.print("Cant save serialization");
        }
        return wholeAuctionData;
    }
    
    /**
     * compare hash of names and if equal then set as one main auction objectsssss
     * @param auctionName 
     */
    private void setAuction(String auctionNameHash){
        Integer hashOfSelectedAuction = new Integer(auctionNameHash);
        for(AuctionData auction: this.auctions){
            
            if(new Integer(auction.getName().hashCode()).equals(hashOfSelectedAuction)){
                this.productNodes = auction.getProductNodes();
                this.users = auction.getUsers();
                this.productsCodeMap = auction.getProductsCodeMap();
            }
        }
    }
    
    public boolean wasAuctionAlreadyLoadedFromDb(String auctionName){
        for(AuctionData auction: this.auctions){
            if(auction.getName().equals(auctionName)){
                return true;
            }
        }
        return false;
    }
    
    public void importBackUp() throws IOException{
        Runtime r = Runtime.getRuntime();
        Process p;
        ProcessBuilder pb;
        r = Runtime.getRuntime();
        pb = new ProcessBuilder( 
            "/usr/lib/postgresql/9.1/bin/pg_restore",
            "--host", "localhost",
            "--port", "5432",
            "--username", "postgres",
            "--dbname", "prosale",
            "--role", "postgres",
            "--no-password",
            "--verbose",
           "/home/mathes/Downloads/eaukcia_sp.backup");
        pb.redirectErrorStream(true);
        p = pb.start();
        InputStream is = p.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String ll;
        while ((ll = br.readLine()) != null) {
         System.out.println(ll);
        }   
    }
    
    public void listener(AjaxBehaviorEvent event) {
        System.out.println("listener");
        String result = "called by " + event.getComponent().getClass().getName();
    }
    
    public void setAuctionService(AuctionService auctionService) {
        this.auctionService = auctionService;
    }
    
    final public static String ID = "id";
    
    final public static String USERS = "users";
    
    final public static String ALL_USERS = "all_users";
    
    final public static String AUCTIONS = "auctions";
    
    final public static String SELECTED_ITEM_CODE = "code";
    
    
    private String getParamValue(String value){
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String,String> getParams = context.getExternalContext().getRequestParameterMap();
        return getParams.get(value);
    }
    
    public String getSelectedCode(){
        return  this.getParamValue(SELECTED_ITEM_CODE);
    }
    
    public ApplicationMode getApplicationMode(){
        return this.applicationMode;
    }
    
    /**
     * user can see comaparison for MANY products, ONE auction, ONE USER  - PRODUCTS_COMPARISON
     * @return 
     */
    public boolean isProductComparisonMode(){
        return this.applicationMode == applicationMode.PRODUCTS_COMPARISON;
    }
    
    /**
     *  * user can see comaparison for ONE products, MANY auctions, ONE USER
     * @return 
     */
    public boolean isProducAuctiontComparisonMode(){
        return this.applicationMode == applicationMode.AUCTIONS_COMPARISON;
    }
    
    /**
     * @return 
     */
    public boolean isProducUserComparisonMode(){
        return this.applicationMode == applicationMode.USERS_COMPARISON;
    }
    
    public void setApplicationMode(ApplicationMode appMode){
        this.applicationMode = appMode;
    }
    
    public String setProductComparisonMode(){
        this.applicationMode = ApplicationMode.PRODUCTS_COMPARISON;
        return "changeMode";
    }
    
    public String setAuctionComparisonMode(){
        this.applicationMode = ApplicationMode.AUCTIONS_COMPARISON;
        return "changeMode";
    }
   
    public String setUserComparisonMode(){
        this.applicationMode = ApplicationMode.USERS_COMPARISON;
        return "changeMode";
    }
    
    private final static String MODE = "mode";
    
    public ApplicationMode getSelectedApplicationMode(){
        String mode =  this.getParamValue(MODE);
        ApplicationMode selectedMode =  ApplicationMode.valueOf(mode);
        this.setApplicationMode(selectedMode);
        return selectedMode;
    }
    
    /*
     * get hash of name of selected auctions
     */
    public ArrayList<String> getSelectedAuctions(){
        ArrayList<String> result = new ArrayList<String> ();
        String value =  this.getParamValue(AUCTIONS);
        if(value == null){ //if no auction was selected
            return result;
        }
        String[] auctions = value.split(",");
        for (int i = 0; i < auctions.length; i++) {
            result.add(auctions[i]);
        }
        return result;
    }
    
    public ArrayList<AuctionData> getSelectedAuctionsObjects(){
        ArrayList<AuctionData> result = new ArrayList<AuctionData>();
        for(String hashOfAuctionsName : this.getSelectedAuctions()){
            if(hashOfAuctionsName.equals(ALL_AUCTIONS)) break;
            for(AuctionData auction : this.auctions){
                if(new Integer(hashOfAuctionsName).equals(auction.getName().hashCode())){
                    result.add(auction);
                }
            }
        }
        return result; 
    }
    
    public JSONArray getLegendForChart(){
        ArrayList<String> result = new ArrayList<String>();
        ArrayList<String> selectedAuctions = this.getSelectedAuctions();
        ArrayList<String> selectedUsers = this.getSelectedUsers();
        if(selectedAuctions.size() > 1){ // auctions are in legend
            for(AuctionData auction : this.getSelectedAuctionsObjects()){
                result.add(auction.getName());
            }
        }
        else if(selectedUsers.size() > 1 ){ // users are in legend 
            result = selectedUsers;
        }
        else{ // products are in legend(for one user)
            for(RequirementNode selectedNode : this.getNodes()){
                result.add(selectedNode.getName());
            }
        }
        return new JSONArray(result);    
    }
    
    public ArrayList<AuctionData> getAuctions(){
        return this.auctions;
    }
    
    public JSONObject getSelectedAuctionsJSON(){
        Map<Integer, String> auctions = new HashMap<Integer, String>();
        for(AuctionData auction : this.getSelectedAuctionsObjects()){
            if(auction != null){
                auctions.put(new Integer(auction.getName().hashCode()), auction.getName());
            }
        }
        return new JSONObject(auctions);
    }
    
    public ArrayList<String> getSelectedUsers(){
        ArrayList<String> result = new ArrayList<String> ();
        String value =  this.getParamValue(USERS);
        if(value == null){ //if no user was selected
            result.add(ALL_USERS);
            return result;
        }
        String[] users = value.split(",");
        for (int i = 0; i < users.length; i++) {
            result.add(users[i]);
        }
        return result;
    }
    
    public JSONArray getSelectedUsersJSON(){
        return new JSONArray(this.getSelectedAuctions());
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
            if(children.getOfferEvents().isEmpty()) continue;
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
    
    public static final String ALL_AUCTIONS = "all_auctions";
    
    public boolean isSelectedAllAuctions(ArrayList<String> selectedAuctions){
        return selectedAuctions.size() == 1 && selectedAuctions.get(0).equals(ALL_AUCTIONS);
    }
    
    public JSONArray  getHistoryOfOfferForNodes(){
       // kazdy JSONArray jedna krivka 
        List<JSONArray> results = new ArrayList<JSONArray>();      
            ArrayList<String> selectedAuctions = this.getSelectedAuctions();
        boolean allAuctions = isSelectedAllAuctions(selectedAuctions);
        
        if(selectedAuctions.isEmpty()){
            return new JSONArray();
        }
        
        if(selectedAuctions.size() > 1 || allAuctions){ // there is more then one auctions selected by user
            String selectedCode = this.getSelectedCode(); 
            if(selectedCode == null){
                return null;
            }
            ArrayList<RequirementNode> nodesForCode = new ArrayList<RequirementNode>(); 
            //find right node
            for(AuctionData auction : this.auctions){
                RequirementNode node = auction.getProductsCodeMap().get(selectedCode);
                if(node != null){
                    continue;
                }
            }
            //get selected auction
            ArrayList<AuctionData> selectedAuctionObjects = new ArrayList<AuctionData>();
            if(allAuctions){
               selectedAuctionObjects = this.auctions; 
            }
            else{
                for(String auctionNameHash : selectedAuctions){
                    for(AuctionData auctionObject : this.auctions){
                        if(new Integer(auctionObject.getName().hashCode()).equals(new Integer(auctionNameHash))){
                            selectedAuctionObjects.add(auctionObject);
                        }
                    }
                }
            }
            // find curve for all node in all auctions
            for(AuctionData auctionObject : selectedAuctionObjects){
                RequirementNode node = auctionObject.getProductsCodeMap().get(selectedCode);
                if(node == null) {
                    continue;
                }
                ArrayList<Event> events = node.getOfferEvents();
                List<JSONArray> offers = new ArrayList<JSONArray>();
                //we add events without time!
                for(Event event : events){
                    //TODO add doblue value for one item!!
                    ArrayList<Double> point = new ArrayList<Double>();
                    Double value = event.getNumeric_value().doubleValue();
                    point.add(value);
                    JSONArray pointJS = new JSONArray(point);
                    offers.add(pointJS);
                }
                JSONArray jsonAraay = new JSONArray(offers);
                results.add(jsonAraay); // jedna krivka
            }
        }
        else{ // only one auction
            //set selected auction
            this.setAuction(selectedAuctions.get(0));
            ArrayList<Integer> ids =  this.getIds(); //selected id by 
            ArrayList<String> selectedUsers = this.getSelectedUsers();  //selected users from URL
            for(Integer id : ids){ //
                if(selectedUsers.contains(ALL_USERS)){  // if all_users also prdocuct vs product only
                     List<Event> events = this.productNodes.get(new Integer(id)).getOfferEvents();
                     List<JSONArray> offers = new ArrayList<JSONArray>();
                    if(id == null){
                        id = new Integer(0); // Default TOTAl value -> id = 0
                    }
                    for(Event event : events){
                        ArrayList<Object> point = new ArrayList<Object>();
                        Long dateToJavascript = event.getLast_modified().getTime();
                        Double value = event.getNumeric_value().doubleValue();
                        point.add(dateToJavascript);
                        point.add(value);
                        JSONArray pointJS = new JSONArray(point);
                        offers.add(pointJS);
                    }

                    JSONArray jsonAraay = new JSONArray(offers);
                    results.add(jsonAraay); // jedna krivka

                }
                else{ // if not all users also for each selected user AND each product 
                    for(String user : selectedUsers){
                        List<Event> events = this.productNodes.get(new Integer(id)).getOfferEvents();
                         List<JSONArray> offers = new ArrayList<JSONArray>();
                        if(id == null){
                            id = new Integer(0); // Default TOTAl value -> id = 0
                        }
                        for(Event event : events){
                            if(event.getFk_user() != null && event.getFk_user().equals(user)){
                                ArrayList<Object> point = new ArrayList<Object>();
                                Long dateToJavascript = event.getLast_modified().getTime();
                                Double value = event.getNumeric_value().doubleValue();
                                point.add(dateToJavascript);
                                point.add(value);
                                JSONArray pointJS = new JSONArray(point);
                                offers.add(pointJS);
                            }
                        }

                        JSONArray jsonAraay = new JSONArray(offers);
                        results.add(jsonAraay); // jedna krivka
                    }

                }

            }
        }
        
        
        JSONArray jsonAraays= new JSONArray(results);
        
        return jsonAraays;
    }
    
    /**
     * get selected product nodes
     * @return 
     */
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
        Collection<RequirementNode>  result = this.productNodes.values();
        return result; 
    }
    
    public Set<String> getAllUsers(){
        return this.users.keySet(); 
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
    
    
    public AllAuctionData getAllAuctionData() {
        return allAuctionData;
    }

    public void setAllAuctionData(AllAuctionData allAuctionData) {
        this.allAuctionData = allAuctionData;
    }
    
    
}
