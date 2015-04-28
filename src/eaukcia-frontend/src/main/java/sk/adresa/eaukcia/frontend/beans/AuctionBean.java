package sk.adresa.eaukcia.frontend.beans;


import com.google.gson.Gson;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.sun.org.apache.bcel.internal.generic.AALOAD;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javassist.bytecode.analysis.Util;
import javassist.bytecode.stackmap.BasicBlock;
import javax.annotation.PostConstruct;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.swing.text.Position;
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
import sk.adresa.eaukcia.core.data.NodeInAuction;
import sk.adresa.eaukcia.core.data.PredictedNode;
import sk.adresa.eaukcia.core.data.Prediction;
import sk.adresa.eaukcia.core.data.RequirementNode;
import sk.adresa.eaukcia.core.data.User;
import sk.adresa.eaukcia.core.query.Paging;
import sk.adresa.eaukcia.core.data.filter.AuctionFilter;
import sk.adresa.eaukcia.core.query.PaginatedList;
import sk.adresa.eaukcia.core.service.AuctionService;
import sk.adresa.eaukcia.core.service.impl.AllAuctionData;
import sk.adresa.eaukcia.core.service.impl.AnalyzePositionVsPrice;
import sk.adresa.eaukcia.core.service.impl.Positions;
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
    
    Positions positions;
            
    private ArrayList<String> usersBid = new ArrayList<String>();
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
    
    public final static boolean LOAD_POSITIONS_AGAIN = true;
    
    public Positions getPositionsObject(){
        Positions positionsObject = Positions.readPosition();
        if(positionsObject != null && !LOAD_POSITIONS_AGAIN){
            return positionsObject;
        }
        Positions newPositionsObject = new Positions();
        return newPositionsObject;
    }
    
    @PostConstruct
    public void initBean() throws IOException{
        this.importBackUp();
        //GeoApiContext context = new GeoApiContext().setApiKey("AIzaSyB79vo28bMjobR2MyJb_DRtjh4u0KPIRxE");
            
        this.positions = this.getPositionsObject();
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
            //set parent's bid
            for (HashMap<Integer,BidForItem> bidsInOneOffer : allBids){
                for(BidForItem bid : bidsInOneOffer.values()){
                    bid.setAllBidsInOffer(bidsInOneOffer);
                }
            }
            
            this.users = this.auctionService.getUsers();
            //doplnim requirment nody o bidy 
            int orderOfBid = 1;
            for (HashMap<Integer,BidForItem> bidsInOneOffer : allBids){
                
                for(Integer id : bidsInOneOffer.keySet()){
                    //we know two types of bid - change only root - that's mean multiply whole
                    // tree by any inex or change a few item in tree
                    //TODO not only root but also parent of items..?
                    if(orderOfBid != 1 && bidsInOneOffer.values().size() == 1 && id.equals(new  Integer(0))){ //was changed only root of    tree
                       BigDecimal ratioOfChangingOfTree = bidsInOneOffer.get(id).getNumeric_value().divide(
                                                            new BigDecimal(this.productNodes.get(id).getLastOffer()),
                                                            3,
                                                            RoundingMode.HALF_UP
                                                          );
                       BidForItem bid = bidsInOneOffer.get(id);
                       for(RequirementNode node : this.productNodes.values()){ 
                           if(node.getOfferEvents().size() > 1){ // if was already any bid for product
                               BigDecimal newValue = ratioOfChangingOfTree.multiply(
                                                        new BigDecimal(node.getLastOffer())
                                                     );
                               BidEvent newEvent = new BidEvent(node.getItem_id(), newValue, bid.getFk_user(), bid.getTime());
                               newEvent.setAllBidsInOffer(bid.getAllBidsInOffer()); 
                               node.addOfferEvent(newEvent);
                           }
                       }
                       
                    }
                    else{
                        RequirementNode node = this.productNodes.get(id);
                        BidForItem bid = bidsInOneOffer.get(id);
                        BidEvent newEvent = new BidEvent(bid.getItem_id(), bid.getNumeric_value(), bid.getFk_user(), bid.getTime());
                        newEvent.setAllBidsInOffer(bid.getAllBidsInOffer()); 
                        node.addOfferEvent(newEvent);
                    }
                    orderOfBid++;   
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
            ///home/mathes/glassfish-3.1.2/glassfish/domains/domain1/
            this.auctions.add(createdAuction);
        }
        
       //ArrayList<File>  filesToSerialize = this.getAllSerializeFiles();
       //this.auctions = loadAuctionDatas(filesToSerialize);
       // this.loadAllAuctions2();
        
        AnalyzePositionVsPrice a = new AnalyzePositionVsPrice(this.positions, this.auctions);
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
    
    private void setAuction(AuctionData auction){
        this.productNodes = auction.getProductNodes();
        this.users = auction.getUsers();
        this.productsCodeMap = auction.getProductsCodeMap();
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
        //usr/bin/pg_restore 
        //--host localhost 
        //--port 5432 
        //--username "postgres"
        //--dbname "prosale" 
        //--no-password 
        //--clean 
        //--schema public 
        //--verbose
        //"/home/mathes/Documents/Bakalarka/PB/ma_poAukcii.backup"
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
    
    public boolean isVisualizationOfAuctionMode(){
        return this.applicationMode == applicationMode.TREE_VISUALIZATION_OF_AUCTION;
    }
    
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
    
    public String setVisualizationMode(){
        this.applicationMode = ApplicationMode.TREE_VISUALIZATION_OF_AUCTION;
        return "changeMode";
    }
   
    public String setUserComparisonMode(){
        this.applicationMode = ApplicationMode.USERS_COMPARISON;
        return "changeMode";
    }
    
    public boolean isSelectedAnyAuction(){
        return this.getSelectedAuctions().size() > 0;
    }
    
    public boolean isSelectedAnyCode(){
        return this.getSelectedCodes().size() > 0;
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
                    if(selectedNode ==null) continue;
                    result.add(selectedNode.getName());
            }
            if(result.isEmpty()) { //ad
                result.add(this.productNodes.get(new Integer(0)).getName());
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
        return new JSONArray(this.getSelectedUsers());
    }
    
    public ArrayList<String> getSelectedCodes(){
        ArrayList<String> result = new ArrayList<String> ();
        String value =  this.getParamValue(ID);
        //if(value == null){
        //    result.add(new Integer(0));
        //    return result;
        //}
        if(value == null || value == ""){
            return result;
        }
        if(value.contains(",")){  //is there more ids
            String[] ids = value.split(",");
            for (int i = 0; i < ids.length; i++) {
                result.add(ids[i]);
            }
        }
        else{
            String oneId = value == null ? null : new String(value);
            result.add(oneId);
        }
        
        return result;        
    }
    
    public JSONArray getSelectedCodesJSON(){
        return new JSONArray(this.getSelectedCodes());
    }
    
    final public static String NAME = "name";
    
    final public static String CHILDRENS = "children";
    
    final public static int MAX_LEVEL_FOR_TREE = 20;
    
    /**
     *  get hierarchy JSON object for visualization into tree
     * 
     * @param rootID
     * @return 
     */
    public String getHierarchyForTree(){
        //only for one auction and one node
        HashMap<String, Object> result =  new HashMap<String, Object>(); 
        ArrayList<String> selectedCodes = this.getSelectedCodes();
        ArrayList<AuctionData> selectedAuctions = this.getSelectedAuctionsObjects();
        AuctionData selectedAuction = null;
        if(selectedAuctions.isEmpty()){
            return new JSONObject(result).toString();
        }
        else{ // set appropiate auction - only 
            selectedAuction= selectedAuctions.get(0);
            if(selectedAuction != null){
                this.setAuction(selectedAuction);
            }

            RequirementNode node;
            if(selectedCodes.isEmpty()){
                node = selectedAuction.getProductNodes().get(0);
            }
            else{
                String code = selectedCodes.get(0);
                node = selectedAuction.getProductsCodeMap().get(code);
            }
            //Double bestValue = node.getLastOffer();
            result.put(NAME, node.getName());
            ArrayList<HashMap<String, Object>> childrens = new ArrayList<HashMap<String, Object>>();
            for(RequirementNode children :  node.getChildrens()){
                HashMap<String, Object> childrenHashMap = this.getChildrenHashMap(children, MAX_LEVEL_FOR_TREE);   
                childrens.add(childrenHashMap);
            }
            result.put(CHILDRENS, childrens);
            JSONObject json = new JSONObject(result);
            return json.toString();
        }
    }
    
    private HashMap<String, Object> getChildrenHashMap(RequirementNode node, int level){
        HashMap<String, Object> result =  new HashMap<String, Object>();
        //result.put(NAME, node.getName() + "((" + node.getCode() + "))");
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
       ArrayList<String> ids = this.getSelectedCodes();
       for(String id : ids){
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
       ArrayList<String> ids = this.getSelectedCodes();
       for(String id : ids){
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
    public ArrayList<Double> getChildrensValue(String id, int idOffer){
        ArrayList<Double> results = new ArrayList<Double>();
        RequirementNode node = this.productsCodeMap.get(id);
        if(node == null) return results;
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
    
    public ArrayList<String> getChildrensName(String id){
        ArrayList<String> results = new ArrayList<String>();
        RequirementNode node = this.productsCodeMap.get(id);
        if(node == null) return results;
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
    
     /** TODO refactor this to something like DateUtil
     * Get a diff between two dates
     * @param date1 the oldest date
     * @param date2 the newest date
     * @param timeUnit the unit in which you want the diff
     * @return the diff value, in the provided unit
     */
    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return diffInMillies;
        //return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }
    
    public JSONArray  getHistoryOfOfferForNodes(){
       // kazdy JSONArray jedna krivka 
        List<JSONArray> results = new ArrayList<JSONArray>();      
        ArrayList<String> selectedAuctions = this.getSelectedAuctions();
        boolean allAuctions = isSelectedAllAuctions(selectedAuctions);
        usersBid = new ArrayList<String>();
        if(selectedAuctions.isEmpty()){
            //TODO show root node of auction
            return new JSONArray();
        }
        
        if(selectedAuctions.size() > 1 || allAuctions){ // there is more then one auctions selected by user
            ArrayList<String> codes =  this.getSelectedCodes();
            if(codes.isEmpty()){
                codes.add("root");
                //TODO in real app return empty
                //return new JSONArray(results); //empty list
            }
            String selectedCode = codes.get(0);
            ArrayList<RequirementNode> nodesForCode = new ArrayList<RequirementNode>(); 
            //find right node -- TODO is this used somehwere - delete?
            //for(AuctionData auction : this.auctions){
              //  RequirementNode node = auction.getProductsCodeMap().get(selectedCode);
               // if(node != null){
                 //   continue;
                //}
            //}
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
            //we have to add one point in time from which will start all auction
            Date startDateOfAuctions = null;
            AuctionData auctionWhichIsStarted = null;
            for(AuctionData auctionObject : selectedAuctionObjects){
                if(startDateOfAuctions == null){
                    //TODO refactor it to AuctionData !! 
                    startDateOfAuctions = auctionObject.getProductNodes().get(new Integer(0)).getOfferEvents().get(0).getLast_modified();
                    startDateOfAuctions = auctionObject.getDateOfFirstOffer();
                    auctionWhichIsStarted = auctionObject;
                } 
                else{
                    break;
                }
            }
            ArrayList<AuctionData> usedAuctionForNode = new ArrayList<AuctionData>();
            // find curve for all node in all auctions
            for(AuctionData auctionObject : selectedAuctionObjects){
                RequirementNode node = null;
                if(selectedCode.equals("root")){
                    node = auctionObject.getProductNodes().get(new Integer(0));
                }
                else{
                    node = auctionObject.getProductsCodeMap().get(selectedCode);
                }
                if(node == null) {
                    continue;
                }
                ArrayList<Event> events = node.getOfferEvents();
                if(!events.isEmpty()){
                    usedAuctionForNode.add(auctionObject);
                }
                List<JSONArray> offers = new ArrayList<JSONArray>();
                //we add events without time!
                for(Event event : events){
                    if(event.getNumeric_value().equals(BigDecimal.ZERO)) continue;
                    ArrayList<Object> point = new ArrayList<Object>();
                    Long dateToJavascript = null;
                    if(auctionObject.equals(auctionWhichIsStarted)){
                         dateToJavascript = event.getLast_modified().getTime();
                    }
                    else{
                        // here we have to relative time if it's not auctionWhichIsStarted
                        //that means how long is this eent from start of auction and 
                        //that add to start of auctionWhichIsStarted
                        long timeAfterStart = this.getDateDiff(auctionObject.getDateOfFirstOffer(), event.getLast_modified(), TimeUnit.MILLISECONDS);
                        dateToJavascript = new Date(startDateOfAuctions.getTime() + timeAfterStart).getTime();
                    }
                    Double value = event.getNumeric_value().doubleValue();
                    String byUser = event.getFk_user();
                    point.add(dateToJavascript);
                    point.add(value);
                    point.add(byUser);
                    HashMap<String, BigDecimal> fullOffer = this.getFullOfferForBid(event);
                    JSONArray pointJS = new JSONArray(point);
                    pointJS.put(fullOffer);
                    offers.add(pointJS);
                }
                JSONArray jsonAraay = new JSONArray(offers);
                results.add(jsonAraay); // jedna krivka
            }
        }
        else{ // only one auction
            //set selected auction
            this.setAuction(selectedAuctions.get(0));
            
            ArrayList<String> codes =  this.getSelectedCodes(); //selected id by TODO change to codes
            ArrayList<String> selectedUsers = this.getSelectedUsers();  //selected users from URL
            if(codes.isEmpty()){
                //add root node
                codes.add("root");
            }
            for(String code : codes){ //
                if(selectedUsers.contains(ALL_USERS)){  // if all_users also prdocuct vs product only       
                    RequirementNode nodeForCode =  this.productsCodeMap.get(code);
                    if(code.equals("root")){
                        nodeForCode = this.productNodes.get(new Integer(0));
                    }
                    if(nodeForCode == null) continue;
                    List<Event> events = nodeForCode.getOfferEvents();
                    List<JSONArray> offers = new ArrayList<JSONArray>();
                    if(code == null){
                        code = this.productNodes.get(0).getCode(); // Default TOTAl value -> id = 0
                    }
                    int order = 0;
                    for(Event event : events){
                        if(event.getNumeric_value().equals(BigDecimal.ZERO)) continue;
                        ArrayList<Object> point = new ArrayList<Object>();
                        Long dateToJavascript = event.getLast_modified().getTime();
                        Double value = event.getNumeric_value().doubleValue();
                        String byUser = event.getFk_user();
                        point.add(dateToJavascript);
                        point.add(value);
                        point.add(byUser);
                        HashMap<String, BigDecimal> fullOffer = this.getFullOfferForBid(event);
                        JSONArray pointJS = new JSONArray(point);
                        pointJS.put(fullOffer);
                        offers.add(pointJS);        
                    }
                    JSONArray jsonAraay = new JSONArray(offers);
                    results.add(jsonAraay); // one curve
                }
                else{ // if not all users also for each selected user AND each product 
                    for(String user : selectedUsers){
                        RequirementNode nodeForCode = this.productsCodeMap.get(code);
                        if(code.equals("root")){
                            nodeForCode = this.productNodes.get(new Integer(0));
                        }
                        if(nodeForCode == null) continue;
                        List<Event> events = nodeForCode.getOfferEvents();
                        List<JSONArray> offers = new ArrayList<JSONArray>();
                        for(Event event : events){
                            if(event.getFk_user() != null && event.getFk_user().equals(user)){
                                ArrayList<Object> point = new ArrayList<Object>();
                                Long dateToJavascript = event.getLast_modified().getTime();
                                Double value = event.getNumeric_value().doubleValue();
                                String byUser = event.getFk_user();
                                point.add(dateToJavascript);
                                point.add(value);
                                point.add(byUser);
                                JSONArray pointJS = new JSONArray(point);
                                HashMap<String, BigDecimal> fullOffer = this.getFullOfferForBid(event);
                                pointJS.put(fullOffer);
                                offers.add(pointJS);
                            }
                        }
                        JSONArray jsonAraay = new JSONArray(offers);
                        results.add(jsonAraay); //one curve
                    }
                }   
            }
        }
        
        
        JSONArray jsonAraays= new JSONArray(results);
        
        return jsonAraays;
    }
    
     public HashMap<String, BigDecimal> getFullOfferForBid(Event event){                        
        HashMap<String, BigDecimal> all = new HashMap<String, BigDecimal>();
        try {
            BidEvent bid = (BidEvent) event;
            HashMap<Integer,BidForItem> allBidsInOffer = bid.getAllBidsInOffer();

            for (Map.Entry<Integer, BidForItem> entry : allBidsInOffer.entrySet()) {
                Integer key = entry.getKey();
                String nameOfNode = this.productNodes.get(key).getName();
                BidForItem bidValue= entry.getValue();
                all.put(nameOfNode, bidValue.getNumeric_value());
            }
        } catch (Exception e) {
            //cant re-type from Event to BidEvent
        }
        return all;
    }
     
    /**
     * list of user appropiate to bid's above
     * @return 
     */
    public ArrayList<String> getUsersBid(){
        return usersBid;
    }
    
    public JSONArray getUsersBidJSON(){
        return new JSONArray(this.getUsersBid());
    }
    
    /**
     * get selected product nodes
     * @return 
     */
    public ArrayList<RequirementNode> getNodes(){
        ArrayList<RequirementNode> nodes = new ArrayList<RequirementNode>();
        ArrayList<String> ids =  this.getSelectedCodes(); 
        for(String id : ids){
            RequirementNode node = this.productsCodeMap.get(id);
            nodes.add(node);
        }
        return nodes;
    }
    
    public ArrayList<String>  getNameOfNode(){
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<String> ids =  this.getSelectedCodes(); 
        for(String  id: ids){
            RequirementNode node = this.productsCodeMap.get(id);
            if(node == null){
                names.add("");
            }
            else{
                names.add(node.getName());
            }
            
        }
        return names;
    }
    
    public JSONArray  getJSNameOfNode(){
        JSONArray jsonAraays= new JSONArray(this.getNameOfNode());        
        return jsonAraays;
    }
    
    public Collection<RequirementNode> getAllProducts(){
        Set<RequirementNode> result= new HashSet<RequirementNode>();
        ArrayList<AuctionData> selectedAuctions = this.getSelectedAuctionsObjects();
        for(AuctionData auction : selectedAuctions){
            Collection<RequirementNode>  productsForAuction = this.productsCodeMap.values();
            result.addAll(productsForAuction);
        }
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
    
    public ArrayList<PredictedNode> getPrediction(){
        ArrayList<PredictedNode> result = new ArrayList<PredictedNode>();
        //TODO only selected or all?
        ArrayList auctionData = this.auctions;
        for(String code : this.getSelectedCodes()){
            Prediction prediction = new Prediction(auctions, code);
            RequirementNode node = prediction.getNode();
            if(!node.getOfferEvents().isEmpty()){
                BigDecimal predictedValue = prediction.getPredictedPriceFromAverage();
                result.add(new PredictedNode(predictedValue, node));
            }
        }
        return result;
    }
    public ArrayList<ArrayList<NodeInAuction>> getHistoryForNodes(){
        ArrayList<ArrayList<NodeInAuction>> result = new ArrayList<ArrayList<NodeInAuction>>();
        for(String code : this.getSelectedCodes()){
            ArrayList<NodeInAuction> historyForNode = new ArrayList<NodeInAuction>();
            for(AuctionData auction : this.auctions){
                if(auction.getProductsCodeMap().get(code) != null){
                    RequirementNode node = auction.getProductsCodeMap().get(code);
                    NodeInAuction nodeInAuction = new NodeInAuction(auction, node);
                    historyForNode.add(nodeInAuction);
                }
            }
            result.add(historyForNode);
        }
        return result;
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
