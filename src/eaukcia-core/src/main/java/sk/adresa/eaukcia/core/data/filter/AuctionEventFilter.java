package sk.adresa.eaukcia.core.data.filter;

import java.util.Date;
import java.util.List;

import sk.adresa.eaukcia.core.util.Util;


public class AuctionEventFilter {
    private Date timeFrom, timeTo;
    private List<String> users,actions;
    private List<Integer> auctions,rounds;
    private String targetUserId;
    
    public AuctionEventFilter() {
    }

    @Override
    public String toString() {
        return 
        "\nObjekt ProjectFilter:{ " + 
        "\n  Od:"+getTimeFrom() + 
        "\n  Do:"+getTimeTo() + 
        "\n  Cielovy uzivatel:"+getTargetUserId() + 
        "\n  Aukcie:"+((getAuctions()==null)?"null" : Util.listToString(getAuctions()).toString().replaceAll("(\n *)","$1    ")) +
        "\n  Uzivatelia:"+((getUsers()==null)?"null" : Util.listToString(getUsers()).toString().replaceAll("(\n *)","$1    ")) +
        "\n  Akcie:"+((getActions()==null)?"null" : Util.listToString(getActions()).toString().replaceAll("(\n *)","$1    ")) +
        "\n  Kola:"+((getRounds()==null)?"null" : Util.listToString(getActions()).toString().replaceAll("(\n *)","$1    ")) +
        "\n}";
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

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }

    public List<String> getActions() {
        return actions;
    }

    public void setAuctions(List<Integer> auctions) {
        this.auctions = auctions;
    }

    public List<Integer> getAuctions() {
        return auctions;
    }

    public void setRounds(List<Integer> rounds) {
        this.rounds = rounds;
    }

    public List<Integer> getRounds() {
        return rounds;
    }

    public void setTargetUserId(String targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getTargetUserId() {
        return targetUserId;
    }
}
