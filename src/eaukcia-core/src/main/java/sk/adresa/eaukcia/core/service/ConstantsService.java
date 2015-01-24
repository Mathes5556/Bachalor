/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.adresa.eaukcia.core.service;

import sk.adresa.eaukcia.core.data.Constant;

/**
 *
 * @author juraj
 */
public interface ConstantsService {
    
    public Constant<String>[] getAuctionActions();
    public Constant<String> getAuctionAction(String id);

}
