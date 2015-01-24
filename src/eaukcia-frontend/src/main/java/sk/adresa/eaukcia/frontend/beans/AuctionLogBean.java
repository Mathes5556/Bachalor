package sk.adresa.eaukcia.frontend.beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import org.richfaces.component.html.HtmlDatascroller;
import sk.adresa.eaukcia.core.data.Auction;
import sk.adresa.eaukcia.core.data.AuctionEvent;
import sk.adresa.eaukcia.core.query.Paging;
import sk.adresa.eaukcia.core.data.Constant;
import sk.adresa.eaukcia.core.data.filter.AuctionEventFilter;
import sk.adresa.eaukcia.core.service.AuctionLogService;
import sk.adresa.eaukcia.core.service.ConstantsService;
import sk.adresa.eaukcia.frontend.resources.ResourceReader;
import sk.adresa.eaukcia.frontend.uidata.UiAuctionEvent;

public class AuctionLogBean {

    private List<String> selectedActions = new LinkedList<String>();
    private List<String> selectedUsers = new LinkedList<String>();
    private Date timeFrom, timeTo;
    private String targetUser;
    private Auction auction;
    private Boolean autoUpdate = true;
    private int round = 0;
    private AuctionLogService auctionLogService;
    private ConstantsService constantsService;
    static Logger logger = Logger.getLogger(AuctionLogBean.class);
    private int pageIndex;
    List<UiAuctionEvent> auctionLogList = null;
    private HtmlDatascroller dataScroller;

    public AuctionLogBean() {
    }

    public void setAuctionLogService(AuctionLogService auctionLogService) {
        this.auctionLogService = auctionLogService;
    }

    public void setConstantsService(ConstantsService constantsService) {
        this.constantsService = constantsService;
    }

    public List getAuctionActions() {
        List<SelectItem> typeList = new ArrayList<SelectItem>();
        Constant[] types = constantsService.getAuctionActions();
        for (Constant c : types) {
            typeList.add(new SelectItem(c.getId(), ResourceReader.getTranslation("action_" + c.getId())));
        }
        typeList.add(new SelectItem("chat", ResourceReader.getTranslation("action_chat")));
        return typeList;
    }

    

    

    private List<UiAuctionEvent> getAuctionLogListInternal() {
        Paging paging = new Paging();

        AuctionEventFilter filter = new AuctionEventFilter();
        filter.setActions(selectedActions);
        filter.setAuctions(Arrays.asList(new Integer[]{auction.getId()}));
        filter.setTimeFrom(timeFrom);
        filter.setTimeTo(timeTo);
        if (selectedUsers.size() > 0) {
            filter.setUsers(selectedUsers);
        }
        if (targetUser != null && !targetUser.equals("")) {
            filter.setTargetUserId(targetUser);
        }

        if (round != 0) {
            filter.setRounds(Arrays.asList(new Integer[]{round}));
        }


        ArrayList<AuctionEvent> logs = new ArrayList<AuctionEvent>();
        logs.addAll(auctionLogService.getFilteredAuctionLogs(filter, paging).getData());

        List<UiAuctionEvent> result = new ArrayList<UiAuctionEvent>(logs.size());
        for (AuctionEvent event : logs) {
            try{
                String[][] params = event.getJSONValues();
                StringBuilder paramsString = new StringBuilder();
                for (int j = 0; j < params.length; j++) {
                    
                    paramsString.append(ResourceReader.getTranslation(params[j][0]));
                    paramsString.append(":\"");


                    if (params[j][2] == null) {
                        paramsString.append(params[j][1]);

                    } else {
                        paramsString.append(ResourceReader.getTranslation(params[j][1]));
                    }
                    paramsString.append("\";");
                }
                UiAuctionEvent uiEvent = new UiAuctionEvent(event);
                uiEvent.setUiValue(paramsString.toString());
                result.add(uiEvent);
            } catch (Exception e){
                logger.error(e);
            }
        }


        return result;

    }

    public void resetFilter() {
        selectedActions = new LinkedList<String>();
        selectedActions.add("connectCurrentUserToAuction");
        selectedActions.add("loginUser");
        selectedActions.add("logoutUser");
        selectedUsers = new LinkedList<String>();
        targetUser = null;
        timeFrom = null;
        timeTo = null;
        round = 0;
        autoUpdate = false;
        updateList();

    }

    public String updateList() {
        auctionLogList = getAuctionLogListInternal();
        //pageIndex = dataScroller.getPageCount();
        return null;
    }
    
    public String getForUpdateList(){
        return updateList();
    }

    public void setSelectedActions(List<String> selectedActions) {
        this.selectedActions = selectedActions;
    }

    public List<String> getSelectedActions() {
        return selectedActions;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }

    public Auction getAuction() {
        return auction;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int getRound() {
        return round;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setTimeFrom(Date timeFrom) {
        this.timeFrom = timeFrom;
    }

    public Date getTimeFrom() {
        return timeFrom;
    }

    public void setTimeTo(Date timeTo) {
        this.timeTo = timeTo;
    }

    public Date getTimeTo() {
        return timeTo;
    }

    public void setTargetUser(String login) {
        this.targetUser = login;
    }

    public String getTargetUser() {
        return targetUser;
    }

    public List<UiAuctionEvent> getAuctionLogList() {
        return auctionLogList;
    }

    /**
     * @return the autoUpdate
     */
    public Boolean getAutoUpdate() {
        return autoUpdate;
    }

    /**
     * @param autoUpdate the autoUpdate to set
     */
    public void setAutoUpdate(Boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }

    /**
     * @return the dataScroller
     */
    public HtmlDatascroller getDataScroller() {
        return dataScroller;
    }

    /**
     * @param dataScroller the dataScroller to set
     */
    public void setDataScroller(HtmlDatascroller dataScroller) {
        this.dataScroller = dataScroller;
    }
}
