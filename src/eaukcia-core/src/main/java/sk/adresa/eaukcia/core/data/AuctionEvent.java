package sk.adresa.eaukcia.core.data;

import java.util.Date;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import sk.adresa.eaukcia.core.util.Util;

public class AuctionEvent implements Comparable{
    private int id;
    private Auction auction;
    private User user;
    private String targetUserId;
    private String action,value,actionDescription;
    private Date time;
    private int round;
    
    
    @Override
    public String toString() {
        return 
        "\nObject AuctionEvent:{ " + 
        "\n  id:" + getId() +
        "\n  auction:" + Util.shiftSpaces(getAuction()) +
        "\n  user:" + Util.shiftSpaces(getUser())+
        "\n  action:"+getAction() +
        "\n  round:"+ getRound() +
        "\n  targetUserId:"+ getTargetUserId() +
        "\n  value:"+ getValue() +
        "\n  time:"+ getTime() +
        "\n}";
    }
    
    public AuctionEvent() {
    }
    
    public AuctionEvent(AuctionEvent template) {
        id = template.getId();
        auction = template.getAuction();
        user = template.getUser();
        targetUserId = template.getTargetUserId();
        action = template.getAction();
        value = template.getValue();
        actionDescription = template.getActionDescription();
        time = template.getTime();
        round = template.getRound();
    }
    
    
    
    public AuctionEvent(int auctionId, User user, String targetUserId, String action, Date time) {
        auction = new Auction(auctionId);
        this.targetUserId = targetUserId;
        this.user = user;
        this.action = action;
        this.time = time;
    }
    
    public AuctionEvent(int auctionId, int round, User user, String targetUserId, String action, Date time) {
        auction = new Auction(auctionId);
        this.targetUserId = targetUserId;
        this.round = round;
        this.user = user;
        this.action = action;
        this.time = time;
    }
    
    public AuctionEvent(int auctionId, User user, String targetUserId, String action, String value, Date time) {
        auction = new Auction(auctionId);
        this.targetUserId = targetUserId;
        this.user = user;
        this.action = action;
        this.value = value;
        this.time = time;
    }
    
    public AuctionEvent(int auctionId, int round, User user, String targetUserId, String action, String value, Date time) {
        auction = new Auction(auctionId);
        this.targetUserId = targetUserId;
        this.round = round;
        this.user = user;
        this.action = action;
        this.value = value;
        this.time = time;
    }
    
    public AuctionEvent(int auctionId, User user, String targetUserId, String action, LoggableObject loggedObject, Date time) {
        auction = new Auction(auctionId);
        this.targetUserId = targetUserId;
        this.user = user;
        this.action = action;
        this.value = loggedObject.toLogString();
        this.time = time;
    }
    
    public AuctionEvent(int auctionId, int round, User user, String targetUserId, String action, LoggableObject loggedObject, Date time) {
        auction = new Auction(auctionId);
        this.targetUserId = targetUserId;
        this.round = round;
        this.user = user;
        this.action = action;
        this.value = loggedObject.toLogString();
        this.time = time;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }

    public Auction getAuction() {
        return auction;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Date getTime() {
        return time;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int getRound() {
        return round;
    }

    public void setActionDescription(String actionDescription) {
        this.actionDescription = actionDescription;
    }

    public String getActionDescription() {
        return actionDescription;
    }

    public void setTargetUserId(String targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getTargetUserId() {
        return targetUserId;
    }

    @Override
    public int compareTo(Object t) {
        AuctionEvent ae = (AuctionEvent)t;
        if(getTime().getTime() < ae.getTime().getTime())
            return -1;
        else
            return 1;
    }
    
    public String[][] getJSONValues(){
        if(getAction().equals("addCriterionItemToCriterion")){
            String[][] result = new String[7][3];
            JSONObject jsonObj = (JSONObject)JSONSerializer.toJSON(value);
            result[0][0] = "logCriterionItemCriterionId";
            result[0][1] = jsonObj.get("criterionId").toString();
            result[1][0] = "logCriterionItemItemId";
            result[1][1] = jsonObj.get("itemId").toString();
            result[2][0] = "logCriterionItemCode";
            result[2][1] = jsonObj.get("code")==null?"":jsonObj.get("code").toString();
            result[3][0] = "logCriterionItemName";
            result[3][1] = jsonObj.get("name").toString();
            result[4][0] = "logCriterionItemAmount";
            result[4][1] = jsonObj.get("amount").toString();
            result[5][0] = "logCriterionItemUnitType";
            result[5][1] = jsonObj.get("unitType")==null?"":jsonObj.get("unitType").toString();
            result[6][0] = "logCriterionItemDescription";
            result[6][1] = jsonObj.get("description")==null?"":jsonObj.get("description").toString();
                    
            return result;
        } else if(getAction().equals("addCriterionToAuctionRound")){
            String[][] result = new String[14][3];
            JSONObject jsonObj = (JSONObject)JSONSerializer.toJSON(value);
            result[0][0] = "logCriterionIdInAuction";
            result[0][1] = jsonObj.get("idInAuction").toString();
            result[1][0] = "logCriterionNameInAuction";
            result[1][1] = jsonObj.get("nameInAuction").toString();
            result[2][0] = "logCriterionPriority";
            result[2][1] = jsonObj.get("priority").toString();
            result[3][0] = "logCriterionCriterionType";
            result[3][1] = jsonObj.get("criterionType").toString();
            result[3][2] = "";
            result[4][0] = "logCriterionListType";
            result[4][1] = jsonObj.get("listType").toString();
            result[5][0] = "logCriterionDirection";
            result[5][1] = jsonObj.get("direction").toString();
            result[6][0] = "logCriterionMinimalStep";
            result[6][1] = jsonObj.get("minimalStep").toString();
            result[7][0] = "logCriterionPriceType";
            result[7][1] = jsonObj.get("priceType").toString();    
            result[8][0] = "logCriterionComparisonValue";
            result[8][1] = jsonObj.get("comparisonValue").toString();
            result[9][0] = "logCriterionDescription";
            result[9][1] = jsonObj.get("description").toString();
            result[10][0] = "logCriterionOrderVisible";
            result[10][1] = jsonObj.get("orderVisible").toString();
            result[11][0] = "logCriterionItemUnification";
            result[11][1] = jsonObj.get("criterionItemUnification")==null?"":jsonObj.get("criterionItemUnification").toString();
            result[12][0] = "logCriterionMinAllowedValue";
            result[12][1] = jsonObj.get("minAllowedValue").toString();
            result[13][0] = "logCriterionMaxAllowedValue";
            result[13][1] = jsonObj.get("maxAllowedValue").toString();
            return result;
            
        } else if(getAction().equals("addUserToAuction")){
            String[][] result = new String[4][3];
            JSONObject jsonObj = (JSONObject)JSONSerializer.toJSON(value);
            result[0][0] = "logUserAuctionInfoAuctionRole";
            result[0][1] = jsonObj.get("auctionRole").toString();
            result[0][2] = "";
            result[1][0] = "logUserAuctionInfoAlias";
            result[1][1] = jsonObj.get("Alias") == null? "" : jsonObj.get("Alias").toString();
            result[2][0] = "logUserAuctionInfoActive";
            result[2][1] = jsonObj.get("active").toString();
            result[3][0] = "logUserAuctionInfoUserCoeficient";
            result[3][1] = jsonObj.get("userCoeficient").toString();
                    
            return result;
        } else if(getAction().equals("createAuction")){
            String[][] result = new String[10][3];
            JSONObject jsonObj = (JSONObject)JSONSerializer.toJSON(value);
            result[0][0] = "logAuctionAuctionType";
            result[0][1] = jsonObj.get("auctionType").toString();
            result[1][0] = "logAuctionName";
            result[1][1] = jsonObj.get("name").toString();
            result[2][0] = "logAuctionAuctionStatus";
            result[2][1] = jsonObj.get("auctionStatus").toString();
            result[2][2] = "";
            result[3][0] = "logAuctionCurrency";
            result[3][1] = jsonObj.get("currency").toString();
            result[4][0] = "logAuctionItemId";
            result[4][1] = ((JSONObject)jsonObj.get("item")).get("id").toString();
            result[5][0] = "logAuctionItemName";
            result[5][1] = (String)((JSONObject)jsonObj.get("item")).get("name");
            result[6][0] = "logAuctionItemCode";
            result[6][1] = (String)((JSONObject)jsonObj.get("item")).get("name");
            result[7][0] = "logAuctionProjectId";
            result[7][1] = ((JSONObject)jsonObj.get("project")).get("id").toString();
            result[8][0] = "logAuctionProjectName";
            result[8][1] = (String)((JSONObject)jsonObj.get("project")).get("name");
            result[9][0] = "logAuctionDescription";
            result[9][1] = jsonObj.get("description").toString();
            return result;
             
        } else if(getAction().equals("createAuctionRound")){
            String[][] result = new String[11][3];
            JSONObject jsonObj = (JSONObject)JSONSerializer.toJSON(value);
            result[0][0] = "logAuctionRoundRoundType";
            result[0][1] = jsonObj.get("roundType").toString();
            result[0][2] = "";
            result[1][0] = "logAuctionRoundCompareAlgorithm";
            result[1][1] = jsonObj.get("compareAlgorithm").toString();
            result[1][2] = "";
            result[2][0] = "logAuctionRoundBeginingTime";
            try{
                result[2][1] = Util.formatDate(new Date((Long)((JSONObject)jsonObj.get("beginingTime")).get("time")),"dd.MM.yyyy HH:mm");
            } catch(Exception e){
                result[2][1] = "";
            }
            result[3][0] = "logAuctionRoundEndTime";
            try{
                result[3][1] = Util.formatDate(new Date((Long)((JSONObject)jsonObj.get("endTime")).get("time")),"dd.MM.yyyy HH:mm:ss");
            } catch(Exception e){
                result[3][1] = "";
            }
            result[4][0] = "logAuctionRoundBestOfferVisible";
            result[4][1] = jsonObj.get("bestOfferVisible").toString();
            result[5][0] = "logAuctionRoundBestCriterionsVisible";
            result[5][1] = jsonObj.get("bestCriterionsVisible").toString();
            result[6][0] = "logAuctionRoundOrderVisible";
            result[6][1] = jsonObj.get("orderVisible").toString();
            result[7][0] = "logAuctionRoundRoundDescription";
            result[7][1] = jsonObj.get("roundDescription").toString();
            result[8][0] = "logAuctionRoundProlongTime";
            result[8][1] = ((Integer)(Integer.parseInt(jsonObj.get("prolongTime").toString()) / 1000)).toString() + " sek";
            result[9][0] = "logAuctionRoundVisibleToParticipants";
            result[9][1] = jsonObj.get("visibleToParticipants").toString();
            result[10][0] = "logAuctionRoundCriterionOrdersVisible";
            result[10][1] = jsonObj.get("criterionOrdersVisible").toString();
            return result;
        } else if(getAction().equals("removeCriterionFromAuctionRound")){
            String[][] result = new String[1][3];
            result[0][0] = "logCriterionIdInAuction";
            result[0][1] = value;
            return result;
        } else if(getAction().equals("removeCriterionItemFromCriterion")){
            String[][] result = new String[2][3];
            String[] values = value.split(";");
            result[0][0] = "logCriterionItemCriterionId";
            result[0][1] = values[0].trim();
            result[1][0] = "logCriterionItemItemId";
            result[1][1] = values[1].trim();
            return result;
        } else if(getAction().equals("setAuction")){
            String[][] result = new String[4][3];
            JSONObject jsonObj = (JSONObject)JSONSerializer.toJSON(value);
            result[0][0] = "logAuctionAuctionType";
            result[0][1] = jsonObj.get("auctionType").toString();
            result[1][0] = "logAuctionName";
            result[1][1] = jsonObj.get("name").toString();
            result[2][0] = "logAuctionAuctionStatus";
            result[2][1] = jsonObj.get("auctionStatus").toString();
            result[2][2] = "";
            result[3][0] = "logAuctionDescription";
            result[3][1] = jsonObj.get("description").toString();
            return result;
        } else if(getAction().equals("setAuctionRound")){
            String[][] result = new String[11][3];
            JSONObject jsonObj = (JSONObject)JSONSerializer.toJSON(value);
            result[0][0] = "logAuctionRoundRoundType";
            result[0][1] = jsonObj.get("roundType").toString();
            result[0][2] = "";
            result[1][0] = "logAuctionRoundCompareAlgorithm";
            result[1][1] = jsonObj.get("compareAlgorithm").toString();
            result[2][0] = "logAuctionRoundBeginingTime";
            try{
                result[2][1] = Util.formatDate(new Date((Long)((JSONObject)jsonObj.get("beginingTime")).get("time")),"dd.MM.yyyy HH:mm");
            } catch(Exception e){
                result[2][1] = "---";
            }
            result[3][0] = "logAuctionRoundEndTime";
            try{
                result[3][1] = Util.formatDate(new Date((Long)((JSONObject)jsonObj.get("endTime")).get("time")),"dd.MM.yyyy HH:mm:ss");
            } catch(Exception e){
                result[3][1] = "---";
            }
            result[4][0] = "logAuctionRoundBestOfferVisible";
            result[4][1] = jsonObj.get("bestOfferVisible").toString();
            result[5][0] = "logAuctionRoundBestCriterionsVisible";
            result[5][1] = jsonObj.get("bestCriterionsVisible").toString();
            result[6][0] = "logAuctionRoundOrderVisible";
            result[6][1] = jsonObj.get("orderVisible").toString();
            result[7][0] = "logAuctionRoundRoundDescription";
            result[7][1] = jsonObj.get("roundDescription").toString();
            result[8][0] = "logAuctionRoundProlongTime";
            result[8][1] = ((Integer)(Integer.parseInt(jsonObj.get("prolongTime").toString()) / 1000)).toString() + " sek";
            result[9][0] = "logAuctionRoundVisibleToParticipants";
            result[9][1] = jsonObj.get("visibleToParticipants").toString();
            result[10][0] = "logAuctionRoundCriterionOrdersVisible";
            result[10][1] = jsonObj.get("criterionOrdersVisible").toString();
            return result;
        } else if(getAction().equals("prolongAuctionRound")){
            String[][] result = new String[1][3];

            result[0][0] = "logAuctionRoundEndTime";
            try{
                result[0][1] = Util.formatDate(new Date(Long.parseLong(value)),"dd.MM.yyyy HH:mm:ss");
            } catch(Exception e){
                result[0][1] = "---";
            }
            
            return result;
        } else if(getAction().equals("setCriterionBestInputValue")){
            String[][] result = new String[3][3];
            JSONObject jsonObj = (JSONObject)JSONSerializer.toJSON(value);
            result[0][0] = "logCriterionIdInAuction";
            result[0][1] = jsonObj.get("idInAuction").toString();
            result[1][0] = "logCriterionNameInAuction";
            result[1][1] = jsonObj.get("nameInAuction").toString();
            result[2][0] = "logCriterionBestInputValue";
            result[2][1] = jsonObj.get("bestInputValue").toString();
            return result;
        } else if(getAction().equals("setCriterionForAuctionRound")){
            String[][] result = new String[11][3];
            JSONObject jsonObj = (JSONObject)JSONSerializer.toJSON(value);
            result[0][0] = "logCriterionIdInAuction";
            result[0][1] = jsonObj.get("idInAuction").toString();
            result[1][0] = "logCriterionNameInAuction";
            result[1][1] = jsonObj.get("nameInAuction").toString();
            result[2][0] = "logCriterionPriority";
            result[2][1] = jsonObj.get("priority").toString();
            result[3][0] = "logCriterionDirection";
            result[3][1] = jsonObj.get("direction").toString();
            result[4][0] = "logCriterionMinimalStep";
            result[4][1] = jsonObj.get("minimalStep").toString();
            result[5][0] = "logCriterionComparisonValue";
            result[5][1] = jsonObj.get("comparisonValue").toString();
            result[6][0] = "logCriterionDescription";
            result[6][1] = jsonObj.get("description").toString();
            result[7][0] = "logCriterionOrderVisible";
            result[7][1] = jsonObj.get("orderVisible").toString();
            result[8][0] = "logCriterionItemUnification";
            result[8][1] = jsonObj.get("criterionItemUnification")==null?"":jsonObj.get("criterionItemUnification").toString();
            result[9][0] = "logCriterionMinAllowedValue";
            result[9][1] = jsonObj.get("minAllowedValue")==null?"":jsonObj.get("minAllowedValue").toString();
            result[10][0] = "logCriterionMaxAllowedValue";
            result[10][1] = jsonObj.get("maxAllowedValue")==null?"":jsonObj.get("maxAllowedValue").toString();
            return result;
        } else if(getAction().equals("setCriterionItem")){
            String[][] result = new String[7][3];
            JSONObject jsonObj = (JSONObject)JSONSerializer.toJSON(value);
            result[0][0] = "logCriterionItemCriterionId";
            result[0][1] = jsonObj.get("criterionId").toString();
            result[1][0] = "logCriterionItemItemId";
            result[1][1] = jsonObj.get("itemId").toString();
            result[2][0] = "logCriterionItemCode";
            result[2][1] = jsonObj.get("code").toString();
            result[3][0] = "logCriterionItemName";
            result[3][1] = jsonObj.get("name").toString();
            result[4][0] = "logCriterionItemAmount";
            result[4][1] = jsonObj.get("amount").toString();
            result[5][0] = "logCriterionItemUnitType";
            result[5][1] = jsonObj.get("unitType").toString();
            result[6][0] = "logCriterionItemDescription";
            result[6][1] = jsonObj.get("description").toString();
                    
            return result;
            
        } else if(getAction().equals("setCriterionsForUser")){
            String[][] result = null;
            JSONObject jsonObj = (JSONObject)JSONSerializer.toJSON(value);
            JSONArray items = null;
            if((Boolean)(jsonObj.get("listType")) == false){
                result = new String[5][3];
            } else {
                items = (JSONArray)jsonObj.get("items");
                result = new String[5+items.size()*4][3];
            }
            result[0][0] = "logCriterionIdInAuction";
            result[0][1] = jsonObj.get("idInAuction").toString();
            result[1][0] = "logCriterionNameInAuction";
            result[1][1] = jsonObj.get("nameInAuction").toString();
            result[2][0] = "logCriterionCriterionType";
            result[2][1] = jsonObj.get("criterionType").toString();
            result[2][2] = "";
            result[3][0] = "logCriterionListType";
            result[3][1] = jsonObj.get("listType").toString();
            if (result[2][1].equals("NUMERIC") || result[2][1].equals("INTEGER") || result[2][1].equals("BOOLEAN")) {
                result[4][0] = "logCriterionNumericValue";
                if(jsonObj.get("numericValue") instanceof Integer)
                    result[4][1] = ((Integer)jsonObj.get("numericValue")).toString();
                else result[4][1] = String.format("%.4f", ((jsonObj.get("numericValue"))));
            } else if (result[2][1].equals("DATE")) {
                result[4][0] = "logCriterionDateValue";
                result[4][1] = Util.formatDate(new Date((Long)((JSONObject)jsonObj.get("dateValue")).get("time")), "dd.MM.yyyy");
            } else {
                result[4][0] = "logCriterionStringValue";
                result[4][1] = jsonObj.get("stringValue").toString();
            }
            if(result[3][1].equals("true")){
                int ix = 5;
                for(Object o:items){
                    JSONObject item = (JSONObject)o;
                    result[ix][0] = "logCriterionItemItemId";
                    result[ix++][1] = item.get("itemId").toString();
                    result[ix][0] = "logCriterionItemCode";
                    result[ix++][1] = item.get("code").toString();
                    result[ix][0] = "logCriterionItemName";
                    result[ix++][1] = item.get("name").toString();
                    result[ix][0] = "logCriterionItemValue";
                    if(item.get("value") instanceof Integer)
                        result[ix++][1] = ((Integer)item.get("value")).toString();
                    else result[ix++][1] = String.format("%.4f", (item.get("value")));
                }
            }
            return result;
            
        } else if(getAction().equals("setUserInAuction")){
            String[][] result = new String[4][3];
            JSONObject jsonObj = (JSONObject)JSONSerializer.toJSON(value);
            result[0][0] = "logUserAuctionInfoAuctionRole";
            result[0][1] = jsonObj.get("auctionRole").toString();
            result[0][2] = "";
            result[1][0] = "logUserAuctionInfoAlias";
            result[1][1] = jsonObj.get("alias") == null? "":jsonObj.get("alias").toString();
            result[2][0] = "logUserAuctionInfoActive";
            result[2][1] = jsonObj.get("active").toString();
            result[3][0] = "logUserAuctionInfoUserCoeficient";
            result[3][1] = jsonObj.get("userCoeficient").toString();
                    
            return result;
        } else if(getAction().equals("chat")){
            String[][] result = new String[3][3];
            JSONObject jsonObj = (JSONObject)JSONSerializer.toJSON(value);
            result[0][0] = "logChatTimeRecieved";
            try{
                result[0][1] = Util.formatDate(new Date((Long)((JSONObject)jsonObj.get("timeRecieved")).get("time")), "dd.MM.yyyy HH:mm:ss");
            } catch(Exception e){
                result[0][1] = "";
            }
            result[1][0] = "logChatMessageType";
            result[1][1] = jsonObj.get("messageType").toString();
            result[1][2] = "";
            result[2][0] = "logChatMessage";
            result[2][1] = jsonObj.get("message").toString();
            
            return result;
        } else {
            return new String[0][0];
        }
        
    }

}
