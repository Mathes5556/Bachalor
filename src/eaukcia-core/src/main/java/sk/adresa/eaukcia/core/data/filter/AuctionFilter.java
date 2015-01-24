package sk.adresa.eaukcia.core.data.filter;

import java.util.List;

import sk.adresa.eaukcia.core.util.Util;


public class AuctionFilter {
    private String user;
    private String auctionType;
    private List<String> users;
    private List<Integer> projects;
    private List<Integer> clients;
    
    
    public AuctionFilter() {
    }
    
    @Override
    public String toString() {
        return 
        "\nObjekt AuctionFilter:{ " +    
        "\n  Uzivatel:"+ getUser()+
        "\n  AuctionType:"+ getAuctionType()+
        "\n  Uzivatelia:"+((users==null)?"null" : Util.listToString(users).toString().replaceAll("(\n *)","$1    ")) +
        "\n  Projekty:"+((projects==null)?"null" : Util.listToString(projects).toString().replaceAll("(\n *)","$1    ")) +
        "\n  Klienti:"+((clients==null)?"null" : Util.listToString(clients).toString().replaceAll("(\n *)","$1    ")) +
        "\n}";
    }


   

    public void setProjects(List<Integer> projects) {
        this.projects = projects;
    }

    public List<Integer> getProjects() {
        return projects;
    }

  

    public void setClients(List<Integer> clients) {
        this.clients = clients;
    }

    public List<Integer> getClients() {
        return clients;
    }


    public void setUsers(List<String> users) {
        this.users = users;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public String getAuctionType() {
        return auctionType;
    }

    public void setAuctionType(String auctionType) {
        this.auctionType = auctionType;
    }
    
    
}
