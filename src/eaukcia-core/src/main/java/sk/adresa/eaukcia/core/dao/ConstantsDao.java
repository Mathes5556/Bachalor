package sk.adresa.eaukcia.core.dao;


import sk.adresa.eaukcia.core.data.Constant;



public interface ConstantsDao {

    public Constant<String>[] getAuctionActions();
    public Constant<String> getAuctionAction(String id);
    
}
